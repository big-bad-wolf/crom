(ns crom.adapter.hugsql
  (:require [honeysql.core :as sql]
            [crom.adapter :as adapter]
            [crom.model :as model]
            [crom.utils :as utils]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [crom.container :as container]))

;;; Private

(def ^:private pk-suffix "id")

(defn- canonical?
  [spec]
  (let [d-spec (s/describe spec)]
    (boolean
     (when (seq? d-spec)
       (or (utils/keys-spec? d-spec)
           (= :coll-of (keyword (first d-spec))))))))


(defn- spec->fields
  [spec]
  (if (utils/keys-spec? spec)
    (mapv utils/spec->name (utils/keys-specs spec))
    [(utils/spec->name spec)]))

(defn- canonical
  [spec]
  (when (utils/keys-spec? spec)
    (filter canonical? (utils/keys-specs spec))))

(defn- canonical-spec
  [spec]
  (when (canonical? spec)
    (if (utils/keys-spec? spec)
      spec
      (second (s/describe spec)))))

(defn- primary-key
  [{::model/keys [primary-key spec]}]
  (or primary-key
      (keyword (subs (str spec pk-suffix) 1))))

(defn- primary-key*
  [model]
  (-> (primary-key model)
      (str/replace "/" ".")
      (subs 1)
      keyword))


(defn- primary-key-column
  [model]
  (utils/spec->name (primary-key model)))


(defn- model-columns
  [model]
  (let [spec (model/spec model)
        canonical (set (canonical spec))
        primary-key (primary-key-column model)
        fields (utils/keys-specs spec)
        fields (filter #(not (contains? canonical %)) fields)]
        fields (filter #(not (contains? #{primary-key} %)) fields)
        (mapv utils/spec->name (conj fields primary-key))))


(defn- joins
  [container model]
  (let [spec (model/spec model)
        model-pk (primary-key* model)
        canonical (canonical spec)
        joins (mapv (fn [spec]
                      (let [c-spec (canonical-spec spec)
                            c-model (container/model container c-spec)
                            t-name (model/table c-model)]
                        [[t-name (utils/spec->name  c-spec)] [:= model-pk (primary-key* c-model)]]))
                   canonical)]
    (if (= 1 (count joins))
      (first joins)
      joins)))


;;; Public
(defn- -query
  [{:keys [container]}  model filters]

  (cond-> {:select (model-columns model)
                      :from [[(model/table model) (-> model model/spec utils/spec->name)]]}
    (not (empty? (canonical (model/spec model)))) (assoc :join (joins container model))
    true sql/format))

(defrecord HugSql [container]
  adapter/Query
  (query [this model filters]
    (-query this model filters)))


(defn make
  [container]
  (map->HugSql {:container container}))

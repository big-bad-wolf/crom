(ns crom.core
  (:require [clojure.spec.alpha :as s]
            [crom.model :as model]
            [crom.container :as container]
            [crom.repository :as repo]
            [crom.adapter.hugsql :as hugsql]
            [crom.utils :as utils]
            [clojure.string :as str])
  (:gen-class))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))




(s/def :product/name string?)
(s/def :product/code string?)
(s/def :product/product (s/keys :req [:product/name :product/code]))


(s/def :tenant/code string?)
(s/def :tenant/name string?)
(s/def :tenant/products (s/coll-of :product/product))
(s/def :tenant/tenant (s/keys :req [:tenant/code :tenant/name]
                              :opt [:tenant/products]))



(def container {::container/models [{::model/table :rel-tenant
                                     ::model/spec :tenant/tenant}

                                    {::model/table :rel-product
                                     ::model/spec :product/product}]})


(container/model container :product/product)


(def hugsql-adapter (hugsql/make container))
(def tenant-repo (repo/make container hugsql-adapter :tenant/tenant))

(repo/query tenant-repo [:= :tenant/name "test"])

(str/replace :a/b "/" ".")

(str (utils/spec->name :tenant/tenant) "id")
(conj '(:tenant/code :Tenant/name) :tenant/id)



;; (defprotocol Changeset
;;   (commit [this])
;;   (command [this]))

;; (defprotocol Model
;;   (model->map [this][this kw])
;;   (schema [this]))

;; (defprotocol Repository
;;   (query [this filters]))


;; (defprotocol Query
;;   (query [this filters]))


;; (defn spec->fields
;;   [spec]
;;   (let [description (s/describe spec)
;;         req-fields (nth description 2 [])
;;         opt-fields (nth description 4 [])]
;;     (mapv (comp keyword name)
;;           (into req-fields opt-fields))))


;; (defn get-model
;;   [{:crom/keys [models]} spec]
;;   (some #(when (= (:crom/spec %)) spec
;;                %) models))

;; (def spec->name (comp keyword name))

;; (get-model container :tenant/tenant)

;; (defn make-from
;;   [container seq-specs]
;;   (let [models (map #(get-model container %) (conj [] seq-specs))
;;         from (mapv (fn [{:crom/keys [table spec]}]
;;                      [table (spec->name spec)])
;;                    models)]
;;     (if (= 1 (count from))
;;       (first from)
;;       from)))

;; (defn canonical?
;;   [s]
;;   (when (seq? s)
;;     (let [k (keyword (first s))]
;;      (or (= :keys k)
;;          (= :coll-of k)))))

;; (defn get-tables
;;   [container spec]
;;   (let [s-kw [spec]
;;         spec-d (s/describe spec) ; walker
;;         canonical (filter canonical? spec-d)]
;;     (make-from container (into s-kw canonical))))

;; (= :keys (keyword (first (s/describe (s/keys :req [:tenant/name])))))
;; (s/describe :tenant/tenant)

;; (get-tables container  [:tenant/tenant])

;; #_(defn honey-query
;;   [{:keys [container spec models]} filters]
;;   (let [model (get-model container spec)
;;         models (set (map (comp keyword name) models))
;;         fields (filter #(not (contains? models ((comp keyword name) %))) (spec->fields spec))
;;         ]
;;     (cond-> {:select fields
;;              :from [(keyword (prefix "_" (name table)))]

;;              :where (first (mapv (fn [[a b c]]
;;                                    [a ((comp keyword name) b) c]) filters))}
;;       true sql/format)))




;; #_(honey-query {:prefix :trd_
;;               :table :tenant
;;               :spec :tenant/tenant
;;               :models [:tenant/products :product]}
;;              [[:= :tenant/name "rinco"]])

;; (defrecord HoneySQLAdapater []
;;   Query
;;   (query [this filters]))


;; (defrecord DefaultModel [schema]
;;   Model
;;   (model->map [this] nil)
;;   (model->map [this kw] nil)
;;   (schema [this] nil))

;; (defrecord DefaultRepository [model]
;;   Repository
;;   )

;; (def tenant-repo (->DefaultRepository (->DefaultModel :tenant/tenant)))

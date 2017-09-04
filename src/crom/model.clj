(ns crom.model
  (:require [clojure.spec.alpha :as s]
            [crom.utils :as utils]))


;;; Public

#_(defn make
  ([spec]
   (make spec (utils/spec->name spec)))
  ([spec table]
   (map->Model {:table table
                :spec spec})))

(defn spec
  [{::keys [spec]}]
  spec)

(defn table
  [{::keys [table spec]}]
  (or table
      (utils/spec->name spec)))

;;; Specs
(s/def ::primary-key keyword?)
(s/def ::table keyword?)
(s/def ::spec s/spec?)
(s/def :crom/model (s/keys :req [::primary-key ::table ::spec]))

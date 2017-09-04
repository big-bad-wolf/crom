(ns crom.repository
  (:require [clojure.spec.alpha :as s]
            [crom.adapter :as adapter]
            [crom.container :as container]))


(defprotocol IRepository
    (query [this filters]))

(defrecord Repository [container adapter model]
  IRepository
  (query [{:keys [container adapter model]} filters]
    (adapter/query adapter model filters)))

(defn make
  [container adapter spec]
  (map->Repository {:container container
                    :adapter adapter
                    :model (container/model container spec)}))

;;; spec
(s/def :crom/adapter any?)
(s/def :crom/repository (s/keys :req-un [:crom/container :crom/adapter :crom/model]))

(ns crom.adapter
  (:require [clojure.spec.alpha :as s]))

(defprotocol Query
  (query [this model filters]))

;;; spec
(s/def :crom/adapter (s/keys :req-un [:crom/container]))

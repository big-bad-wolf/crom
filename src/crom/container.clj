(ns crom.container
  (:require [clojure.spec.alpha :as s]
            [crom.model :as  model]))

(defn model
  [{::keys [models]} spec]
  (some #(when (= (model/spec %) spec)
               %) models))

;;; Specs
(s/def ::models (s/coll-of :crom/models))
(s/def :crom/container (s/keys :req [::models]))


(s/fdef model
        :args (s/cat :container :crom/container)
        :ret (s/nilable :crom/model))

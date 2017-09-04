(ns crom.utils
  (:require [clojure.spec.alpha :as s]))

(def spec->name (comp keyword name))

(defn keys-spec?
  [spec]
  (= :keys (if (seq? spec)
             (keyword (first spec))
             (keyword (first (s/describe spec))))))

(defn keys-specs
  [spec]
  (when (keys-spec? spec)
    (let [d-spec (s/describe spec)
          req (nth d-spec 2 [])
          opt (nth d-spec 4 [])]
      (into req opt))))

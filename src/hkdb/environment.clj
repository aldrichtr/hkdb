;;; environment.clj --- Processes and programs -*- mode: clojure -*-

(ns hkdb.environment
  "An `environment` is a collection of `layer`s that affect a `keymap`")

(defn new
  "Create a new environment."
  []
  {:name "root"
   :layers []
   :keys []})


(defn add-layer
 "Add a layer to the environment."
 [name parent]
  (conj (parent :layers) name))

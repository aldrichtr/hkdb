;;; core.clj --- Main program file -*- mode: clojure -*-

(ns hkdb.core
  (:gen-class)
  (:require [hkdb.environment :as env]
            [hkdb.keys :as k]))

;;; Definitions

(defn -main
  "Hotkey Database main."
  [& args]
  (let [env (env/new)
        keycodes (k/load-key-file)]
    (println (format "There are %d codes in the db" (count keycodes)))
    (println (format "The environment starts with %s" (:name env)))
    (println "Hello, World!")))

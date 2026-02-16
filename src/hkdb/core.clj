;;; core.clj --- Main program file -*- mode: clojure -*-

(ns hkdb.core
  (:require [hkdb.environment :as env]
             [hkdb.keys.core :as keys]))

;;; Definitions

(defn -main
  "Hotkey Database main."
  [& args]
  (println "Hello, World!"))

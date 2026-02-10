(ns hkdb.core
  "Main entry point to the HotkeyDataBase"
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :refer [join]]
            [hkdb.db :as db]
            [hkdb.cli :as cli])
  (:import java.io.PushbackReader))

;;; Configuration

(def _default_config
  "Internal configuration structure."
  {:version "0.1.0" ; Version of the database schema
   :db {:path "hk.db"
        :client {:server-type :datomic-local
                 :system "hkdb"}
        }
   })

(def _default_config_file
  "Default config file if not specified."
  "hkdb.config.edn")

(def config {})

(defn init-config
  "Initialize the configuration."
  []
  (merge config _default_config))
  
(defn read-config
  "Read the configuration from the given file or url, and merge it
  with the existing config. Return the resulting configuration." 
  [& [config fname]]
  (let [config (or config (init-config))
        fname (or fname _default_config_file)]
    (try
      (let [loaded-config
            (when-let [url (or (io/resource fname)
                               (io/file fname))]
                (with-open [reader (-> url io/reader PushbackReader.)]
                  (edn/read reader)))]
        (merge config loaded-config))
      (catch java.io.FileNotFoundException _
        config))))


;;; Main

(defn -main [& args]
;;  (let [config read-config]
;;  (db/init config)))
  (println "Starting hkdb with args: " (join ", " args))
 (apply cli/run args))

(ns hkdb.cli
  "Handle the Command Line Interface of hkdb."
  (:require [cli-matic.core :refer [run-cmd]]))

;;; Keybindings

(defn add-keybinding
  "Add a new keybinding to the database."
  [{:keys [keys action]}]
  (println "Adding keybinding: " keys " " action))

;;; Context
(def context
  "The context that hkdb is operating in.  A context is a combination
of the application and any modes, states or options in place at the given
moment."
  {:name "global"
   :id "global"
   :level 0})

(defn get-context
  "Retrieve the context we are currently running under."
  [& _]
  (println "Context: " context))

;;; Run
;; (def options
;;   "Command Line Interface options."
;;   {:command "hkdb"
;;    :description "A command line utility for managing keyboard shortcuts"
;;    :version "0.1.0"
;;    :opts [{:option "config"
;;            :as "The configuration file to use"
;;            :default "hkdb-config.edn"
;;            :type :string}
;;           {:option "context"
;;            :as "The context to operate in."
;;            :short "x"
;;            :default "global"
;;            :type :string}]
;;    :subcommands [{:command "add"
;;                   :short "a"
;;                   :description "Add information to the database"
;;                   :subcommands [{:command "keybinding"
;;                                  :short "keys"
;;                                  :runs add-keybinding
;;                                  :opts [{:option  "keys"
;;                                          :as      "The keybinding"
;;                                          :type    :string
;;                                          :short   0 ;; means first un-parsed entry
;;                                          :default :present ;; means mandatory field
;;                                          }
;;                                         {:option  "action"
;;                                          :as      "The action"
;;                                          :type    :string
;;                                          :short   1 ;; means second un-parsed entry
;;                                          :default :present ;; means mandatory field
;;                                          }]}]}
;;                  {:command "show"
;;                   :short "s"
;;                   :description "Output information from the database"
;;                   :subcommands [{:command "context"
;;                                  :short "ctx"
;;                                  :runs get-context}]}]})
(def options
  (atom {:command "hkdb"
         :description "A command line utility for managing keyboard shortcuts"
         :version "0.1.0"
         :opts []
         :subcommands []}))

(defn add-option
  "Add a global option to hkdb command line"
  [opt]
  (swap! options update :opts conj opt))

(defn add-command
  "Add a subcommand to hkdb command line"
  [cmd]
  (swap! options update :subcommands conj cmd))

(defn init
  "Initialize the command line with all the options and commands"
  []
  (add-option {:option "config"
               :as "The configuration file to use"
               :default "hkdb-config.edn"
               :type :string})
  (add-option {:option "context"
               :as "The context to operate in."
               :short "x"
               :default "global"
               :type :string})
  (add-command {:command "add"
                :short "a"
                :description "Add information to the database"
                :subcommands [{:command "keybinding"
                               :short "keys"
                               :runs add-keybinding
                               :opts [{:option  "keys"
                                       :as      "The keybinding"
                                       :type    :string
                                       :short   0
                                       :default :present}
                                      {:option  "action"
                                       :as      "The action"
                                       :type    :string
                                       :short   1
                                       :default :present}]}]})
  (add-command {:command "show"
                :short "s"
                :description "Output information from the database"
                :subcommands [{:command "context"
                               :short "ctx"
                               :runs get-context}]}))

(defn run
  "Run the command line interface."
  [& args]
  (init)
  (run-cmd args options))

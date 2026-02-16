;;; cli.clj --- Command Line Interface for hkdb -*- mode: clojure -*-

(ns hkdb.cli
  (:require [cli-matic.core :as cm]
            [cli-matic.utils-v2 :refer [get-subcommand]]
            [clojure.pprint :refer [pprint]]))

;;; Configuration



(defn print-configuration
  "Output to the console, the current configuration"
  [cfg]
  (pprint cfg))

(defn add-subcmd
  "Add a subcommand to the Command Line.

  subcmd is a map of {:command :description :opts :runs}"
  [subcmd cfg]
  (conj (:subcommands cfg) subcmd))

(defn add-option
  "Add a option to the Command Line.

  subcmd is a map of {:command :description :opts :runs}"
  [subcmd cfg]
  (conj (:subcommands cfg) subcmd))

;;; Help Commands

(defn global-help
  "Output help text for the hkdb cli."
  [cfg sub-cmd]
  (let [branch (get-subcommand cfg sub-cmd)]
    (str sub-cmd "\n" branch)))

(defn subcmd-help
  "Output help text for the given sub-command."
  [cfg sub-cmd]
  (let [leaf (get-subcommand cfg sub-cmd)]
    (str sub-cmd "\n" leaf)))

(def configuration
  {:command "hkdb"
   :description "Manage hotkeys and keybindings."
   :global-help global-help
   :subcmd-help subcmd-help
   :version     "0.1.0"
   :opts        []
   :subcommands []})
;;; Run

(defn run
  "Run the hkdb cli."
  [args config]
  (cm/run-cmd args config))

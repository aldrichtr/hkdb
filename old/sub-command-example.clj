(ns hkdb.core
  "Main entry point to the HotkeyDataBase"
  (:require [clojure.tools.cli :as cli]
            [clojure.string :as string]))

(def global-options
  [["-c" "--config FILE" "Configuration file"]])

(def add-options
  [["-a" "--app APP" "Application name" :required true]
   ["-m" "--mode MODE" "Mode (global, application-specific)" :default "global"]
   ["-x" "--action ACTION" "Action to perform" :required true]])

(def query-options
  [["-k" "--key KEY" "Key to query"]])

(def cli-options
  (concat global-options
          [["-h" "--help" "Show help"
            :default false
            :flag true]]))

(defn usage [options-summary]
  (str "Usage: hkdb [options] <command> [command-options]\n"
       "Commands:\n"
       "  add    Add a new keybinding\n"
       "  query  Query existing keybindings\n"
       "\n"
       "Options:\n"
       options-summary))

(defn add-main [args]
  (let [{:keys [options arguments errors summary]}
        (cli/parse-opts args add-options)]
    (cond
      errors (println (str "Error parsing add options:\n" (string/join "\n" errors)))
      (:help options)
      (println "Usage: hkdb add [options] <keybinding>"))
      (empty? arguments)
      (println "Error: <keybinding> is required for the add command.")
      :else (println "Adding keybinding:"
                     (first arguments) "with options:" options)))

(defn query-main [args]
  (let [{:keys [options arguments errors summary]}
        (cli/parse-opts args query-options)]
    (cond
      errors
      (println (str "Error parsing query options:\n" (string/join "\n" errors)))
      (:help options)
      (println "Usage: hkdb query [options]")
      :else
      (println "Querying with options:" options "and arguments:" arguments))))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]}
        (cli/parse-opts args cli-options :in-order true)]
    (cond
      errors (println (str "Error parsing global options:\n"
                           (string/join "\n" errors)))
      (:help options) (println (usage summary))
      (empty? arguments) (println (usage summary))
      :else
      (let [[command & command-args] arguments]
        (case command
          "add" (add-main command-args)
          "query" (query-main command-args)
          (println (str "Unknown command: " command "\n" (usage summary))))))))

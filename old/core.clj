(ns hkdb.core
    (:require [clojure.string :as str]
              [hkdb.db :as db]
              [clojure.java.io :as io]))

  ;;; Data Model (as Clojure data structures)
  (defn create-application [name & {:keys [description]}] ...)
  (defn create-mode [name & {:keys [description]}] ...)
  (defn create-key [code & {:keys [description]}] ...)
  (defn create-command [name & {:keys [description]}] ...)
  (defn create-key-combination [keys] ...)
  (defn create-leader-key [key] ...)
  (defn has-mode [app mode] ...)
  (defn binds-to [mode target & {:keys [uses-key uses-leader then-key]}] ...)
  (defn uses-key [combination key order] ...)
  (defn uses-leader [binding leader] ...)
  (defn then-key [binding combination] ...)
  ;;; Example Data
  (def vscode (create-application "VSCode" :description "Visual Studio Code"))
  (def global-mode (create-mode "Global"))
  (def normal-mode (create-mode "Normal" :description "Normal mode in Vi"))
  (def insert-mode (create-mode "Insert" :description "Insert mode in Vi"))
  (def ctrl (create-key "Ctrl" :description "Control Key"))
  (def s (create-key "S"))
  (def d (create-key "D"))
  (def a (create-key "A"))
  (def space (create-key "Space"))
  (def f (create-key "F"))
  (def enter (create-key "Enter"))
  (def esc (create-key "Esc"))
  (def save-cmd (create-command "save" :description "Save the current file"))
  (def delete-line-cmd (create-command "delete-line" :description "Delete the current line"))
  (def append-cmd (create-command "append" :description "Append to the current line"))
  (def find-files-cmd (create-command "find-files" :description "Find files in the workspace"))
  (def new-line-cmd (create-command "new-line" :description "Insert a new line below the current line"))
  (def exit-insert-mode (create-command "exit-insert-mode" :description "Exit insert mode"))
  (def ctrl-s-combo (create-key-combination [ctrl s]))
  (def dd-combo (create-key-combination [d d]))
  (def ctrl-space-leader (create-leader-key (create-key-combination [ctrl space])))
  (def a-combo (create-key-combination [a]))
  (def enter-key-combo (create-key-combination [enter]))
  (def esc-key-combo (create-key-combination [esc]))
  (def f-key-combination (create-key-combination [f]))
  (def keybindings
    [(has-mode vscode global-mode)
     (has-mode vscode normal-mode)
     (has-mode vscode insert-mode)
     (-> global-mode
         (binds-to save-cmd :uses-key ctrl-s-combo))
     (-> normal-mode
         (binds-to delete-line-cmd :uses-key dd-combo))
     (-> normal-mode
         (binds-to append-cmd :uses-key a-combo))
     (-> insert-mode
         (binds-to exit-insert-mode :uses-key esc-key-combo))
     (-> global-mode
         (binds-to find-files-cmd :uses-leader ctrl-space-leader :then-key f-key-combination))
     (-> insert-mode
         (binds-to new-line-cmd :uses-key enter-key-combo))])

  ;;; Graph Database Interaction (Neo4j)
  (def db-uri "bolt://localhost:7687") ; Or "neo4j+sdb://" for embedded, adjust as needed

  (defn create-node [session node]
    (let [props (or (:properties node) {})
          labels (if (= :KeyCombination (:type node))
                   ["KeyCombination"]
                   (if (= :LeaderKey (:type node))
                     ["LeaderKey"]
                     [(name (:type node))]))]
      (neo4j/execute session
                     (str "CREATE (n" (str/join "" (map (fn [label] (str ":" label)) labels)) " "
                          (neo4j/props-str props) ") RETURN n")
                     props)))

  (defn create-edge [session edge]
    (let [source-props (get-in edge [:source :properties])
          target-props (get-in edge [:target :properties])
          rel-props    (:properties edge)]
      (neo4j/execute session
                     (str "MATCH (a),(b) "
                          "WHERE a." (if (= :KeyCombination (:type (:source edge))) "id" "name") " = $sourceName AND b." (if (= :KeyCombination (:type (:target edge))) "id" "name") " = $targetName "
                          "CREATE (a)-[r:" (name (:relation edge)) " " (neo4j/props-str rel-props) "]->(b) RETURN r")
                     {:sourceName (if (= :KeyCombination (:type (:source edge))) (str (gensym "kc")) (:name source-props))
                      :targetName (if (= :KeyCombination (:type (:target edge))) (str (gensym "kc")) (:name target-props))
                      :rel-props    rel-props})))

  (defn store-data-in-neo4j [session data]
    (doseq [item data]
      (if (contains? [:Application :Mode :Key :Command :KeyCombination :LeaderKey] (:type item))
        (create-node session item))
    (doseq [item data]
      (if (contains? [:HAS_MODE :BINDS_TO :USES_KEY :USES_LEADER :THEN_KEY] (:relation item))
        (create-edge session item))))

  (defn get-bindings-for-mode-from-neo4j [session app-name mode-name]
    (neo4j/execute session
                   "MATCH (app:Application {name: $appName})-[:HAS_MODE]->(mode:Mode {name: $modeName})
                    OPTIONAL MATCH (mode)-[:BINDS_TO]->(target)
                    OPTIONAL MATCH (target)-[:USES_KEY]->(kc:KeyCombination)-[:HAS_KEY]->(k:Key)
                    OPTIONAL MATCH (target)-[:USES_LEADER]->(leader:LeaderKey)-[:HAS_KEY]->(lk:Key)
                    RETURN
                      target.name AS command,
                      collect(k.code) AS keys,
                      collect(lk.code) AS leaderKeys"
                   {:appName  appName
                    :modeName mode-name})
    )

  (defn get-command-for-key-combination-from-neo4j [session app-name mode-name key-combination]
    (let [keys (str/split key-combination #"\+")
          n-keys (count keys)]
      (neo4j/execute session
                     (str "MATCH (app:Application {name: $appName})-[:HAS_MODE]->(mode:Mode {name: $modeName})
                          MATCH (mode)-[:BINDS_TO]->(target)
                          OPTIONAL MATCH (target)-[:USES_KEY]->(kc:KeyCombination)
                          WITH app, mode, target, kc
                          WHERE size((kc)-[:HAS_KEY]->()) = " n-keys "
                          MATCH (kc)-[:HAS_KEY]->(k:Key)
                          WITH app, mode, target, kc, collect(k.code) as keycodes
                          RETURN target.name AS command, keycodes")
                     {:appName  appName
                      :modeName mode-name})
      ))

  ;;; Main (Example Usage)
  (defn -main []
    (println "Keybinding App")
    (neo4j/with-driver [driver db-uri]
      (neo4j/with-session [session driver]
        ;; Ensure the database is clean before running the example
        (neo4j/execute session "MATCH (n) DETACH DELETE n")
        (store-data-in-neo4j session keybindings)

        (println "Bindings for VSCode in Global mode:" (neo4j/read session (get-bindings-for-mode-from-neo4j session "VSCode" "Global")))
        (println "Bindings for VSCode in Normal mode:" (neo4j/read session (get-bindings-for-mode-from-neo4j session "VSCode" "Normal")))
        (println "Command for Ctrl+S in VSCode Global:" (neo4j/read session (get-command-for-key-combination-from-neo4j session "VSCode" "Global" "Ctrl+S")))
        (println "Command for dd in VSCode Normal:" (neo4j/read session (get-command-for-key-combination-from-neo4j session "VSCode" "Normal" "D+D")))
        (println "Command for Ctrl+Space+f in VSCode Global:" (neo4j/read session (get-command-for-key-combination-from-neo4j session "VSCode" "Global" "Ctrl+Space+F")))))))


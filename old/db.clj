(ns hkdb.db
  (:require '[neo4clj.client :as client]
            [clojure.string :as str]))

(def connection
  (client/connect "bolt://localhost:7687"))

  
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
                  :modeName mode-name}))

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
                    :modeName mode-name}))))

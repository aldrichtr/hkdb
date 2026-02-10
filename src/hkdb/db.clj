(ns hkdb.db
  "Database functions for Hotkey Database."
  (:require [datomic.client.api :as d]))

(def cfg
  "Configuration settings for the datomic client to connect to the database."
  {:server-type :datomic-local :system "hkdb"})

(def client
  "The Datomic database client connection instance."
  (d/client cfg))

;;; Schema

;; Keys (individual keys on a keyboard)
(def key-schema
  "Schema defining individual keyboard keys, including name, code, and category."
  [{:db/ident :key/id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc
    "Unique identifier for a key (e.g., 'Ctrl', 'Escape')."}
   
   {:db/ident :key/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc
    "Human-readable name of the key (e.g., 'Return' for 'Enter')."}
   
   {:db/ident :key/code
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc
    "Numeric keycode associated with the key (e.g., 13 for 'Enter')."}
   
   {:db/ident :key/category
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc
    "Classification of the key (e.g., 'Navigation', 'Modifier', 'Function')."}])

;; Keychords (multiple keys pressed together)
(def keychord-schema
  "Schema defining keychords, which consist of multiple keys pressed simultaneously."
  [{:db/ident :keychord/id
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/doc "References multiple keys to form a keychord (e.g., 'Ctrl+Shift')."}])

;; Keysequences (ordered keychords forming a shortcut)
(def keysequence-schema
  "Schema defining keysequences, which are ordered lists of keychords."
  [{:db/ident :keysequence/id
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/doc "An ordered sequence of keychords that represent a shortcut (e.g., 'Ctrl+X', 'Ctrl+C')."}])

;; Keybindings (mapping keysequences to actions)
(def keybinding-schema
  "Schema defining keybindings, which associate key sequences with actions."
  [{:db/ident :keybinding/id
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "A unique identifier for a keybinding entry."}
   
   {:db/ident :keybinding/sequence
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "The key sequence that triggers an action (e.g., 'Ctrl+C')."}
   
   {:db/ident :keybinding/action
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/many
    :db/doc "The action(s) associated with the keybinding (e.g., 'Copy', 'Paste')."}])

;; Keymaps (grouping keybindings by context)
(def keymap-schema
  "Schema defining keymaps, which group keybindings within specific contexts."
  [{:db/ident :keymap/id
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "A unique identifier for the keymap."}
   
   {:db/ident :keymap/context
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/doc "References one or more contexts where keybindings apply."}
   
   {:db/ident :keymap/bindings
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/doc "A collection of keybindings associated with the keymap."}])

;; Actions (things keybindings trigger)
(def action-schema
  "Schema defining actions triggered by keybindings."
  [{:db/ident :action/id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "A unique identifier for an action (e.g., 'Copy', 'Paste')."}])

;; Platforms (environments where keybindings exist)
(def platform-schema
  "Schema defining platforms, including OS, applications, and window managers."
  [{:db/ident :platform/id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "A unique identifier for the platform (e.g., 'Windows', 'Emacs')."}
   
   {:db/ident :platform/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Human-readable name of the platform."}
   
   {:db/ident :platform/type
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The type of platform (e.g., 'OS', 'Application', 'Window Manager')."}
   
   {:db/ident :platform/priority
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc "Priority level of the platform (higher values take precedence)."}])

;; Contexts (application "states" where keybindings are valid)
(def context-schema
  "Schema defining contexts, which represent application modes or environments where keybindings are active."
  [{:db/ident :context/id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Unique identifier for the context (e.g., 'Normal Mode', 'Insert Mode')."}
   
   {:db/ident :context/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Human-readable name of the context."}
   
   {:db/ident :context/platform
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "References the platform that owns this context."}
   
   {:db/ident :context/priority
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc "Priority level of the context (higher values take precedence in keybinding resolution)."}])

;; Combine all schemas into a single definition
(def schema
  "Complete schema definition for the hkdb database."
  (concat key-schema keychord-schema keysequence-schema keybinding-schema
          keymap-schema action-schema context-schema platform-schema))

(defn load-data
  "Loads a batch of data into the database."
  [conn data]
  (d/transact conn data))

(defn db-exists?
  "Checks if the database has been initialized by looking for key categories."
  [conn]
  (seq (d/q '[:find ?e :where [?e :category/name]] conn)))

(defn init
  "Initialize the HotKey DataBase. If the database already exists, return the
  connection to it, otherwise, create a new database and return a connection to
  that."
  [config])


(defn create-new
  "Create a new HotKey DataBase."
  [config]
  (let [_ (d/create-database client {:db-name config})
        conn (d/connect client {:db-name "hkdb"})]
    (d/transact conn {:tx-data schema})
    conn))

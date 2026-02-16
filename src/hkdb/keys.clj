;;; keys.clj --- Key structures -*- mode: clojure; -*-

(ns hkdb.keys
  "Interface to keybinding structures and functions."
  (:require [clojure.edn :as edn]      ; TODO: Perhaps we want to allow json as well?
           [clojure.java.io :as io]))

;;; Keys

(defn load-key-file
  "Load key data from the given key file (keys.edn by default)"
  ([] (load-key-file (io/resource "keycodes.edn"))) ; default path for keycodes.edn
  ([path]
   (try
     (with-open [rdr (java.io.PushbackReader. (io/reader path))]
       (edn/read {:eof nil} rdr))
     ;; (-> (slurp (io/file path))
     ;;     (edn/read-string))
     (catch Exception e
       (println "Error reading keycodes file:" (.getMessage e))
       nil))))

;;; Chords

;;; Sequences

;;; Bindings


;; (defn add-binding
;;   "Create a keybinding in a given map.

;;   - keys is a vector of one or more keychords
;;   - action is a map of command, name, description"
;;   [binding keymap]
;;   {:keys keys :action action})


;; (def keycodes
;;   "Individual keys on the keyboard."
;;   (load-key-file)) ; TODO: This file path should come from the config

(def modcodes
  "Modifier key codes and values.
  These correspond to *left side* modifier keys"
  {:shift   0x01 ; 0000 0001
   :alt     0x02 ; 0000 0010
   :control 0x04 ; 0000 0100
   :meta    0x08 ; 0000 1000
   :super   0x10 ; 0001 0000
   :hyper   0x20 ;
   })

(defn build-by-value
  "Create a vector of keycodes where the index is the value of the keycode."
  [by-name]
  (let [max-val (apply max (map :value (vals by-name)))]
    (reduce
     (fn [v [k {:keys [value] :as entry}]]
       (assoc v value (assoc entry :name k)))
     (vec (repeat (inc max-val) nil))
     by-name)))

;;; Pack and unpack


(defn pack
  "Pack a keychord into a 32bit integer"
  [keycode modifiers]
  (bit-or (bit-shift-left keycode 16)
          (bit-and modifiers 0xFFFF)))

(defn unpack
  "Unpack a 32bit integer into a key"
  [chord]
  {:keycode   (bit-and (bit-shift-right chord 16) 0xFFFF)
   :modifiers (bit-and chord 0xFFFF)})

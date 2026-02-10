(ns hkdb.keybinding
  (:require [clojure.string :as str]))

(defn emacs-keyseq-parser
  "Parses an Emacs-style key sequence into a structured vector.
  
  Accepts key sequences like:
  - \"C-x C-s\" → [[\"Ctrl\" \"x\"] [\"Ctrl\" \"s\"]]
  - \"C-P\" → [[\"Ctrl\" \"Shift\" \"P\"]]
  
  Uses:
  - C- → 'Ctrl'
  - M- → 'Alt'
  - S- → 'Shift'
  
  Example:
    (emacs-keyseq-parser \"C-x C-s\") ;; => [[\"Ctrl\" \"x\"] [\"Ctrl\" \"s\"]]
  "
  [keyseq-str]
  (let [chords (str/split keyseq-str #" ") ;; Split multiple key chords
        key-map {"C-" "Ctrl" "M-" "Alt" "S-" "Shift"}]
    (mapv
     (fn [chord]
       (let [[_ modifier key] (re-find #"^(C-|M-|S-)?(.+)$" chord)]
         (cond-> []
           modifier (conj (key-map modifier)) ;; Convert modifier
           (Character/isUpperCase (first key)) (conj "Shift") ;; Detect Shift
           :always (conj key)))) ;; Always add the main key
     chords)))

(defn standard-keyseq-parser
  "Parses GUI-style key sequences like 'Ctrl+Shift+P' into a structured vector.
  
  Accepts:
  - \"Ctrl+Shift+P\" → [\"Ctrl\" \"Shift\" \"P\"]
  - \"Alt+F4\" → [\"Alt\" \"F4\"]
  
  Example:
    (standard-keyseq-parser \"Ctrl+Shift+P\") ;; => [\"Ctrl\" \"Shift\" \"P\"]
  "
  [keyseq-str]
  ;; (Implementation here)
  )

(defn raw-keyseq-parser
  "Processes already-structured key sequences into a cleaned format.
  
  Accepts:
  - [\"Ctrl\" \"Alt\" \"P\"] → [\"Ctrl\" \"Alt\" \"P\"]
  - [\"Shift\" \"Escape\"] → [\"Shift\" \"Escape\"]
  
  Example:
    (raw-keyseq-parser [\"Ctrl\" \"Alt\" \"P\"]) ;; => [\"Ctrl\" \"Alt\" \"P\"]
  "
  [keys]
  (mapv clojure.string/trim keys))

(defn parse-keys
  "Determines the correct parser based on the input format.
  
  - Detects **Emacs-style sequences** (e.g., \"C-x C-s\").
  - Detects **GUI-style sequences** (e.g., \"Ctrl+Shift+P\").
  - Detects **raw key vector input** (e.g., [\"Ctrl\" \"Alt\" \"P\"]).
  
  Example:
    (parse-keys \"C-x C-s\") ;; => [[\"Ctrl\" \"x\"] [\"Ctrl\" \"s\"]]
    (parse-keys \"Ctrl+Shift+P\") ;; => [\"Ctrl\" \"Shift\" \"P\"]
    (parse-keys [\"Ctrl\" \"Alt\" \"P\"]) ;; => [\"Ctrl\" \"Alt\" \"P\"]
  "
  [keyseq]
  (cond
    (string? keyseq)
    (if (re-find #"-" keyseq) ;; If it has a dash (Emacs-style)
      (emacs-keyseq-parser keyseq)
      (standard-keyseq-parser keyseq))

    (vector? keyseq) (raw-keyseq-parser keyseq)
    :else (throw (Exception. "Unrecognized key sequence format!"))))

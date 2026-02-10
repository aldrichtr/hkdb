(ns hkdb.keysequence-test
  "Unit tests for key sequence parsing."
  (:require [clojure.test :refer [deftest is]]
            [hkdb.keybinding :as keys]))

(deftest test-emacs-keyseq-parser
 ;; "Tests Emacs-style key sequence parsing."
  (is (= (keys/emacs-keyseq-parser "C-p") ["Ctrl" "p"]))
  (is (= (keys/emacs-keyseq-parser "C-P") ["Ctrl" "Shift" "P"]))
  (is (= (keys/emacs-keyseq-parser "M-x") ["Alt" "x"])))

(deftest test-standard-keyseq-parser
  ;; "Tests standard GUI-style key sequence parsing."
  (is (= (keys/standard-keyseq-parser "Ctrl+Shift+P") ["Ctrl" "Shift" "P"]))
  (is (= (keys/standard-keyseq-parser "Alt+F4") ["Alt" "F4"])))

(deftest test-parse-keys
  ;; "Tests high-level parsing function."
  (is (= (keys/parse-keys "C-x C-s") [["Ctrl" "x"] ["Ctrl" "s"]]))
  (is (= (keys/parse-keys ["Ctrl" "Alt" "P"]) ["Ctrl" "Alt" "P"])))

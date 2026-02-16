;;; core_test.clj --- unit tests for hkdb/keys/core -*- mode: clojure -*-

(ns hkdb.keys.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [hkdb.keys.core :as k]))

;;; Keys

(deftest test-that-key-file-is-loaded
  (testing "Load key file - no args - use default"
  (let [keys (k/load-key-file)]
    (is (> (count keys) 0) "There are keys after loading file")
    (is (= ((keys :VK_F1) :value) 112) "Can lookup the value of the F1 key"))))

;;; Pack
(deftest pack-test
  (testing "pack combines keycode and modifier into a 32bit integer"
    (is (= 65538 (k/pack 1 2)))
    (is (= 0x00010000 (k/pack 1 0)))
    (is (= 0x00000001 (k/pack 0 1)))))

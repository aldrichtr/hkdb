;;; environment_test.clj -*- mode: clojure -*-

(ns hkdb.environment-test
  (:require [clojure.test :refer [deftest is testing]]
            [hkdb.environment :as env]))



(deftest test-that-environment-root-is-created
  (testing "The root environment is created."
    (let [environ (env/new)]
      (is (= (:name environ) "root")))))

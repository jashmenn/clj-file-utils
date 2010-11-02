(ns clj-file-utils.core-test
  (:use [clj-file-utils.core] :reload)
  (:use [clojure.test])
  (:require [clojure.contrib.io :as io])
  (:import java.io.File))

(def test-file (io/file "test" "clj_file_utils" "core_test.txt"))

(deftest test-size
  (is (= 11 (size test-file)))
  (is (= 11 (size (canonical-path test-file)))))

(ns clj-file-utils.core-test
  (:use [clj-file-utils.core] :reload)
  (:use [clojure.test])
  (:require [clojure.contrib.io :as io])
  (:import java.io.File))

(def test-file (io/file "test" "clj_file_utils" "core_test.txt"))

(defn tmp-dir-fixture [f]
  (let [tmp-dir (io/file "tmp")]
    (do
      (io/delete-file-recursively tmp-dir true)
      (.mkdirs tmp-dir)
      (f))))

(deftest test-core
  (is (directory? "src"))
  (is (directory? (io/file "src")))
  (is (not (directory? nil)))
  (is (file? test-file))
  (is (file? (canonical-path test-file)))
  (is (exists? test-file))
  (is (not (exists? nil))))

(deftest test-size
  (is (= 11 (size test-file)))
  (is (= 11 (size (canonical-path test-file)))))

(use-fixtures :each tmp-dir-fixture)

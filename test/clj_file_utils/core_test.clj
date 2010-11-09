(ns clj-file-utils.core-test
  (:use [clj-file-utils.core] :reload)
  (:use [clojure.test])
  (:require [clojure.contrib.io :as io])
  (:import java.io.File))

(def test-file (io/file "test" "clj_file_utils" "core_test.txt"))

(def tmp-dir (io/file "tmp"))

(defn tmp-dir-fixture [f]
  (do
    (io/delete-file-recursively tmp-dir true)
    (.mkdirs tmp-dir)
    (f)))

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

(deftest test-cp
  (let [to-file (io/file tmp-dir "test-cp")]
    (do
      (cp test-file to-file)
      (is (exists? to-file))
      (is (file? to-file))
      (is (= (.lastModified test-file)
             (.lastModified to-file))))))

(deftest test-mv-file-to-file
  (let [from-file (io/file tmp-dir "from-file")
        to-file (io/file tmp-dir "to-file")]
    (do
      (cp test-file from-file)
      (is (exists? from-file))
      (mv from-file to-file)
      (is (not (exists? from-file)))
      (is (exists? to-file))
      (is (file? to-file)))))

(deftest test-mv-file-to-dir
  (let [from-file (io/file tmp-dir "from-file")
        to-dir (io/file tmp-dir "to-dir")]
    (do
      (cp test-file from-file)
      (is (exists? from-file))
      (mkdir to-dir)
      (mv from-file to-dir)
      (is (not (exists? from-file)))
      (is (file? (file to-dir (.getName from-file)))))))

(deftest test-mv-dir-to-dir
  (let [from-dir (io/file tmp-dir "from-dir")
        to-dir (io/file tmp-dir "to-dir")]
    (do
      (mkdir from-dir)
      (mv from-dir to-dir)
      (is (not (exists? from-dir)))
      (is (exists? to-dir))
      (is (directory? to-dir)))))

(deftest test-touch
  (let [file (io/file tmp-dir "test-touch")]
    (do
      (is (not (exists? file)))
      (touch file)
      (is (exists? file)))))

(use-fixtures :each tmp-dir-fixture)

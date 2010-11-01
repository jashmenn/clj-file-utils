(ns clj-file-utils.core
  (:import (java.io File IOException)
           org.apache.commons.io.FileUtils)
  (:use clojure.contrib.shell-out)
  (:gen-class))

(defn file
  "Returns an instance of java.io.File based on the given file name (or 
    acutal file) and parent filenames or files. Note that name args given
    as Strings must not include any path seperators."
  ([name]        (File. name))
  ([parent name] (File. parent name))
  ([p q & names] (reduce file (file p q) names)))

(defmacro defn-file [name docstring args & body]
  `(do
    (defmulti ~name ~docstring class)
    (defmethod ~name File ~args ~@body)
    (defmethod ~name String ~args (~name (file ~@args)))))

(defmacro defn-file-2 [name docstring args & body]
  `(do
    (defmulti ~name ~docstring (fn [a# b#] [(class a#) (class b#)]))
    (defmethod ~name [File File] ~args ~@body)
    (defmethod ~name [String String] ~args (~name 
                                             (file (first ~args))
                                             (file (last ~args))))))


(defn-file exists?
  "returns true if the file exists" 
  [file] 
  (.exists file))

(defn-file exist 
  "DEPRICATED: Returns true if the file exists" 
  [file] 
  (.exists file))

(defn-file size
  "Returns the size in bytes of a file."
  [file]
  (.length file))

(defn-file-2 mv
  "Move a file from one location to another, preserving the file data."
  [from-file to-file]
  (FileUtils/moveFile from-file to-file))

(defn-file-2 cp
  "Copy a file from one location to another, preserving the file date."
  [from-file to-file]
  (FileUtils/copyFile from-file to-file))

(defn-file-2 cp-r
  "Copy a directory from one location to another, preseing the file data."
  [from-dir to-dir]
  (FileUtils/copyDirectory from-dir to-dir))

(defn-file rm
  "Remove a file. Will throw an exception if the file cannot be deleted."
  [file]
  (if-not (.delete file)
    (throw (IOException.))))

(defn-file rm-f
  "Remove a file, ignoring any errors."
  [file]
  (FileUtils/forceDelete file))

(defn-file rm-r
  "REmove a directory. The directory must be empty; will throw an exception
  if it is not or if the file cannot be deleted."
  [dir]
  (if-not (.delete dir)
    (throw (IOException.))))

(defn-file rm-rf
  "Remove a directory, ignoring any errors."
  [dir]
  (FileUtils/forceDelete dir))

(defn-file touch
  "'touch' as file, as with the Unix command."
  [file]
  (FileUtils/touch file))

(defn-file mkdir-p
  "Create the directory, including any required but nonexist parents.
  The method does not throw an exception if the complete directory tree
  already exists."
  [file]
  (when-not (.exists file)
    (FileUtils/forceMkdir file)))

;; TODO turn this into a multimethod as well
(defn chmod
  "'chmod' a file to a mode given as a 4-character string. Only works on
  system with a chmod command."
  [#^File file #^String mode]
  (sh "chmod" mode (.getAbsolutePath file)))

;;(defn ls [dir]
;;  (seq (. (new java.io.File dir) (listFiles))))


(defn cwd [] (System/getProperty "user.dir"))


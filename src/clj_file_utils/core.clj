(ns clj-file-utils.core
  (:require [clojure.contrib.duck-streams :as streams])
  (:require [clojure.contrib.io :as io])
  (:import [java.io File])
  (:gen-class))

(defmacro defun [name docstring args & body]
  `(do
     (defmulti ~name ~docstring class)
     (defmethod ~name File ~args ~@body)
     (defmethod ~name String ~args (~name (io/file ~@args)))
     (defmethod ~name :default ~args false)))

(defun file?
  "Returns true if the path is a file; false otherwise."
  [path]
  (.isFile path))

(efun directory?
  "Returns true if the path is a directory; false otherwise."
  [path]
  (.isDirectory path))

(defun exists?
  "Returns true if path exists; false otherwise."
  [path]
  (.exists path))

(defn size
  "Returns the size in bytes of a file."
  [file]
  (.length (io/file file)))

(defn rm
  "Remove a file. Will throw an exception if the file cannot be deleted"
  [file]
  (io/delete-file file))

(defn rm-f
  "Remove a file, ignoring any errors."
  [file]
  (io/delete-file file true))

(defn rm-r
  "Remove a directory. The directory must be empty; will throw an exception
    if it is not or if the file cannot be deleted."
  [path]
  (io/delete-file-recursively path))

(defn rm-rf
  "Remove a directory, ignoring any errors."
  [path]
  (io/delete-file-recursively path true))

(defn cp
  "Copy a file, preserving last modified time by default."
  [from to & {:keys [preserve] :or {preserve true}}]
  (do
    (streams/copy from to)
    (if (and (file? from) (file? to) preserve)
      (.setLastModified to (.lastModified from)))))

;(defn cp-r
;  "Copy a directory, preserving last modified times by default."
;  [from to & {:keys [preserve] :or {preserve true}}]
;  (do
;    (println "Hello!")))

(defn mv
  "Try to rename a file, or copy and delete if on another filesystem."
  [from to]
  (if (not (.renameTo from to))
    (do
      (cp from to)
      (rm from))))

(defn touch
  "Create a file or update the last modified time."
  [file]
  (if-not (.createNewFile file)
    (.setLastModified file (System/currentTimeMillis))))

(defn mkdir
  "Create a directory."
  [dir]
  (.mkdir dir))

(defn mkdir-p
  "Create a directory and all parent directories if they do not exist."
  [dir]
  (.mkdirs dir))

;(defn chmod
;  [path]

(ns clj-file-utils.core
  (:require [clojure.contrib.duck-streams :as streams])
  (:require [clojure.contrib.io :as io])
  (:import [java.io File])
  (:gen-class))

(defmacro defn-file [m docstring args & body]
  `(do
     (defmulti ~m ~docstring class)
     (defmethod ~m File ~args ~@body)
     (defmethod ~m String ~args (~m (io/file ~@args)))))

(defn-file file?
  "Returns true if the path is a file; false otherwise."
  [path]
  (.isFile path))

(defn-file directory?
  "Returns true if the path is a directory; false otherwise."
  [path]
  (.isDirectory path))

(defn-file exists?
  "Returns true if path exists; false otherwise."
  [path]
  (.exists path))

(defn-file size
  "Returns the size in bytes of a file."
  [file]
  (.length file))

(defn-file rm
  "Remove a file. Will throw an exception if the file cannot be deleted"
  [file]
  (io/delete-file file))

(defn-file rm-f
  "Remove a file, ignoring any errors."
  [file]
  (io/delete-file file true))

(defn-file rm-r
  "Remove a directory. The directory must be empty; will throw an exception
    if it is not or if the file cannot be deleted."
  [file]
  (io/delete-file-recursively file))

(defn-file rm-rf
  "Remove a directory, ignoring any errors."
  [file]
  (io/delete-file-recursively file true))

(defn-file cp
  "Copy a file, preserving last modified time by default."
  [from to & {:keys [preserve] :or {preserve true}}]
  (do
    (streams/copy from to)
    (if (and (file? from) (file? to) preserve)
      (.setLastModified to (.lastModified from)))))

;(defn-file cp-r
;  "Copy a directory, preserving last modified times by default."
;  [from to & {:keys [preserve] :or {preserve true}}]
;  (do
;    (println "Hello!")))

(defn-file mv
  "Try to rename a file, or copy and delete if on another filesystem."
  [from to]
  (if (not (.renameTo from to))
    (do
      (cp from to)
      (rm from))))

(defn-file touch
  "Create a file or update the last modified time."
  [file]
  (if-not (.createNewFile file)
    (.setLastModified file (System/currentTimeMillis))))

(defn-file mkdir
  "Create a directory."
  [dir]
  (.mkdir dir))

(defn-file mkdir-p
  "Create a directory and all parent directories if they do not exist."
  [dir]
  (.mkdirs dir))

;(defn chmod
;  [path]

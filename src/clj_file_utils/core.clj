(ns clj-file-utils.core
  (:require [clojure.contrib.duck-streams :as streams])
  (:require [clojure.contrib.io :as io])
  (:use [clojure.contrib.shell-out :only (sh)])
  (:import [java.io File])
  (:import [org.apache.commons.io FileUtils])
  (:gen-class))

; The following function is retained for backwards compatibility purposes.
(def file io/file)

; The following function is retained for backwards compatibility purposes.
(def pwd io/pwd)

; The following function is retained for backwards compatibility purposes.
(def cwd io/pwd)

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

(defun directory?
  "Returns true if the path is a directory; false otherwise."
  [path]
  (.isDirectory path))

(defun exists?
  "Returns true if path exists; false otherwise."
  [path]
  (.exists path))

(defn ls
  "List files in a directory."
  [dir]
  (seq (.listFiles (io/file dir))))

(defn touch
  "Create a file or update the last modified time."
  [path]
  (let [file (io/file path)]
    (do
      (.createNewFile file)
      (.setLastModified file (System/currentTimeMillis)))))

(defn mkdir
  "Create a directory."
  [dir]
  (.mkdir (io/file dir)))

(defn mkdir-p
  "Create a directory and all parent directories if they do not exist."
  [dir]
  (.mkdirs (io/file dir)))

(defn canonical-path
  "Returns the canonical path of the file or directory."
  [path]
  (.getCanonicalPath (io/file path)))

(defn size
  "Returns the size in bytes of a file."
  [file]
  (.length (io/file file)))

(defn rm
  "Remove a file. Will throw an exception if the file cannot be deleted."
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
  (let [from-file (io/file from)
        to-file (io/file to)]
    (FileUtils/copyFile from-file to-file preserve)))

(defn cp-r
  "Copy a directory, preserving last modified times by default."
  [from to & {:keys [preserve] :or {preserve true}}]
  (let [from-file (io/file from)
        to-file (io/file to)]
    (cond
      (and (file? from-file) (file? to-file))
        (FileUtils/copyFile from-file to-file preserve)
      (and (file? from-file) (directory? to-file))
        (FileUtils/copyFileToDirectory from-file to-file preserve)
      :default
        (FileUtils/copyDirectory from-file to-file preserve))))

(defn mv
  "Try to rename a file, or copy and delete if on another filesystem."
  [from to]
  (let [from-file (io/file from)
        to-file (io/file to)]
    (cond
      (and (file? from-file) (file? to-file))
        (FileUtils/moveFile from-file to-file)
      :default
        (FileUtils/moveToDirectory from-file to-file true))))

(defn chmod
  "Changes file permissions (UNIX only); for portability, consider pchmod."
  [args path]
  (sh "chmod" args (.getAbsolutePath (io/file path))))

(defn pchmod
  "Change file permissions in a portable way."
  [path & {:keys [r w x]}]
  (let [file (io/file path)]
    (do
      (if-not (nil? r) (.setReadable file r))
      (if-not (nil? w) (.setWritable file w))
      (if-not (nil? x) (.setExecutable file x)))))

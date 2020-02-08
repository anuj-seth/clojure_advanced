(ns the-tests.utils
  (:import (java.io FileOutputStream OutputStreamWriter BufferedWriter))
  (:import (java.io FileInputStream InputStreamReader BufferedReader)))

(defn make-reader
  [src]
  (-> src
      FileInputStream.
      InputStreamReader.
      BufferedReader.))

(defn make-writer
  [dst]
  (-> dst
      FileOutputStream.
      OutputStreamWriter.
      BufferedWriter.))


(ns the-tests.io-factory
  (:import (java.io File)
           (java.io FileOutputStream OutputStream OutputStreamWriter BufferedWriter)
           (java.io FileInputStream InputStream InputStreamReader BufferedReader)
           (java.net Socket URL)))

(defprotocol IOFactory
  (make-reader [this] "Creates a buffered reader")
  (make-writer [this] "Creates a buffered writer"))

(extend InputStream
  IOFactory
  {:make-reader (fn [src]
                  (-> src
                      InputStreamReader.
                      BufferedReader.))})

(extend OutputStream
  IOFactory
  {:make-writer (fn [dst]
                  (-> dst
                      OutputStreamWriter.
                      BufferedWriter.))})

(extend-type File
  IOFactory
  (make-reader [src]
    (make-reader (FileInputStream. src)))
  (make-writer [dst]
    (make-writer (FileOutputStream. dst))))

(extend-protocol IOFactory
  Socket
  (make-reader [src]
    (make-reader (.getInputStream src)))
  (make-writer [dst]
    (make-writer (.getOutputStream dst)))

  URL
  (make-reader [src]
    (make-reader
     (if (= "File" (.getProtocol src))
       (-> src .getPath FileInputStream.)
       (.openStream src))))
  (make-writer [dst]
    (make-writer
     (if (= "File" (.getProtocol dst))
       (-> dst .getPath FileOutputStream.)
       (throw (ex-info "Cannot write to a non-file URL"
                       {:type (.getProtocol dst)}))))))

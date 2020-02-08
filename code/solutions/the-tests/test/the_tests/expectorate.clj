(ns the-tests.expectorate
  (:require [the-test.utils :as utils]))

(defn expectorate
  [dst content]
  (with-open [writer (utils/make-writer dst)]
    (.write writer (str content))))

(expectorate "/home/anuj/hello.txt" "hello.text")


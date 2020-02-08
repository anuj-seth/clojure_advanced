(ns the-tests.gulp
  (:require [the-test.utils :as utils]))

(defn gulp
  [src]
  (let [sb (StringBuilder.)]
    (with-open [reader (utils/make-reader src)]
      (loop [c (.read reader)]
        (if (neg? c)
          (str sb)
          (do
            (.append sb (char c))
            (recur (.read reader))))))))

(gulp "/home/anuj/veryme_flow.txt")



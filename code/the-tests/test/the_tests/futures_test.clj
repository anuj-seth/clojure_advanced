(ns the-tests.futures-test
  (:require  [clojure.test :refer :all]))

(defn http-simulator
  "Return the last part of the url as an integer"
  [fake-url]
  (println fake-url)
  (Thread/sleep 1000)
  (read-string (last (clojure.string/split
                      fake-url #"/"))))

(defn process-urls
  "Process all the urls, 10 at a time so as not to overwhelm the system"
  [urls]
  __)

(deftest a-lot-of-futures
  (is (= [] (process-urls )))
  (let [suffixes (range 100)
        urls (map #(str %1 %2)
                  (repeat "http://fake-url/")
                  suffixes)]
    (is (= suffixes (process-urls urls)))))


;; this takes ~100 seconds
(time (let [suffixes (range 100)
            urls (map #(str %1 %2)
                      (repeat "http://fake-url/")
                      suffixes)]
        (doall
         (map http-simulator
              urls))))

;; how much time does this take ?
;; it should be around 10 seconds
(time (let [suffixes (range 100)
            urls (map #(str %1 %2)
                      (repeat "http://fake-url/")
                      suffixes)]
        (process-urls urls)))





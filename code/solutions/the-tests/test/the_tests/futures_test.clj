(ns the-tests.futures-test
  (:require  [clojure.test :refer :all]))

(defn http-simulator
  "Return the last part of the url as an integer"
  [fake-url]
  (Thread/sleep 1000)
  (read-string (last (clojure.string/split
                      fake-url #"/"))))

(defn process-urls-lazy
  "Process all the urls, 10 at a time so as not to overwhelm the system"
  [urls]
  (flatten (map (fn [batch]
                  (let [the-futures (map #(future (http-simulator %))
                                         batch)
                        the-results (map deref the-futures)]
                     the-results))
                 (partition-all 10 urls))))

;; how much time does this take ?
;; it should be around 10 seconds
#_(time (let [suffixes (range 10)
              urls (map #(str %1 %2)
                        (repeat "http://fake-url/")
                        suffixes)
              x (doall (process-urls-lazy urls))]
          x))

(defn process-urls-into-map
  "Process all the urls, 10 at a time so as not to overwhelm the system"
  [urls]
  (into []
        (mapcat (fn [batch]
                  (let [the-futures (mapv #(future (http-simulator %))
                                          batch)
                        the-results (mapv deref the-futures)]
                    the-results))
                (partition-all 10 urls))))

(defn process-urls
  "Process all the urls, 10 at a time so as not to overwhelm the system"
  [urls]
  (reduce (fn [acc batch]
            (let [the-futures (mapv #(future (http-simulator %))
                                    batch)
                  the-results (mapv deref the-futures)]
              (concat acc the-results)))
          []
          (partition-all 10 urls)))

;; how much time does this take ?
;; it should be around 10 seconds
(time (let [suffixes (range 100)
            urls (map #(str %1 %2)
                      (repeat "http://fake-url/")
                      suffixes)]
        (process-urls urls)))

#_(defn process-urls
  "Process all the urls, 10 at a time so as not to overwhelm the system"
  [urls]
  (loop [urls urls
         results []]
    (if-not (seq urls)
      results
      (let [this-batch (take 10 urls)
            the-futures (mapv #(future (http-simulator %))
                              this-batch)
            the-results (mapv deref the-futures)]
        (recur (drop 10 urls)
               (into results the-results))))))

(deftest a-lot-of-futures
  (let [suffixes (range 100)
        urls (map #(str %1 %2)
                  (repeat "http://fake-url/")
                  suffixes)]
    (is (= suffixes (process-urls urls)))))

(comment 
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

  )























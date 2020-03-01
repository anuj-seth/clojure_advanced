(ns the-tests.promised-data-pipeline-test
  (:require  [clojure.test :refer :all]))

;; create a data pipeline
;; you will be given an arbitrary number of functions
;; of 3 arguments - a stage number, input promise and
;; output promise.
;; all functions do some initialization actions,
;; wait for a promise to be delievered
;; and deliver their result also to a promise

(defn a-stage
  [stage-number in-channel out-channel]
  ;; initialize
  (Thread/sleep (+ 1000 (rand-int 2000)))
  (let [input @in-channel]
    ;; do some work here
    (deliver out-channel (+ input 1))))

(deftest data-pipeline
  (let [number-of-stages (rand-int 10)
        stages (repeat number-of-stages a-stage)
        promises (take (inc number-of-stages)
                       (repeatedly #(promise)))]
    (doall (map #(future (apply %1 %2 %3))
                stages
                (range)
                (partition 2 1 promises)))
    (deliver (first promises) 1)
    (is (= (inc number-of-stages)
           @(last promises)))))

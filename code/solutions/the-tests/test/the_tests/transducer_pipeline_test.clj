(ns the-tests.transducer-pipeline-test
  (:require  [clojure.test :refer :all]
             [clojure.core.async :as async]))

(def xform (comp (filter odd?) (map inc)))

(defn process
  [items]
  (let [out (async/chan 100 xform)
        in (async/to-chan items)]
    (async/go-loop []
      (if-some [item (async/<! in)]
        (do
          (async/>! out item)
          (recur))
        (async/close! out)))
    (async/<!! (async/reduce conj
                             []
                             out))))
(defn pipeline-process
  [items]
  (let [out (async/chan 100)
        in (async/to-chan items)]
    (async/pipeline 4 out xform in)
    (async/<!! (async/reduce conj
                             []
                             out))))

(deftest a-pipeline
  (is (= (pipeline-process (range 10))
         (process (range 10)))))



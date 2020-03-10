(ns embedded-code.transducers)

;; sample(intro)
;; a number of sequence producing functions
;; return transducers when called with 1 argument
(map inc)
;; this can be applied to a collection 
;; + here is a reducing function
(transduce (map inc) + [1 2 3])

;; 5 here is the initial value
(transduce (map inc) + 5 [1 2 3])
;; reduce the result of (map inc [1 2 3])
;; using + and 5 as initial value
;; end-sample

;; sample(intro-2)
;; transducers are reducing functions.
;; map and filter are reducing functions
;; which seems strange until you think of them
;; reducing one collection to another collection
;; end-sample

;; sample(comp)
;; transducers can be stacked using composition
(def xform (comp (map inc) (filter odd?)))

;; notice the order of application of the
;; transducers
(transduce xform conj [] [0 1])
;=> [1]

(into [] xform [0 1])
;; though they might seem similar to ->>
;; transducer are applied to each element of
;; the collection without creating intermediate
;; sequences
;; end-sample

;; sample(sequence)
;; transduce is eager
;; use sequence for lazy transformation
(def transducer
  (comp
   (filter even?)
   (take 5)
   (map inc)))

(sequence transducer (range))
;; end-sample

;; sample(channel)
;; more interesting is the fact that channels
;; and transducers can be combined
(let [c (async/chan 1 (map count))]
  (async/>!! c "hello")
  (println (async/<!! c))
  (async/>!! c "world")
  (println (async/<!! c))
  (async/close! c)
  (async/<!! c))
;; end-sample

;; sample(channel-side-note)
;; a side-note:
;; channel can also be associated with
;; sequence generating functions
(let [c (async/to-chan (range 5))]
  (loop []
    (when-let [v (async/<!! c)]
      (println v)
      (recur))))
;; end-sample

;; sample(pipeline)
;; pipeline brings this all together
(let [out (async/chan 100 (map count))
      in (async/to-chan ["hello" "cruel" "world"])]
  (async/pipeline 4 out identity in)
  (async/<!! (async/reduce conj
                           []
                           out)))
;; end-sample

;; sample(pipeline-test)
;; open the file transducer_pipeline_test.clj
;; implement the function pipeline-process
;; and make the test case pass
;; end-sample

(ns embedded-code.concurrency-parallelism)

;; sample(pvalues)
;; let's first take look at some functions that can 
;; parallelize your code without requiring any code changes

;; pvalues returns a lazy sequence of values of the
;; expressions evaluated in parallel
(defn sleeper 
  [s thing]
  (Thread/sleep (* 1000 s))
  thing)

(defn pvs
  []
  (pvalues (sleeper 2 :1st)
           (sleeper 30 :2nd)
           (keyword "3rd")))
;; end-sample

;; sample(pvalues-time)
;; let's see how much time it takes to get the first
;; value
(time (first (pvs)))
;=> "Elapsed time: 2003.992906 msecs"
;=> :1st

;; the time taken for the first element is equal to
;; the time taken to realize that value
;; but we will see that the time taken to realize subsequent
;; values is as much as the most expensive element before it.
(time (last (pvs)))

;=> "Elapsed time: 30007.241914 msecs"
;=> :3rd
;; end-sample

;; sample(pvalues-time-explanation)
;; the reason for this is that clojure maintains a sliding
;; window within which all elements are realized and
;; the total time taken is that for the most expensive
;; computation

;; generally, the sliding window is of size N + 2,
;; where N is the number of cores
;; end-sample

;; sample(pmap)
;; pmap is the parallel version of map
;; note the doall to realize the lazy seq
(time (doall
       (pmap (comp inc
                   (partial sleeper 2))
             [1 2 3])))
;=> Elapsed time: 1992.638618 msecs
;=> (2 3 4)

;; the total cost of realizing the sequence is again
;; bound by the most expensive operation

;; pmap seems to work very well for the above example,
;; so should we replace all calls to map with pmap ?
;; end-sample

;; sample(pmap-experiment)
;; let's do an experiment
;; define a range of 10 million numbers
;; pmap and map over it using a function
;; that multiplies each number by itself

;; what is the time taken by pmap ?
;; by map ?
;; end-sample

;; sample(pmap-time)
(let [l (range 10000000)]
  (time (dorun (pmap #(* % %) l))))
;=> "Elapsed time: 22739.495523 msecs"

(let [l (range 10000000)]
  (time (dorun (map #(* % %) l))))
;=> "Elapsed time: 1114.723023 msecs"
;; end-sample

;; sample(pmap-time-explanation)
;; whether we should or not replace map with pmap depends
;; on the use case.
;; there is some overhead associated with sending work off to
;; threads, co-ordinating result gathering in proper order.
;; if you are sure that your function cost is higher than
;; this overhead then use pmap.

;; as with all parallel systems, you have to experiment
;; and see what works for your use case. 
;; end-sample

;; sample(pcalls)
;; finally there's the pcalls function which takes an
;; arbitrary number of no argument functions
;; and executes them in parallel
(time (doall 
       (pcalls #(sleeper 2 :first)
               #(sleeper 3 :second)
               #(keyword "3rd"))))

;;=> "Elapsed time: 3003.252293 msecs"
;;=> (:first :second :3rd)            

;; pcalls has the same benefits and trade-offs as pvalues
;; and pmap
;; end-sample



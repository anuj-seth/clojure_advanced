(ns the-tests.do-primes-test
  (:require  [clojure.test :refer :all]))

(defn prime?
  [n]
  (cond
    (= n 0) false
    (= n 1) false
    (= n 2) true
    :else (not-any? #(zero? (mod n %))
                    (range 2 n))))

(defn next-prime
  "Return the next prime greater or equal to n"
  [n]
  (first (filter prime?
                 (iterate inc n))))

(defn primes-between
  [start end]
  (filter prime?
          (range start (inc end))))


(defmacro do-primes
  "this is like the doseq macro
   but simpler"
  [[sym start end] & body]
  `(for [~sym (primes-between ~start ~end)]
     (do 
       ~@body)))

(deftest do-primes-test
  (is (= [2 3 5 7]
         (do-primes [p 0 9]
                    p)))
  (is (= 25
         (count (do-primes [p 0 100]
                           (println p)
                           p)))))
(let [p -100]
  (count (do-primes [p 0 100]
                    (println p)
                    p)))
(comment

  (= 25
     (count
      (filter prime?
              (range 1 101))))

  (= 2 (next-prime-2 1))

  (= 2 (next-prime-2 2))

  (= 23 (next-prime-2 20))

  (primes-between 0 9)
  (macroexpand-1 '(do-primes [p 0 9]
                             (println "prime is:" p)
                             (+ p 2)))

  (macroexpand-1 '(do-primes [p 0 9]
                             p))
  (do-primes [p 0 9]
             p)

  
  )

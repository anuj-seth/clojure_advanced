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

(defmacro do-primes
  "This is like the doseq macro but simpler.
  Takes a symbol, start number and end number.
  Executes the body forms binding symbol with the next
  prime on each iteration"
  [__]
  __)

(deftest do-primes-test
  (is (= [2 3 5 7]
         (do-primes [p 0 9]
                    p)))
  (is (= 25
         (count (do-primes [p 0 100]
                           (println p)
                           p)))))



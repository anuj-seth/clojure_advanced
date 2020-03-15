(ns the-tests.lsl-multi-test
  (:require  [clojure.string :as string]
             [clojure.java.io :as io]
             [clojure.test :refer :all]))

(defmulti parse #(subs %1 0 4))


(defmethod parse :default
  [& args]
  (throw (ex-info "No matching parser found"
                  {:args args})))

(parse "SVCLFOWLER         10101MS0120050313.........................")
(deftest lsl-test
  (is (=
       [{:type :#123}
        {:type :svcl
         :customer-name "FOWLER        "
         :customer-id "1010"
         :call-type-code "MS0"
         :date-of-call-string "20050313"}
        {:type :svcl
         :customer-name "HOHPE         "
         :customer-id "1020"
         :call-type-code "DX0"
         :date-of-call-string "20050315"}
        {:type :svcl
         :customer-name "TWO           "
         :customer-id "1030"
         :call-type-code "MRP"
         :date-of-call-string "20050329"}
        {:type :usge
         :customer-id "1030"
         :customer-name "TWO          "
         :cycle "7"
         :read-date "05032"}]
       (with-open [sample-log (io/reader (io/resource "sample.log"))]
         (doall
          (map parse
               (line-seq sample-log)))))))


(defmacro m
  [x]
  `('~x ~(map inc x)))

(macroexpand-1 '(m (1 2 3)))


(= [6 1 2 3 4 5] (swap! atomic
                        (fn [old-value & more] )
                        1 2 3 4 5))












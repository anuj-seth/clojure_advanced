(ns the-tests.lsl-test
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.test :refer :all]))

(defmulti parse #(subs %1 0 4))

(defmethod parse :default
  [& args]
  (throw (ex-info "No matching parser found"
                  {:args args})))

;; The defmapping macro creates a multimethod definition
;; for each mapping type defined below
(defmacro defmapping
  [__]
  __)


;; We want users to be able to give parsing rules for lines
(defmapping header "#123")

;; in this case service-call is just a friendly name for user convenience
;; "SVCL" is the identifier that occurs in the first 4 characters of the line
;; characters between position 4 (inclusive) and 17 (inclusive) denote customer-name
;; and so on.
;; note: character numbering starts from zero
;; an input line like below
;; SVCLFOWLER         10101MS0120050313.........................
;; will produce the following output
;; {:type :svcl,
;;  :customer-name "FOWLER        ",
;;  :customer-id "1010",
;;  :call-type-code "MS0",
;;  :date-of-call-string "20050313"}
(defmapping service-call "SVCL"
  (4 17 customer-name) 
  (19 22 customer-id)
  (24 26 call-type-code)
  (28 35 date-of-call-string))

(defmapping usage "USGE"
  (4 7 customer-id)
  (9 21 customer-name)
  (30 30 cycle)
  (31 35 read-date))

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

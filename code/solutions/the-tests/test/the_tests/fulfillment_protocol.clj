(ns the-tests.fulfillment-protocol
  (:require  [clojure.test :as t]))

;; from Stuart Sierra's article

(defprotocol Fulfillment
  (invoice [this] "Returns an invoice")
  (manifest [this] "Returns a shipping manifest"))

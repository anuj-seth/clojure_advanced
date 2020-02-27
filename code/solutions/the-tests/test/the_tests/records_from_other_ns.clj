(ns the-tests.records-from-other-ns
  (:require [the-tests.my-records :as records]))

(records/->a-sample-record 1 2 3)


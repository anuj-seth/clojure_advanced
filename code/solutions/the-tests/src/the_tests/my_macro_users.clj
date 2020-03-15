(ns the-tests.my-macro-users
  (:require [the-tests.my-macros :refer :all]))

(defn get-from-hive
  []
  ;; connect to hive
  ;; run query
  ;; return result
  ;;:hello
  )

(macroexpand-1 '(ulta-when (get-from-hive)
                           (+ 1 2)
                           :we-are-done))

(not '(get-from-hive))

(not '(1))

(ulta-when (get-from-hive)
           (+ 1 2)
           :we-are-done)

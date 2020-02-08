(ns the-tests.protocols
  (:require  [clojure.test :as t]))

;; (defprotocol P
;;   (foo [x])
;;   (bar-me [x]))

;; (deftype Foo [a b c]
;;   P
;;   (foo [x] a)
;;   (bar-me [x] b)
;;   (bar-me [x y] (+ c y)))

;; above does not work since
;; two argument bar-me is not defined in the defprotocol

(defprotocol P
  (foo [x])
  (bar-me [x] [x y]))


(deftype Foo [a b c]
  P
  (foo [x] a)
  (bar-me [x] b)
  (bar-me [x y] (+ c y)))

(bar-me (Foo. 1 2 3))

(bar-me (let [x 42]
          (reify P
            (foo [this] x))))

;; an implementation can choose not to define a method
;; but you get an error when you try to call this method 

(ns the-tests.tree-walk-test
  (:require [clojure.test :refer :all]
            [clojure.walk :as walk]))

;; the arithmetic expression is
;; 1-2/(3-4)+5*6
;; the parse tree looks like this
;;              +
;;            /   \
;;           -     *
;;         /   \  /  \
;;         1   /  5   6
;;            / \
;;           2   -
;;              / \
;;             3   4

(defn expression-tree
  []
  [:expr
   [:add
    [:sub
     [:number "1"]
     [:div [:number "2"] [:sub [:number "3"] [:number "4"]]]]
    [:mul [:number "5"] [:number "6"]]]])

(defmulti eval-node first)

(defmethod eval-node :number
  [[_ number]]
  (read-string number))

(defmethod eval-node :mul
  [[_ lhs rhs]]
  (* lhs rhs))

(defmethod eval-node :add
  [[_ lhs rhs]]
  (+ lhs rhs))

(defmethod eval-node :sub
  [[_ lhs rhs]]
  (- lhs rhs))

(defmethod eval-node :div
  [[_ lhs rhs]]
  (/ lhs rhs))

(defmethod eval-node :expr
  [[_ x]]
  x)

(deftest expr-parser-test
  (is (= 33 (walk/postwalk #(if (vector? %)
                              (eval-node %)
                              %)
                           (expression-tree)))))



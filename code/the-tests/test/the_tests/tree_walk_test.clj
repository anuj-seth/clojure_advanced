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



(deftest expr-parser-test
  (is (= 33 (walk/postwalk #(if (vector? %)
                              (eval-node %)
                              %)
                           (expression-tree)))))



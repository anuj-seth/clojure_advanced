(ns the-tests.hierarchical-tree-walk-test
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
   [:arithmetic/add
    [:arithmetic/sub
     [:number "1"]
     [:arithmetic/div [:number "2"] [:arithmetic/sub [:number "3"] [:number "4"]]]]
    [:arithmetic/mul [:number "5"] [:number "6"]]]])

(derive :arithmetic/add :arithmetic/binary)
(derive :arithmetic/sub :arithmetic/binary)
(derive :arithmetic/mul :arithmetic/binary)
(derive :arithmetic/div :arithmetic/binary)

;; defmulti does not let you redefine
;; the dispatch function on the fly
;; do ns-unmap then re-eval dispatch
;; as well as all methods
;;(ns-unmap *ns* 'eval-node)

(defmulti eval-node first)

(defmethod eval-node :number
  [[_ number]]
  (read-string number))

(defmethod eval-node :arithmetic/binary
  [[op lhs rhs]]
  (let [binary-ops {:arithmetic/add +
                    :arithmetic/sub -
                    :arithmetic/div /
                    :arithmetic/mul *}]
    ((binary-ops op) lhs rhs)))

(defmethod eval-node :expr
  [[_ x]]
  x)

(deftest expr-test
  (is (= 33 (walk/postwalk #(if (vector? %)
                              (eval-node %)
                              %)
                           (expression-tree)))))


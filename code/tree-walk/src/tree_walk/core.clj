(ns tree-walk.core
  (:require [clojure.walk :as walk])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

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


(defmulti eval-node (fn [x]
                      (println x)
                      (first x)))

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

(walk/postwalk #(if (vector? %)
                  (eval-node %)
                  %)
               (expression-tree))

(walk/prewalk #(do
                  (println "visiting " %)
                  %)
               (expression-tree))

(walk/walk #(do
              (println "inner visiting " %)
              %)
           #(do
              (println "outer visiting " %)
              %)
              (expression-tree))

(tree-seq vector? #(do (println "visiting " %)
                    (seq %))
          (expression-tree))

(vector? (seq? [1 2]))

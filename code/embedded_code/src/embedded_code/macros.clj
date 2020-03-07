(ns embedded-code.macros)

;; sample(intro)
;; in programming languages like java or c
;; the compiler translates your program text
;; into an intermediate form called the AST but
;; this AST is not accessible to you, the programmer

;; in clojure and other lisps the textual representation
;; of your code is coverted to a list data structure which
;; is the AST
;; hence, in clojure all your code is data
;; end-sample

;; sample(eval)
;; evaluate the form as a data structure
(eval "(+ 1 2)")
;=> "(+ 1 2)"

(eval (list + 1 2))
;=> 3

;; note that eval works on a list data structure
;; not a textual representation of it.
(eval (concat (list conj [1 2]) [3]))
;;=> [1 2 3]
;; end-sample

;; sample(eval-2)
;; let's take it a step further
(eval (list 'def 'x
            (list map inc '(list 1 2 3))))
;=> #'user/x

(type x)
;=> clojure.lang.LazySeq
x
(2 3 4)

;; code generators in other languages generate text,
;; but manipulating data is more powerful than manipulating
;; text
;; end-sample

;; sample(infix)
;; macros are code that return lists to be evaluated
;; by the clojure interpreter.
;; another way of saying that is - the body of the
;; macro is evaluated and whatever it returns is
;; evaluated
(defmacro infix
  [[left-operand operator right-operand]]
  (list operator left-operand right-operand))

(infix (2 + 3))

;; the arguments to a macro are not evaluated.
;; try writing infix as a normal function
;; end-sample

;; sample(quote)
;; we can create lists in macros using list
;; but there's another way.
;; the syntax quote

;; we saw this earlier
(eval (list + 1 2))

;; let's use a syntax quote
;; and see if you get the same result
(eval `(+ 1 2))

;; can you run this ?
`(+ 1 2)
;; end-sample

;; sample(quote-2)
(eval (list 'def 'x
            (list map inc '(list 1 2 3))))

;; ~ is called unquote
(eval `(def x (map inc
                   '~(list 1 2 3))))

;; ~@ is unquote splicing
(let [z [1 2 3]]
  `(+ ~@z))
;; end-sample

;; sample(quote-infix)

(defmacro infix
  [[left-operand operator right-operand]]
  (list operator left-operand right-operand))

;; can you convert this to use the syntax quote ?
;; end-sample

;; sample(quote-infix-answer)

(defmacro infix
  [[left-operand operator right-operand]]
  `(~operator ~left-operand ~right-operand))

;; end-sample

(defmacro infix
  [[operand-one operator operand-two & more]]
  `(for [[op# arg#] (reverse (partition 2 ~more))]
     (op# arg#)
   ))
(infix (2 + 3 - 4 + 5 + 7))

(defn infix
  [& args]
  (loop [[lhs op rhs & more] args]
    (if (nil? op)
      lhs
      (recur (cons (op lhs rhs)
                   more)))))

(infix 2 + 3 - 4 + 5 + 7)

(defmacro infix
  [[lhs op rhs & args]]
  `(~op ~lhs ~rhs))


(macroexpand-1
 (list 'def 'lucky-number (concat (list '+ 1 2) [10])))
                                        ;=> (def lucky-number (+ 1 2 10))

(macroexpand-1 `(def lucky-number ~(concat '(+ 1 2) [10])))
                                        ;=> (def user/lucky-number (+ 1 2 10))

```
</section>


(defmacro dot-product
  [[a b c]]
  (println a b c)
  (b a c))

(defn dot-product
  [[a b c]]
  (b a c))

(ns embedded-code.macros)

;; sample(intro)
;; in programming languages like java or c
;; the compiler translates your program text
;; into an intermediate form called the AST but
;; this AST is not accessible to you, the programmer

;; in clojure and other lisps the textual representation
;; of your code is coverted to a list data structure which
;; is the AST
;; hence, in clojure all your code is data and can be
;; manipulated by the language.
;; this also goes by the fancy name of homoiconicity
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
;; the syntax quote - a reader macro -
;; is executed when clojure reads your program

;; we saw this earlier
(eval (list + 1 2))

;; let's use a syntax quote
;; and see if you get the same result
(eval `(+ 1 2))
;; can you run this on the repl ?
`(+ 1 2)
;=> (clojure.core/+ 1 2)
;; end-sample

;; sample(quote-2)
;; symbols inside a syntax quoted form are
;; namespace qualified to avoid being overshadowed
;; in the namespace they are used in
;; atomic values - numbers, strings - evaluate
;; to themselves
`x
`"abcd"

(def x +)
(def y 1)
(def z 1)
`(x y z)
;=> (user/x user/y user/z)
(eval `(x y z))
;=> 2
;; end-sample

;; sample(unquote)
;; anything preceded by ~ is unquoted
(def x 1)
`(+ 1 ~x)
;=> (clojure.core/+ 1 1)

;; do you see a difference between what above
;; returns and what below would return ?
`(+ 1 x)
;; end-sample

;; sample(unquote-example)
;; can you convert this to use
;; syntax quote/unquote ?
(eval (list 'def 'x
            (list map inc '(list 1 2 3))))
x
;=> (2 3 4)
;; note: there are multiple ways of
;; doing this
;; and all might trip you up while handling
;; (1 2 3)
;; end-sample

;; sample(unquote-example-answer)
;; these differ in when the map is run
`(def x (map inc '~(list 1 2 3)))

`(def x '~(map inc '(1 2 3)))

;; eval'ing either should return the same result
(eval `(def x (map inc
                   '~(list 1 2 3))))
x
;=> (2 3 4)
;; end-sample

;; sample(macroexpand)
;; debugging macros is tough but macroexpand can help.
;; it evaluates the first symbol of the form
;; if it is a macro
(macroexpand-1 '(when 1 2))
;;=> (if 1 (do 2))

;; if the first symbol is not a macro then it returns
;; the form as is
(macroexpand-1 '(+ 1 2))
;;=> (+ 1 2)

;; macroexpand repeatedly calls macroexpand-1 until the
;; first symbol is no longer a macro
;; clojure.walk/macroexpand-all recursively expands until
;; no macros remain in the entire form
;; end-sample

;; sample(macroexpand-2)
;; try macro expanding our infix macro
(defmacro infix
  [[left-operand operator right-operand]]
  (list operator left-operand right-operand))

;; can you explain the output of this ?
(macroexpand-1 '(infix (2 + 3)))
;;=> (+ 2 3)
;; end-sample

;; sample(quote-infix)
;; can you convert this to use the syntax quote ?
(defmacro infix
  [[left-operand operator right-operand]]
  (list operator left-operand right-operand))
;; end-sample

;; sample(quote-infix-answer)
(defmacro infix
  [[left-operand operator right-operand]]
  `(~operator ~left-operand ~right-operand))
;; end-sample

;; sample(unquote-splice)
;; ~@ is unquote splicing
(let [z [1 2 3]]
  `(+ ~@z))

;; can you write a macro that takes a list
;; of numbers as argument, increments each number by 1
;; and then adds them up
;; try writing it without using unquote splice first
;; and then with unquote splice
;; hint: how do you apply a function over a list ?
;; end-sample

;; sample(list-add)
;; if your first attempt was this
;; and you got an error, let's see why
(defmacro list-add
  [l]
  `(apply + ~(map inc l)))

(macroexpand-1 '(list-add [1 1 1]))
;=> (clojure.core/apply clojure.core/+ (2 2 2))

(defmacro list-add
  [l]
  `(apply + '~(map inc l)))

(macroexpand-1 '(list-add [1 1 1]))
;=> (clojure.core/apply clojure.core/+ (quote (2 2 2)))
;; end-sample

;; sample(list-add-splice)
;; using unquote splicing we get
(defmacro list-add
  [l]
  `(+ ~@(map inc l)))

(list-add (1 1 1))
;=> 6
(macroexpand-1 '(list-add (1 1 1)))
;=> (clojure.core/+ 2 2 2)
;; end-sample

;; sample(quoting-101-1)
;; quoting 101
;; quoting especially nested quotes are hard to parse
;; - for people
;; in nested syntax quotes innermost form is expanded first.
;; if several ~ occur in a row, then leftmost ~ belongs to
;; innermost syntax-quote.
;; put another way - tilde matches a syntax-quote if
;; there are the same number of tildes as syntax-quotes
;; between them.
  ``(~~a)
;;12 21
;; the numbers tell which ~ matches which syntax quote
;; and also the order in which they will be expanded
;; end-sample

;; sample(quoting-101-2)
;; let's work through an example - step by step
;; the setup
(def x `a)
(def y `b)
(def a 1)
(def b 2)
;; the expression
``(w ~x ~~y)

;; first step - remove first syntax quote
`(user/w ~user/x ~user/b)

;; second step - remove second syntax quote
(user/q user/a 2)
;; end-sample

;; sample(quoting-101-3)
;; THIS IS NOT USED
;; see if you can understand the outputs of this
;; try on your own before running the let block
(let [x 9, y '(- x)]
  (println "1 " `y)
  (println "2 " ``y)
  (println "3 " ``~y)
  (println "4 " ``~~y))
;; end-sample

;; sample(quoting-101-4)
;; THIS IS NOT USED
(let [x 9, y '(- x)]
  (println "1 " `y)
  (println "2 " ``y) 
  (println "3 " ``~y)
  (println "4 " ``~~y))
;;=> 1  lsl.core/y
;;=> 2  (quote lsl.core/y)
;;=> 3  lsl.core/y
;;=> 4  (- x)
;; end-sample

;; sample(ulta-when)
;; writing your own control structures is another
;; tradition while learning macros
;; and it also shows off the flexibility of the
;; language - try writing a control structure in Java

;; let's assume that when-not does not exist.
;; we will write our own control structure called
;; ulta-when
;; ulta-when evaluates the body forms when the test
;; expression is false
;; end-sample

;; sample(ulta-when-as-function)
;; let's try to write this as a function
(defn ulta-when
  [test body]
  (if (not test)
     body))

;; i expect this to not print anything
(ulta-when true (println "test is false"))

;; i expect this to print the message
(ulta-when false (println "test is false"))
;; end-sample

;; sample(ulta-when-as-macro)
(defmacro ulta-when
  [test body]
  `(if (not ~test)
     ~body))

;; i expect this to not print anything
(ulta-when true (println "test is false"))

;; i expect this to print the message
(ulta-when false (println "test is false"))
;; end-sample

;; sample(ulta-when-multiple-forms)
;; what happens if i want multiple forms evaluated
;; in the body ?
(ulta-when false
           (println "test is false")
           (+ 1 2))

;; can you make this work ?
;; end-sample

;; sample(ulta-when-unquote-splice)
(defmacro ulta-when
  [test & body]
  `(if (not ~test)
     (do ~@body)))
;; end-sample

;; sample(do-primes)
;; open the file do_primes_test.clj
;; and make the test cases pass.

;; there's one helper function already there
;; write more if you need
;; end-sample

;; sample(with-timing)
;; i want to write a macro called with-timing
;; it will print the time it takes to evaluate
;; arbitrary input forms.
;; it's return value should be what the input forms
;; evaluate to

(with-timing
  (+ 1 2 3)
  (clojure.string/reverse "abcd"))
;;=> Time taken: 2 msecs
;; "dcba"
;; end-sample

;; sample(with-timing-ns-qualified-symbols)
;; here's an attempt
(defmacro with-timing
  [& body]
  `(let [start (System/currentTimeMillis)
         v (do ~@body)
         end (System/currentTimeMillis)]
     (println "Time taken: " (- end start))
     v))

;; on running this we get an error
;; let's see that this macro expands to
(clojure.core/let [user/start (java.lang.System/currentTimeMillis)
                   user/v (do (+ 1 2))
                   user/end (java.lang.System/currentTimeMillis)]
  (clojure.core/println "Time taken: " (clojure.core/- user/end user/start))
  user/v)
;; end-sample

;; sample(with-timing-unqualified-symbols)
;; how can we get unqualified symbols in macros ?
;; if an unquote returns a symbol it remains unqualified
`~'c
`~(symbol "c")

;; changing the macro we get this
(defmacro with-timing
  [& body]
  `(let [~'start (System/currentTimeMillis)
         ~'v (do ~@body)
         ~'end (System/currentTimeMillis)]
     (println "Time taken: " (- ~'end ~'start))
     ~'v))
;; end-sample

;; sample(with-timing-variable-capture)
;; and it seems to work
(with-timing (Thread/sleep 1000))
;;=> Time taken:  1000
;;=> nil

;; but what happens when you run this ?
(let [start 1000]
  (with-timing (Thread/sleep start)))

;; try using macroexpand-1 to see what is going
;; on.
(let [start 1000]
  (macroexpand-1 '(with-timing (Thread/sleep start))))
;; end-sample

;; sample(with-timing-variable-capture-expand)
(clojure.core/let [start (java.lang.System/currentTimeMillis)
                   v (do (Thread/sleep start))
                   end (java.lang.System/currentTimeMillis)]
  (clojure.core/println "Time taken: " (clojure.core/- end start))
  v)

;; you have been bitten by variable capture
;; also called unhygienic macro

;; note: the trick ~'c has it's uses
;;       remember it when we talk about LSL
;; end-sample

;; sample(with-timing-gensym-intro)
;; so how do we write hygienic macros ?
;; use local vars with really weird names
;; or we could use gensym to get unique symbols
(gensym)
;;=> G__2158
(gensym "ABCD")
;;=>ABCD2161

;; and there are also auto-gensyms
`x#
;;=> x__2162__auto__

`(x# x#)
;;=> (x__2165__auto__ x__2165__auto__)
;; end-sample

;; sample(with-timing-gensym-exercise)
;; rewrite the with-timing macro to use auto gensyms
;; end-sample

;; sample(with-timing-gensym-answer)
(defmacro with-timing
  [& body]
  `(let [start# (System/currentTimeMillis)
         v# (do ~@body)
         end# (System/currentTimeMillis)]
     (println "Time taken: " (- end# start#))
     v#))
;; end-sample

;; sample(with-timing-gensym-expand)
(let [start 1000]
  (macroexpand-1 '(with-timing (Thread/sleep start))))

(clojure.core/let [start__2168__auto__ (java.lang.System/currentTimeMillis)
                   v__2169__auto__ (do (Thread/sleep start))
                   end__2170__auto__ (java.lang.System/currentTimeMillis)]
  (clojure.core/println "Time taken: " (clojure.core/- end__2170__auto__
                                                       start__2168__auto__))
  v__2169__auto__)
;; end-sample

;; sample(macro-returning-fn)
;; the moral of the story -
;; always use generated symbols in let bindings
;; and function args in your macro generated forms

;; let bindings makes sense
;; but why in function args ?
;; function args are scoped by the function !!!
;; end-sample

;; sample(macro-returning-fn-variable-capture)
;; let's take an example
;; write a macro called join-join that takes a string
;; as input and returns a function of one string arg
;; that joins the two
((join-join "x") "y")
;;=> "xy"

(defmacro join-join
  [s]
  `(fn [~'l] (str ~s ~'l)))

((join-join "x") "y")
;;=> "xy"
;; end-sample

;; sample(macro-returning-fn-variable-capture-explained)
(defmacro join-join
  [s]
  `(fn [~'l] (str ~s ~'l)))

;; but what happens in this case ?
(def l "z")
((join-join l) "x")

;; i would assume to see an output "zx"
;; can you explain what happened and fix it ?
;; end-sample

;; sample(macro-returning-fn-gensym)
(defmacro join-join
  [s]
  `(fn [l#] (str ~s l#)))

;; or

(defmacro join-join
  [s]
  (let [fn-arg (gensym)]
    `(fn [~fn-arg] (str ~s ~fn-arg))))
;; end-sample

``(w ~x ~~@(list `a `b))
``(user/w ~user/x ~~@(list `a `b))
(defmacro list-add
  [l]
  `(+ ~@(map #(inc (eval %)) l)))

(defmacro list-add
  [l]
  `(apply + (map inc ~~l)))

(macroexpand-1 '(list-add (1 1 (+ 1 1))))

`(def 'x (map inc '~(list 1 2 3)))

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



```
</section>


(defmacro dot-product
  [[a b c]]
  (println a b c)
  (b a c))

(defn dot-product
  [[a b c]]
  (b a c))

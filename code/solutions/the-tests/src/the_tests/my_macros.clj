(ns the-tests.my-macros)

(defmacro ulta-when
  [test & body]
  `(if ~(not test)
     (do ~@body)))


(ns embedded-code.types)

;; sample(types)
;; not all things are records
;; use types when you want to specify
;; behaviour

;; generally used when you need java interop
(deftype Person [first-name last-name]
  Encryptable
  (encrypt [this] (str first-name last-name)))

(encrypt (Person. "Iron" "man"))
;;=> Ironman
;; end-sample

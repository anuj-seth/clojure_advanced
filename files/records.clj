;; sample(place-protocol)
;; how do records take part in protocols ?
(defprotocol Place
  (name [this])
  (description [this]))

(defrecord Airport [name city icao iata]
  Place
  (name [this] (:name this))
  (description [this] (str (:name this)
                           " has IATA code "
                           (:iata this))))
;; end-sample

;; sample(city)
(defrecord City [name country]
  Place
  (name [this] (:name this))
  (description [this] (str (:name this)
                           " is a city")))
;; end-sample

;; sample(usage)
(def delhi-airport (->Airport "Indira Gandhi International Airport"
                              "New Delhi"
                              "VIDP"
                              "DEL"))
(def delhi (->City "New Delhi" "India"))


(description delhi)
;=> "New Delhi is a city"
(description delhi-airport)
;=> "Indira Gandhi International Airport has IATA code DEL"
;; end-sample


;; sample(is-in)
(defprotocol Is-In
  (is-in [this]))

(extend-protocol Is-In
  Airport
  (is-in [this] (str (:name this)
                     " is in "
                     (:city this)))
  City
  (is-in [this] (str (:name this)
                     " is in "
                     (:country this))))
;; end-sample

;; sample(is-in-usage)
(is-in delhi)
;=> "New Delhi is in India"
(is-in delhi-airport)
;=> "Indira Gandhi International Airport is in New Delhi"

;; Place protocol is still implemented
(description delhi)
;=> "New Delhi is a city"
;; end-sample

;; sample(callable-records-short)
(defrecord Airport [name city icao iata]
  clojure.lang.IFn
  (invoke [this k] (get this k))
  (invoke [this k not-found] (get this k not-found)))
;; end-sample

;; sample(callable-records)
(defrecord Airport [name city icao iata]
  clojure.lang.IFn
  (invoke [this k] (get this k))
  (invoke [this k not-found] (get this k not-found))
  (applyTo [this args]
    (let [n (clojure.lang.RT/boundedLength args
                                           2)]
      (case n
        0 (throw (clojure.lang.ArityException.
                  n
                  (.. this (getClass) (getSimpleName))))
        1 (.invoke this (first args))
        2 (.invoke this (first args) (second args))
        3 (throw (clojure.lang.ArityException.
                  n
                  (.. this (getClass) (getSimpleName))))))))
;; end-sample

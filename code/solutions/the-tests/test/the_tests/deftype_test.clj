(ns the-tests.deftype-test
  (:require  [clojure.test :refer :all]))

(defprotocol Place
  (name [this])
  (description [this]))

(deftype Airport [name city icao iata lat lon]
  Place
  (name [this] name)
  (description [this] (str name
                           " has IATA code "
                           iata)))

(def delhi-airport (Airport. "Indira Gandhi International Airport"
                             "New Delhi"
                             "VIDP"
                             "DEL"
                             "28.5562"
                             "771000"))

(name (->Airport "Indira Gandhi International Airport"
                 "New Delhi"
                 "VIDP"
                 "DEL"
                 "28.5562"
                 "771000"))

(name delhi-airport)
(description delhi-airport)

(deftype City [name country lat lon]
  Place
  (name [this] name)
  (description [this] (str name " is a city")))

(def new-delhi (City. "New Delhi"
                      "India"
                      "28.6139"
                      "77.2090"))

(name new-delhi)
(description new-delhi)


;; let's say six months from now I have the
;; requirement to add function that reports the
;; location of the place.
;; we could modify the original Place protocol
;; or we could extend the protocol

(defprotocol Is-In
  (is-in [this]))

(extend-type Airport
  Is-In
  (is-in [this] (str (.name this) 
                     " is in "
                     (.city this))))

City
(is-in [this] (str name
                   " is in "
                   country))
(is-in delhi)
(is-in delhi-airport)

(.name delhi-airport)


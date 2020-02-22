(ns the-tests.core-test
  (:require [clojure.test :refer :all]
            [the-tests.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))


(defrecord Airport [icao iata])

(->Airport "ZBAA"
           "PEK")

(map->Airport {:icao "ZBAA"
               :iata "PEK"})

(= (->Airport "ZBAA"
              "PEK")
   (map->Airport {:icao "ZBAA"
                  :iata "PEK"}))

(:iata (map->Airport {:icao "ZBAA"}))

(:icao (map->Airport {:icao "ZBAA"}))

;; since the key exists with nil value
(= nil
   (:iata (map->Airport {:icao "ZBAA"}) :hello))

;; this key does not exist
(:iata0 (map->Airport {:icao "ZBAA"}) :hello)

(type (assoc (map->Airport {:icao "ZBAA" :iata "PEK"})
             :city "Beijing"))

(keys (assoc (map->Airport {:icao "ZBAA" :iata "PEK"})
             :city "Beijing"))

(defrecord Airport [name icao iata lat lon])

(->Airport "Indira Gandhi International Airport"
           "VIDP"
           "DEL"
           "28.5562"
           "771000")

(map->Airport {:name "Indira Gandhi International Airport"
               :icao "VIDP"
               :iata "DEL"
               :lat "28.5562"
               :lon "771000"})

(= (->Airport "Indira Gandhi International Airport"
              "VIDP"
              "DEL"
              "28.5562"
              "771000")
   (map->Airport {:name "Indira Gandhi International Airport"
                  :icao "VIDP"
                  :iata "DEL"
                  :lat "28.5562"
                  :lon "771000"}))

(defrecord City [name lat lon])

(->City "New Delhi" "28.6139" "77.2090")

(defprotocol Place
  (name [this])
  (description [this]))

(defrecord Airport [name city icao iata]
  Place
  (name [this] (:name this))
  (description [this] (str (:name this)
                           " has IATA code "
                           (:iata this))))

(defrecord City [name country]
  Place
  (name [this] (:name this))
  (description [this] (str (:name this) " is a city")))

(def delhi-airport (->Airport "Indira Gandhi International Airport"
                              "New Delhi"
                              "VIDP"
                              "DEL"))
(def delhi (->City "New Delhi" "India"))

(description delhi)
(description delhi-airport)

(name delhi)
(name delhi-airport)



;; can we inherit functions ?
;; does not seem like it since defprotocol
;; does not look at bodies



;; let's say six months from now I have the
;; requirement to add function that reports the
;; location of the place.
;; we could modify the original Place protocol
;; or we could extend the protocol

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

(is-in delhi)
(is-in delhi-airport)




(derive ::superman ::superhero)
(derive ::wonder-woman ::superhero)
(derive ::lex-luthor ::supervillain)


(isa? ::superman ::superhero)

(isa? ::superman ::supervillain)

(isa? ::lex-luthor ::supervillain)

(defmulti evil? :name)

(defmethod evil? ::superhero [_] :never)

(defmethod evil? ::lex-luthor [_] :always)

(evil? {:name ::superman})

(evil? {:name ::lex-luthor})

(derive ::batman ::superhero)
(defmethod evil? ::batman [_] :only-when-ends-justify-means)

(evil? {:name ::batman})



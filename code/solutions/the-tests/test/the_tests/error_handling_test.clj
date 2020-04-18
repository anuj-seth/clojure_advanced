(ns the-tests.error-handling-test
  (:require  [clojure.test :refer :all]
             [clojure.string :as string]))

(defn parse
  [{:keys [line] :as full-data}]
  (let [fields (string/split line #"\|")]
    {:data fields
     :status :ok}))

(defn validate
  [{:keys [data] :as full-data}]
  (if (= (count data) 5)
    full-data
    (assoc full-data 
           :status :error
           :msg "not enough fields in line")))

(defn transform
  [{:keys [data] :as full-data}]
  (let [ks [:short-date :timestamp :level :trx :log-msg]]
    {:status :ok
     :data (zipmap ks data)}))

(defn write-to-db
  [{:keys [data] :as full-data}]
  {:status :ok
   ;; below are presumably the ID column values
   ;; returned by the DB
   :data [1 2 3 4]})

(defn ok-or-never
  "Call f on the data as long as status is :ok"
  [f {:keys [status msg] :as data}]
  (if (= :ok status)
    (f data)
    data))

(deftest error-handler-test
  (let [data ["03/22|08:51:06|TRACE|...read_physical_netif|Home list entries returned = 7"
              "03/22|08:51:06|INFO|...read_physical_netif|index #0, interface VLINK1 has address 129.1.1.1, ifidx 0"
              "03/22|08:51:06|INFO|...read_physical_netif|index #1, interface TR1 has address 9.37.65.139, ifidx 1"
              "03/22|08:51:06|INFO|...read_physical_netif|index #2, interface LINK11 has address 9.67.100.1, ifidx 2"
              "03/22|08:51:06|INFO|index #3, interface LINK12 has address 9.67.101.1, ifidx 3"]
        after-processing (map #(->> {:status :ok :line %}
                                     (ok-or-never parse)
                                     (ok-or-never validate)
                                     (ok-or-never transform)
                                     (ok-or-never write-to-db))
                              data)]
    (is (= [:ok :ok :ok :ok :error]
           (map :status after-processing)))
    (is (vector? ((comp :data last)
                  after-processing)))))

(ns the-tests.channel-pipeline-test
  (:require  [clojure.test :refer :all]
             [clojure.string :as string]
             [clojure.core.async :refer [chan >!! <!! close! thread go <! >! go-loop]]))

(defn parse
  [line]
  ;;(println "in parse " line)
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

(defn stage
  [in out err f]
  (go (loop []
        (when-some [v (<! in)]
          ;;(println "in stage " v f)
          (let [{:keys [status] :as data} (f v)]
            (if (= status :ok)
              (>! out data)
              (>! err data))))
        (recur))
      (close! out)))

(defn setup-pipeline
  []
  (let [in (chan 10)
        to-validator (chan 10)
        to-transformer (chan 10)
        to-good (chan 10)
        to-error (chan 10)]
    (stage in to-validator to-error parse)
    (stage to-validator to-transformer to-error validate)
    (stage to-transformer to-good to-error transform)
    {:in in :out to-good :error to-error}))

(deftest channel-pipeline-test
  (let [data ["03/22|08:51:06|TRACE|...read_physical_netif|Home list entries returned = 7"
              "03/22|08:51:06|INFO|...read_physical_netif|index #0, interface VLINK1 has address 129.1.1.1, ifidx 0"
              "03/22|08:51:06|INFO|...read_physical_netif|index #1, interface TR1 has address 9.37.65.139, ifidx 1"
              "03/22|08:51:06|INFO|...read_physical_netif|index #2, interface LINK11 has address 9.67.100.1, ifidx 2"
              "03/22|08:51:06|INFO|index #3, interface LINK12 has address 9.67.101.1, ifidx 3"]
        {:keys [in out error]} (setup-pipeline)]
    (doseq [datum data]
      (println datum)
      (>!! in datum))
    (close! in)
    (is (= [:ok :ok :ok :ok]
           (loop [acc []]
             (if-let [v (<!! out)]
               (do (println acc)
                   (recur (conj acc (:status v))))
               acc))))
    ;;(is (vector? ((comp :data last)
    ;;            after-processing)))
    ))

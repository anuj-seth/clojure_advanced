(ns the-tests.channel-pipeline-test
  (:require  [clojure.test :refer :all]
             [clojure.string :as string]
             [clojure.core.async :as async]))

(defn parse
  [line]
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
  (async/go (loop []
              (if-some [v (async/<! in)]
                (let [{:keys [status] :as data} (f v)]
                  (if (= status :ok)
                    (async/>! out data)
                    (async/>! err data))
                  (recur))
                (async/close! out)))))

(defn setup-pipeline
  []
  (let [[in to-validator to-transformer to-good to-error] (repeatedly 5
                                                                      #(async/chan 10))]
    (stage in to-validator to-error parse)
    (stage to-validator to-transformer to-error validate)
    (stage to-transformer to-good to-error transform)
    {:in in :out to-good :error to-error}))

(defn drain-until-closed
  [channel]
  (async/<!! (async/reduce conj
                           []
                           channel)))

(deftest channel-pipeline-test
  (let [data ["03/22|08:51:06|TRACE|...read_physical_netif|Home list entries returned = 7"
              "03/22|08:51:06|INFO|...read_physical_netif|index #0, interface VLINK1 has address 129.1.1.1, ifidx 0"
              "03/22|08:51:06|INFO|...read_physical_netif|index #1, interface TR1 has address 9.37.65.139, ifidx 1"
              "03/22|08:51:06|INFO|...read_physical_netif|index #2, interface LINK11 has address 9.67.100.1, ifidx 2"
              "03/22|08:51:06|INFO|index #3, interface LINK12 has address 9.67.101.1, ifidx 3"]
        {:keys [in out error]} (setup-pipeline)
        good-values (async/thread (drain-until-closed out))
        error-values (async/thread (drain-until-closed error))]
    (doseq [datum data]
      (async/>!! in datum))
    (Thread/sleep 10000)
    (async/close! in)
    ;; no need to close out. why ?
    ;;(async/close! out)
    (async/close! error)
    (is (= [:ok :ok :ok :ok]
           (map :status 
                (async/<!! good-values))))
    (is (= [:error]
           (map :status
                (async/<!! error-values))))))

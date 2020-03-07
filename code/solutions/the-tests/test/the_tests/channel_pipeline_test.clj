(ns the-tests.channel-pipeline-test
  (:require  [clojure.test :refer :all]
             [clojure.string :as string]
             [clojure.core.async :as async]))

(defn parse
  [{:keys [data]}]
  (let [fields (string/split data #"\|")]
    {:data fields
     :status :ok}))

(defn validate
  [{:keys [data]}]
  (if (= (count data) 5)
    {:status :ok
     :data data}
    {:status :error
     :msg "not enough fields in line"
     :data data}))

(defn transform
  [{:keys [data]}]
  (let [ks [:short-date :timestamp :level :trx :log-msg]]
    {:status :ok
     :data (zipmap ks data)}))

(defn stage
  "Reads a value - v - from the in channel
  and calls the function f on each value.
  If (f v) has value of key :status as :ok then
  sends the (f v) data to out channel,
  else prints the return value of (f v) to the console
  prefixed with ERROR.
  Reads values from the in channel as long as it is not closed.
  Closes out channel before exit"
  [in out f]
  (async/go (loop []
              (if-some [v (async/<! in)]
                (let [{:keys [status] :as full-data} (f v)]
                  (if (= status :ok)
                    (async/>! out full-data)
                    (println "ERROR: " full-data))
                  (recur))
                (async/close! out)))))

(defn setup-pipeline
  []
  (let [[in to-validator to-transformer out] (repeatedly 4
                                                         #(async/chan 10))]
    (stage in to-validator parse)
    (stage to-validator to-transformer validate)
    (stage to-transformer out transform)
    {:in in :out out}))

(defn drain-until-closed
  [channel]
  (loop [acc []]
    (if-some [v (async/<!! channel)]
      (recur (conj acc v))
      acc)))

(deftest channel-pipeline-test
  (let [data ["03/22|08:51:06|TRACE|...read_physical_netif|Home list entries returned = 7"
              "03/22|08:51:06|INFO|...read_physical_netif|index #0, interface VLINK1 has address 129.1.1.1, ifidx 0"
              "03/22|08:51:06|INFO|...read_physical_netif|index #1, interface TR1 has address 9.37.65.139, ifidx 1"
              "03/22|08:51:06|INFO|...read_physical_netif|index #2, interface LINK11 has address 9.67.100.1, ifidx 2"
              "03/22|08:51:06|INFO|index #3, interface LINK12 has address 9.67.101.1, ifidx 3"]
        {:keys [in out]} (setup-pipeline)
        good-values (async/thread (drain-until-closed out))]
    (doseq [datum data]
      (async/>!! in {:status :ok
                     :data datum}))
    (async/close! in)
    ;; should we close the out channel ?
    ;; who closes the out channel ?
    (is (= [:ok :ok :ok :ok]
           (map :status
                (async/<!! good-values))))))


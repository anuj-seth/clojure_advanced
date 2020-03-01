(ns the-tests.log-agent
  (:require  [clojure.test :refer :all]))

(defn spit-log
  [msgs]
  (spit (str "out_"
             (rand-int 1000)
             "_"
             (.getTime (new java.util.Date))
             ".log")
        (str (clojure.string/join "\n"
                                  msgs)
             "\n")))

(let [logger (agent [])]
  (defn log
    "Write 10 messages to a file using the spit-log function
     when the agent holds >= 10 messages."
    [msg]
    (letfn [(writer [val msg]
              (let [new-val (conj val msg)]
                (if (>= (count new-val) 10)
                  (do
                    (spit-log (take 10 new-val))
                    (drop 10 new-val))
                  new-val)))]
      (send logger writer msg)))
  (defn close-log
    "Write all remaining log messages to a file using spit-log"
    []
    (send logger #(spit-log val))
    (shutdown-agents)))

(let [runners (doall (map #(future (log {:timestamp (.getTime (new java.util.Date))
                                         :msg %}))
                          (range 100)))
      returns (doall (map deref runners))]
  (Thread/sleep 2000)
  (close-log))



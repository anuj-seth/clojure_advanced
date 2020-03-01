(ns the-tests.log-agent)

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
              __)]
      (send logger writer msg)))
  (defn close-log
    "Write all remaining log messages to a file using spit-log"
    []
    (send logger #(spit-log val))
    ;; always call shutdown agents otherwise you will get
    ;; a one minute wait before clojure cleans up the agent threads
    (shutdown-agents)))

(let [runners (doall (map #(future (log {:timestamp (.getTime (new java.util.Date))
                                         :msg %}))
                          (range 100)))
      returns (doall (map deref runners))]
  (Thread/sleep 2000)
  (close-log))



(ns embedded-code.futures-delays-promises)

;; sample(futures)
;; using futures you can define a task and place it
;; on another thread without requiring the
;; result immediately.
(future (Thread/sleep 10000)
        (println "this will print after 10 seconds"))

;; notice how the repl can continue executing other tasks
;; while the future executes in another thread.

;; you can fire off a future and forget about it
;; but most likely you are interested in it's result
;; end-sample

;; sample(futures-result)
;; if you noticed when we ran the future we got a funny
;; output on the repl immediately.
;; a future returns a reference that we can use to query it.

;; we can deref a future to get it's value 
(let [result (future (println "this will print only once")
                     (+ 1 1))]
  (println "deref: " (deref result))
  (println "@: " @result))

;; notice that @ is shorthand for deref
;; dereferencing the future twice executes it
;; only once
;; end-sample

;; sample(futures-realized)
;; you can call realized? on a future to see if it 
;; is done running
(realized? (future (Thread/sleep 1000)))
;=> false

(let [f (future)]
  @f
  (realized? f))
;=> true

;; we can also supply a timeout while dereferncing futures
(deref (future (Thread/sleep 1000)
               0)
       10 ;; timeout
       5) ;; val to return on timeout
;; end-sample

;; sample(delays)
;; delays allow you to define a task without
;; having to execute it immediately or
;; requiring the result
(def a-simple-delay
  (delay (let [message "the message from delay"]
           (println "First deref:" message)
           message))) 

;; force or @ starts the execution of a delay.
;; a delay is executed only once and it's value cached
;; just like futures 
(force a-simple-delay)
@a-simple-delay
;; notice the difference in output
;; end-sample

;; sample(delay-example)
;; make your own lazy variables
;; or shared resources
(defn a-db-connection-pool
  []
  (println "creating pool")
  :pool)

(def a (delay (a-db-connection-pool)))

(dotimes [_ 100]
  (future @a))
;; note that even though @a will be executed multiple
;; times from different threads the delay body only
;; gets executed once.
;; end-sample


;; sample(promises)
;; promises are placeholders for values that can
;; be delivered by executing threads later on.
(def x (promise))

(def fut (future @x))

(deref fut 1000 :not-yet)

(deliver x 42)

(deref fut 1000 :not-yet)

@fut
;=> 42

;; promises block if we try to dereference them before
;; a value has been delivered.
;; calling deliver multiple times has no effect
;; end-sample


;; sample(promise-example)
;; let's try to see how we can use promises

;; we want to fire off a number of tasks but we are only
;; interested in a logically true return value
(let [my-result (promise)]
  (doseq [idx (range 10)]
    (future (if-let [answer (some-function idx)]
              (deliver my-result answer))))
  (println "and the answer is: " @my-result))

;; note that even though deliver might be called multiple times 
;; only the first one has any effect

;; also we can provide a time out for the deref
;; end-sample


;; sample(promise-example-2)
;; a use case for promises is as a callback
;; i.e. to execute some piece of code once some 
;; other code finishes
(defn do-long-boring-work
  [signal-complete]
  ;; do some work
  (Thread/sleep 10000)
  (deliver signal-complete :complete))

(let [the-promise (promise)
      _ (future (do-long-boring-work the-promise))]
  ;; do some other work here
  (println "wait for the promised value:" @the-promise))
;; end-sample


;; sample(delay-closure)
;; what if we want to write a function that
;; returns the connection pool
;; creating it only when asked the first time
;; how would you do it ?
;; end-sample

(let [conn-pool (delay (a-db-connection-pool))]
  (defn -connection-pool
    []
    @conn-pool))


(def global-connection-pool (atom (delay (a-db-connection-pool))))

@@global-connection-pool

(defn refresh-pool
  []
  (swap! global-connection-pool
         (fn [old-pool]
           (log "Closing " @old-pool)
           (delay (a-db-connection-pool)))))

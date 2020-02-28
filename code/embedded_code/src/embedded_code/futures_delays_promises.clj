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
;; you can also call realized? on a future to see if it 
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
;; having to execute it immediately or requiring the
;; result
(def a-simple-delay
  (delay (let [message "the message from delay"]
           (println "First deref:" message)
           message))) 

;; we can start the execution of a delay by using force or @.
;; just like futures a delay is executed only once and 
;; it's value cached.
(force a-simple-delay)

;; notice how the first time 2 messages are printed
;; and subsequently only the value of the delay is returned
@a-simple-delay
;; end-sample

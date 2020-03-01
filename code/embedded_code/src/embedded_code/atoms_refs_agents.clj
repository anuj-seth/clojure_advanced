(ns embedded-code.atoms-refs-agents)

;; sample(atoms)
;; atom is a type used to store and
;; atomically update values when required.
;; atoms are thread safe and can be used across threads
;; as light weight shared data.
(def counter (atom 0)) 

;; we can use the familiar deref operators to get the
;; atom's value
@counter
(deref counter)

;; note that blocking on dereference does not make
;; sense in case of atoms.
;; it will always return the atom's current value
;; end-sample

;; sample(atom-ops)
;; to update an atom to a new state we use swap!
(swap! counter inc)

;; dereferencing the atom after a swap! will show 
;; the new value
@counter
;=> 1

;; note that swap! works synchronously so your thread
;; will block until the update function returns

;; end-sample

;; sample(atom-ops-2)
;; we can also supply a function with multiple arguments
(swap! counter * 2 3)

;; this gets translated to (update-fn current-value args)

;; if you want to check the value before setting it
(compare-and-set! counter 6 600)
;=> 600

;; if you just want to update state without caring about
;; previous value you can just use reset!
(reset! counter -1)
;; end-sample

;; sample(atom-tests)
;; make the below test cases pass
(def atomic (atom []))

(= __ @atomic)

(= __ (do (swap! atomic conj 0)
          @atomic))

(= [6] (__ atomic __))

(= [6 1 2 3 4 5] (swap! atomic
                        (fn [__] __)
                        1 2 3 4 5))
;; end-sample

;; sample(atom-tests-solution)
(swap! atomic
       (fn [curr-val & args]
         (apply conj curr-val args))
       1 2 3 4 5)
;; end-sample


;; sample(refs)
;; refs are another type that allow you
;; to update the state of multiple entities using
;; transaction semantics
(def account-a (ref {:name "a" :balance 1000}))
(def account-b (ref {:name "b" :balance 200000}))

;; we can deref refs to get their value
(:balance @account-a)

;; now we want to transfer some money from account-b to a
;; but we want this done as a transaction like in databases
;; end-sample

;; sample(refs-2)
;; we use alter to  modify refs within a transaction started
;; by dosync
;; calling alter without an enclosing transaction causes
;; an error

(dosync
 (alter account-a update-in [:balance] + 1000)
 (alter account-b update-in [:balance] - 1000))

;; if we now check the values of the refs we see
@account-a
;=> {:name "a", :balance 2000}
@account-b
;=> {:name "b", :balance 199000} 
;; end-sample

;; sample(ref-set)
;; finally ref-set can be used to update the value of
;; any ref

(dosync
 (ref-set account-a {:name "a" :balance 0}))
;; end-sample

;; sample(agents)
;; agents are a type that have a queue holding
;; the actions that need to be performed on the agent's value
(def simple-agent (agent []))

;; we can enqueue an action for the agent by using send
;; or send-off
(send simple-agent conj "abcd")

@simple-agent
;=> ["abcd"]

;; send returns immediately with the value of the agent 
;; the update on the agent happens in a thread on thread pool
;; end-sample

;; sample(agent-send-off)
(send-off simple-agent
          (fn [old-val val]
            (Thread/sleep 10000)
            (conj old-val val))
          "defg")

;; send works with the fixed thread pool
;; if all your threads are busy your agent may freeze
;; send-off potentially spawns a new thread and executes the
;; function

;; this means that send-off may be a bit slower but you
;; should use it for long running/blocking tasks so as
;; not to block all threads of the fixed thread pool
;; end-sample


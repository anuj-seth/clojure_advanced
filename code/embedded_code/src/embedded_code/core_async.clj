(ns embedded-code.core-async
  (:require [clojure.core.async :as async]))

(require '[clojure.core.async :as async])

;; sample(channels)
;; a channel supports multiple readers
;; and writers
;; this is an unbuffered channel i.e. size 1
(async/chan)
;; buffered channels are created like this
;; let's look at this line by line
(let [c (async/chan 10)]
  (async/>!! c "hello from this side")
  (async/close! c)
  (async/<!! c))

;; note that even though the channel is closed
;; you can still take values from it until drained
;; at which point it returns nil
;; end-sample

;; sample(channels-2)
(let [c (async/chan 10)]
  (dotimes [x 10]
    (async/>!! c (str "hello from this side " x)))
  (async/close! c)
  (loop []
    (when-let [v (async/<!! c)]
      (println v)
      (recur))))

;; what would happen if I push 11 items onto
;; this channel ?
;; or push one item onto an unbuffered channel
;; with no reader waiting
;; end-sample

;; sample(thread)
;; thread is like future
;; it returns a channel which gets the value of body
(let [c (async/chan)
      return (async/thread (async/>!! c "can you hear me ?"))]
  (println (async/<!! c))
  (async/close! c)
  (async/<!! return))

(let [c (async/chan)
      return (async/thread (async/>!! c "can you hear me ?")
                           :i-am-done)]
  (println (async/<!! c))
  (async/close! c)
  (async/<!! return))
;; end-sample

;; sample(go)
;; go blocks execute asynchronously in
;; a seperate thread pool of size 8 by default.
;; one pool for all go blocks in your code
(let [c (async/chan)
      go-and-loop (async/go
                    (loop [acc []]
                      (if-let [v (async/<! c)]
                        (recur (conj acc (str ".." v "..")))
                        acc)))]
  (dotimes [x 10]
    (async/>!! c x))
  (async/close! c)
  (async/<!! go-and-loop))
;; end-sample

;; sample(park-vs-block)
;; why the different functions - >! vs >!! ?
;; <! vs <!! ?
;; <! parks, <!! blocks
;; >! parks, >!! blocks
;; end-sample

;; sample(go-2)
;; the (go (loop ..)) is such a common idiom
;; that there exists a go-loop macro already
;; can you convert the dotimes loop to use a
;; go-loop ?
(let [c (async/chan)
      go-and-loop (async/go
                    (loop [acc []]
                      (if-let [v (async/<! c)]
                        (recur (conj acc (str ".." v "..")))
                        acc)))]
  (dotimes [x 10]
    (async/>!! c x))
  (async/close! c)
  (async/<!! go-and-loop))
;; end-sample

;; sample(go-loop)
(let [c (async/chan)
      go-and-loop (async/go
                    (loop [acc []]
                      (if-let [v (async/<! c)]
                        (recur (conj acc (str ".." v "..")))
                        acc)))]
  (async/go-loop [x 0]
    (if (< x 10)
      (do 
        (async/>! c x)
        (recur (inc x)))
      (async/close! c)))
  (async/<!! go-and-loop))
;; notice how the close! is inside the go-loop
;; can it be outside the go-loop ?
;; end-sample

;; sample(go-vs-thread)
;; so when do you use thread and
;; when should you prefer go blocks ?

;; use thread when you need to do blocking work -
;; io, calls to webservers, db, etc.
;; and go blocks for compute intensive work
;; end-sample

;; sample(go-pipeline)
;; remember how we created a pipeline using promise
;; let's do something similar with channels
(defn stage
  [in out f]
  (async/go (loop []
        (when-some [v (async/<! in)]
          (async/>! out (f v))
          (recur)))
      (async/close! out)))

(let [in (async/chan)
      a (async/chan)
      b (async/chan)
      c (async/chan)
      out (async/chan)]
  (stage in a inc)
  (stage a b inc)
  (stage b c inc)
  (stage c out inc)
  (async/>!! in 1)
  (async/close! in)
  (async/<!! out))
;; end-sample

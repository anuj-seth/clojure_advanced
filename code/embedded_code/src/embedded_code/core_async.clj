(ns embedded-code.core-async
  (:require [clojure.core.async :refer [chan >!! <!! close! thread go <! >! go-loop]]))

(require '[clojure.core.async :refer [chan >!! <!! close! thread go <! >! go-loop]])

;; sample(channels)
;; a channel supports multiple readers
;; and writers
;; this is an unbuffered channel i.e. size 1
(chan)
;; buffered channels are created like this
;; let's look at this line by line
(let [c (chan 10)]
  (>!! c "hello from this side")
  (close! c)
  (<!! c))

;; note that even though the channel is closed
;; you can still take values from it until drained
;; at which point it returns nil
;; end-sample

;; sample(channels-2)
(let [c (chan 10)]
  (dotimes [x 10]
    (>!! c (str "hello from this side " x)))
  (close! c)
  (loop []
    (when-let [v (<!! c)]
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
(let [c (chan)
      return (thread (>!! c "can you hear me ?"))]
  (println (<!! c))
  (close! c)
  (<!! return))

(let [c (chan)
      return (thread (>!! c "can you hear me ?")
                     :i-am-done)]
  (println (<!! c))
  (close! c)
  (<!! return))
;; end-sample

;; sample(go)
;; go blocks execute asynchronously in
;; a seperate thread pool
(let [c (chan)
      go-and-loop (go
                    (loop [acc []]
                      (if-let [v (<! c)]
                        (recur (conj acc (str ".." v "..")))
                        acc)))]
  (dotimes [x 10]
    (>!! c x))
  (close! c)
  (<!! go-and-loop))

;; the (go (loop ..)) is such a common idiom
;; that there exists a go-loop macro already
;; can you convert the dotimes loop to use a
;; go-loop ?
;; end-sample

;; sample(go-loop)
(let [c (chan)
      go-and-loop (go
                    (loop [acc []]
                      (if-let [v (<! c)]
                        (recur (conj acc (str ".." v "..")))
                        acc)))]
  (go-loop [x 0]
    (if (< x 10)
      (do 
        (>! c x)
        (recur (inc x)))
      (close! c)))
  (<!! go-and-loop))
;; notice how the close! is inside the go-loop
;; can it be outside the go-loop ?
;; end-sample

;; sample(go-pipeline)
;; remember how we created a pipeline using promise
;; let's do something similar with channels
(defn stage
  [in out f]
  (go (loop []
        (when-some [v (<! in)]
          (>! out (f v))
          (recur)))
      (close! out)))

(let [in (chan)
      a (chan)
      b (chan)
      c (chan)
      out (chan)]
  (stage in a inc)
  (stage a b inc)
  (stage b c inc)
  (stage c out inc)
  (>!! in 1)
  (close! in)
  (<!! out))
;; end-sample

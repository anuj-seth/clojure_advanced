(require '[clojure.core.async :as async])

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

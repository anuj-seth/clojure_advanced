(ns the-tests.tower-of-hanoi-test
  (:require  [clojure.test :refer :all]))

(defn tower-of-hanoi
  "Let's see how many moves are required for stacks of different heights.
  A one disk stack is trivial - :A to :C (assuming :A is starting pin,
  :C is the destination and :B is the one in the middle).
  For a 2 disk stack - put the top disk out of the way by moving it to :B,
  then move the bottom disk to :C and finally move the smaller disk from :B to :C.
  A stack of 3 presents intersting challenges but let's see if we can restate our problem
  in a way that makes it easier to solve.
  Can we move the top 2 disks from :A to :B using :C only as an intermediate holding pin ?
  Yes we already know how to do that. 
  After moving the 2 top disks onto :B, move the bottom disk to :C.
  As a last step move the 2 disks from :B to :C using :A as intermediate storage.
  
  So it seems we have a method here:
  For n disks:
  Move n - 1 disks to pin :B.
  Move 1 disk to :C.
  Move n - 1 disks from :B to :C.
  In mathematics this goes by the fancy name of induction.

  Args - n is the number of disks in tower.
         from-pin is the starting pin on which all the n disks are placed initially.
         to-pin is the destination pin on which all the n disks should rest at the end.
         using-pin is the intermediate resting place for any pins while we are moving them from from-pin to to-pin.
  Return - a sequence of moves. Each move is a hash-map with two keys - :from and :to - which are the source and
           destination pin for a move.
  Note: The rules state that no smaller disk should ever be placed under a larger disk."
  [n from-pin to-pin using-pin]

  )

(deftest tower-of-hanoi-test
  (is (= [{:from :A, :to :C}]
         (tower-of-hanoi 1 :A :C :B)))
  (is (= [{:from :A, :to :B}
          {:from :A, :to :C}
          {:from :B, :to :C}]
         (tower-of-hanoi 2 :A :C :B)))
  (is (= [{:from :A, :to :C}
          {:from :A, :to :B}
          {:from :C, :to :B}
          {:from :A, :to :C}
          {:from :B, :to :A}
          {:from :B, :to :C}
          {:from :A, :to :C}]
         (tower-of-hanoi 3 :A :C :B))))

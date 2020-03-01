(ns the-tests.ref-test
  (:require  [clojure.test :refer :all]))

(deftest ref-test
  (let [books (ref {})
        shelves (ref {})]
    (is (=  {"The Shining" "Stephen King"}
            (do (dosync
                 (ref-set books {"The Shining" "Stephen King"})
                 (ref-set shelves {:s1 #{"The Shining"}}))
                @books)))
    (is (= [{"The Shining" "Stephen King",
             "Carrie" "Stephen King",
             "Good Omens" "Terry Pratchett",
             "Feet Of Clay" "Terry Pratchett"}
            {:s1 #{"The Shining" "Carrie"},
             :s2 #{"Feet Of Clay" "Good Omens"}}]
           (let [book-adder (fn [book-name author shelf]
                              (dosync
                               (alter books assoc book-name author)
                               (alter shelves
                                      update-in
                                      [shelf]
                                      (fnil conj #{})
                                      book-name)))]
             (doall
              (pcalls #(book-adder "Carrie" "Stephen King" :s1)
                      #(book-adder "Feet Of Clay" "Terry Pratchett" :s2)
                      #(book-adder "Good Omens" "Terry Pratchett" :s2)))
             [@books @shelves])))))

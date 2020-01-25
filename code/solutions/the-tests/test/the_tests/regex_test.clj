(ns the-tests.regex-test
  (:require  [clojure.test :refer :all]))

(deftest re-matches-test
  ;; \d matches digits while \D matches non-digits
  ;; ^ and $ match start and end of strings
  (let [p #"^\D*(\d+)\D*$"]
    (are [x y] (= x y)
      "123" (second (re-matches p
                                "abc123def"))
      nil   (re-matches p
                        "1abc123def")))

  ;; write a regex pattern to match
  ;; two words seperated by a space
  ;; i.e. a name like "John Malkovich"
  ;; \w matches a-zA-Z_0-9
  ;; \s matches spaces
  (is (= "john"
         (let [[_ fname lname] (re-matches #"(\w+)\s(\w+)"
                                           "john Malkovich")]
           fname))))

(deftest re-find-test
  ;; +91-XXXX-NNNNNN
  ;; mobile numbers in india have the above pattern
  ;; extract XXXX which is the mobile carrier
  ;; assume that all phone numbers have this length
  ;; some phone numbers may not have hyphens
  ;; also validate that it is a valid mobile number
  (let [p #"\+91-{0,1}(\d{4})-{0,1}\d{6}"]
    (are [x y] (= x y)
      "9765" (second (re-find  p
                               "+91-9765-123456"))
      nil    (re-find p
                      "+91-9765-12345")
      "9765" (second (re-find p
                              "+919765123456")))))

(deftest re-seq-test
  ;; can you find all the 4 letter words ?
  ;; re-seq also behaves like re-matches/re-find
  ;; when groups are used
  (is (= ["over" "lazy"]
         (map second 
              (re-seq #"\s(\w{4})\s"
                      "a quick brown fox jumps over the lazy dogs")))))

(deftest replace-test
  ;; given a hash map of replacements
  ;; match digits and replace them
  (let [replacements {"123" "one-two-three-"
                      "456" "-four-five-six"}]
    (is (= "one-two-three-abc-four-five-six"
           (clojure.string/replace "123abc456"
                                   #"\d+"
                                   replacements)))

    (is (= "one-two-three--four-five-six"
           (clojure.string/replace "abc123|defabc456|def"
                                   #"abc(\d+)\|[a-z]{3}"
                                   (fn [[full-match grp]]
                                     (replacements grp)))))))

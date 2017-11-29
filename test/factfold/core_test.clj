(ns factfold.core-test
  (:require [clojure.test :refer :all]
            [factfold.core :refer [evaluate]]))

; This file will need to be refactored as tests grow

(defn latest-value [k] #(or (get %2 k) (get % k)))

(def test-facts
  [{:first-name "Emmet" :last-name "Smith" :lives-on "Earth"}
   {:lives-on "Mars"}])

(def test-model
  [{:first-name (latest-value :first-name)
    :last-name (latest-value :last-name)
    :lives-on (latest-value :lives-on)
    :original-planet
      (fn [case fact]
        (or (case :original-planet) (fact :lives-on)))}

   {:full-name
      (fn [case fact]
        (str (case :first-name) " " (case :last-name)))}])

(def expected-values
  {:first-name "Emmet"
   :last-name "Smith"
   :full-name "Emmet Smith"
   :lives-on "Mars"
   :original-planet "Earth"})

(deftest model-application
  (testing "Basic model application works"
    (let [actual (reduce (partial evaluate test-model) {} test-facts)]
      (is (= actual expected-values)))))

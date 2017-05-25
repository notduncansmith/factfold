(ns factfold.core-test
  (:require [clojure.test :refer :all]
            [factfold.model :as model]))

; This file will need to be refactored as tests grow

(def test-facts
  [{:first-name "Emmet" :last-name "Smith" :lives-on "Earth"}
   {:lives-on "Mars"}])

(def test-model
  [{:first-name (model/latest-value :first-name)
    :last-name (model/latest-value :last-name)
    :lives-on (model/latest-value :lives-on)
    :original-planet
      (fn [case fact]
        (or (case :original-planet) (fact :lives-on)))}

   {:full-name
    (fn [case fact]
      (str (case :first-name) " " (case :last-name)))}])

(def expected-case-values
  {:first-name "Emmet"
   :last-name "Smith"
   :full-name "Emmet Smith"
   :lives-on "Mars"
   :original-planet "Earth"})

(deftest model-application
  (testing "Basic model application works"
    (let [actual (model/apply-model test-model {} test-facts)]
      (is (= actual expected-case-values)))))

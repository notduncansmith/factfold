(ns factfold.core-test
  (:require [clojure.test :refer :all]
            [factfold.core :refer [evaluate-all]]))

(defn latest-value [k] (fn [state fact] (or (get fact k) (get state k))))

(def test-facts
  [{:first-name "Emmet" :last-name "Smith" :lives-on "Earth"}
   {:lives-on "Mars"}])

(def test-model
  [{:first-name (latest-value :first-name)
    :last-name (latest-value :last-name)
    :lives-on (latest-value :lives-on)}

   {:original-planet (fn [s _] (or (s :original-planet) (s :lives-on)))
    :full-name (fn [s _] (str (s :first-name) " " (s :last-name)))}])

(def expected-values
  {:first-name "Emmet"
   :last-name "Smith"
   :full-name "Emmet Smith"
   :lives-on "Mars"
   :original-planet "Earth"})

(deftest model-evaluation
  (testing "Basic model evaluation works"
    (let [actual (evaluate-all test-model {} test-facts)]
      (is (= actual expected-values)))))
(ns factfold.model
  (:require [factfold.core :refer [evaluate]]))

(defn apply-model
  "Reduces a model, a starting state, and a discrete sequence of facts into an end model state."
  [model current-values facts]
  (reduce #(evaluate model % %2) current-values facts))

(defn latest-value
  "Generate a property function to return either the last received value for a key, or the updated value given by the current fact"
  ([k] (latest-value k nil))
  ([k default-value] #(or (%2 k) (% k) default-value)))

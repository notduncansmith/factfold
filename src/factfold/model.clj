(ns factfold.model
  (:require
    [factfold.util :refer [filter-kv]]))

(defn new-values-for-properties
  "Given a property map (a map of property keys to the functions that compute the values of those keys), a case state, and a new fact, compute any changed property values by reducing the list of facts with the property fn, starting from the current value of that property. Returns a sparse map of updated properties."
  [property-map current-case-values fact]
  (reduce-kv
    (fn [updated-property-values property-label property-fn]
      (let [result (property-fn updated-property-values fact)
            starting-value (current-case-values property-label)]
        (if (not= result starting-value)
          (assoc updated-property-values property-label result)
          updated-property-values)))
    current-case-values
    property-map))

(defn apply-model
  "Given a model (dependency-ordered array of property maps), a case, and a list of facts (oldest-first), compute the new `:values` for the case. Application of a model to a list of facts is a fold of single-fact application over the list. Fact application is a fold of recomputation of the model's property maps."
  [model current-case-values facts]
  (if (empty? facts)
    current-case-values
    (reduce
      (fn [case-values fact]
        (reduce
          (fn [m property-map]
            (merge m (new-values-for-properties property-map m fact)))
          case-values
          model))
      current-case-values
      facts)))

(defn latest-value
  "Generate a property function to return either the last received value for a key, or the updated value given by the current fact"
  [property-key]
  (fn [case-values fact]
    (or ; should find a cleaner first-non-nil method
      (fact property-key)
      (case-values property-key))))
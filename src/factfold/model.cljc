(ns factfold.model)

(defn evaluate-properties
  "Given a property map, their current values, and a new fact, compute their new values."
  [property-map current-values fact]
  (reduce-kv
    (fn [new-values property-label property-formula]
      (assoc new-values property-label
        (if (= (type property-formula) clojure.lang.PersistentVector)
          (evaluate-model property-formula new-values fact)
          (property-formula new-values fact))))
    current-values
    property-map))

(defn evaluate-model
  "Given a model, a state, and a new fact, return the model's next state."
  [model current-values fact]
  (reduce #(merge % (evaluate-properties %2 % fact)) current-values model))

(defn apply-model
  "Deprecated in favor the less-complected `evaluate-model`. Reduces a discrete sequence of facts into an end model state."
  [model current-values facts]
  (reduce #(evaluate-model model %2 %) current-values facts))

; helpers, not enough to need their own namespace yet

(defn latest-value
  "Generate a property function to return either the last received value for a key, or the updated value given by the current fact"
  ( [property-key] (latest-value property-key nil))
  ( [property-key default-value]
    (fn [case-values fact]
      (or ; should find a cleaner first-non-nil method
        (fact property-key)
        (case-values property-key)
        default-value))))

(defn constant [value] (fn [state fact] value))
(defn in-state [p] (get-in state p))

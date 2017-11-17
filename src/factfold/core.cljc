(ns factfold.core)

(defn evaluate
  "Given a model, a state, and a new fact, return the model's next state"
  [model current-values fact]
  (reduce
    (fn [order-values property-map]
      (reduce-kv #(assoc % %2 (if (= (type %3) clojure.lang.PersistentVector)
                                (evaluate %3 (%2 current-values) fact)
                                (%3 current-values fact)))
        order-values property-map))
    current-values model))

(defn advance!
  "Update the model state in a given atom with a given model and fact"
  [state-atom model fact]
  (swap! state-atom #(evaluate model % fact)))

(defn evolve!
  "Update the state in an atom with a model extracted from the state with `get-model` and a given fact. Aka poor-man's π-calculus."
  [state-atom get-model fact]
  (swap! state-atom #(evaluate (get-model %) % fact)))

(ns factfold.core)

(defn evaluate
  "Given a model, a state, and a new fact, return the model's next state"
  [model state fact]
  (reduce
    (fn [order-values property-map]
      (reduce-kv #(assoc % %2 (if (= (type %3) clojure.lang.PersistentVector)
                                (evaluate %3 (%2 state) fact)
                                (%3 state fact)))
        order-values property-map))
    state model))

(defn advance!
  "Update the model state in a given atom with a given model and fact"
  [state-atom model fact]
  (swap! state-atom #(evaluate model % fact)))

(defn evolve!
  "Update the state in an atom with a model extracted from the state with `get-model` and a given fact (aka poor-man's π-calculus)"
  [state-atom get-model fact]
  (swap! state-atom #(evaluate (get-model %) % fact)))

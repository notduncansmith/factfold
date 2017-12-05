(ns factfold.core)

(defn evaluate
  "Given a model, a state, and a new fact, return the model's next state"
  [model state fact]
  (reduce
    (fn [new-state property-map]
      (reduce-kv
        (fn [order-values property-name property-formula]
          (assoc order-values property-name
            (if (= (type property-formula)  #?(:clj clojure.lang.PersistentVector
                                               :cljs cljs.core/PersistentVector))
              (evaluate property-formula (get state property-name) fact)
              (property-formula state fact))))
        new-state property-map))
    state model))

(defn evaluate-all
  "Evaluate a sequence of facts in the same context"
  [model state facts]
  (reduce (partial evaluate model) state facts))

(defn advance!
  "Update the model state in a given atom with a given model and fact"
  [state-atom model fact]
  (swap! state-atom (partial evaluate model) fact))

; commented until tested
; (defn advance-async!
;   "Update the state in an agent (as opposed to an atom) with a given model and fact"
;   [state-agent model fact]
;   (send state-agent (partial evaluate model) fact))

(defn evolve!
  "Update the state in an atom with a model extracted from the state with `get-model` and a given fact (aka poor-man's π-calculus)"
  [state-atom get-model fact]
  (swap! state-atom #(evaluate (get-model %) % fact)))

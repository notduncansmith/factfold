(ns factfold.core
  (:require [factfold.model :as model]))

(defn advance!
  "Update the model state in a given atom with a given model and fact"
  [state-atom model fact]
  (swap! state-atom model/evaluate-model model fact))

(defn evolve!
  "Update the state in an atom with a model extracted from the state with `next-model` and a given fact"
  [state-atom next-model fact]
  (swap! state-atom #(model/evaluate-model (next-model %) % fact)))

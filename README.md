# Factfold

[![Clojars Project](https://img.shields.io/clojars/v/factfold.svg)](https://clojars.org/factfold)

> The utility of a language as a tool of thought increases with the range of topics it can treat, but decreases with the amount of vocabulary and the complexity of grammatical rules which the user must keep in mind. Economy of notation is therefore important.
>
> Kenneth E. Iverson, 1979

Factfold is a Clojure library that makes it easier to write organized, predictable, composable, parallelizable code, *without a ton of boilerplate*. It is a reference implementation of what aims to one day be a language-portable API. It offers a way to build programs as tiers of derived data structures represented by the functions that construct them from streams of input. While it bears a strong resemblance to JavaScript's Redux library, Factfold attempts to present a more data-oriented API. It's also written in Clojure, which is often more suitable than JS for processing medium-large volumes of data.

## How it works

Factfold computes **state** by applying a **model** to **facts** in chronological order, retaining state between facts. Models associate **property** names with functions to compute their values. Grouping model properties into **orders** makes their logical dependencies clear to human readers, and provides natural concurrency boundaries. Each property's value is computed from the current state and the newest fact.

A model is represented as a vector of maps, which associate property names with functions. A state is represented as a map of all property names to their function's most recent return value. A fact is represented as a map with no predefined structure.

This example model has a single first-order property, `:subject`. This property has a constant value of `"world"`. The model also has a second-order property, `:greeting`, which depends on `:subject`.

```clj
(require '[factfold.core :refer [evaluate]])

(def model
  [{:subject (fn [state fact] "world")}
   {:greeting (fn [state fact] (str "Hello " (or (:subject fact) (:subject state)) "!"))}])

(evaluate model {} nil) ; {:subject "world" :greeting "Hello world!"}
(evaluate model {} {:subject "Github"}) ; {:subject "world" :greeting "Hello Github!"}
```

This pairs well with [`[factual/durable-queue "0.1.5"]`](https://github.com/Factual/durable-queue) and Clojure's [agents](https://clojure.org/reference/agents). For ideas about state persistence, consider the [hitchhiker tree](https://github.com/datacrypt-project/hitchhiker-tree), [konserve](https://github.com/replikativ/konserve), and [durable-ref](https://github.com/riverford/durable-ref) projects.

More examples to come.

## License

Licensed under the Eclipse Public License, same as Clojure.

Copyright Duncan Smith 2017

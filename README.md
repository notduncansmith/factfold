# Factfold - Orchestral programming

[![Clojars Project](https://img.shields.io/clojars/v/factfold.svg)](https://clojars.org/factfold)

Factfold is a library that makes it easier to write maintainable software by providing a flexible interface to data structure composition.

It's also an interpreter, in-memory database, complex event processor, rules/inference engine, computer algebra system, or monadic application state framework, depending on your persuasion. This is but one take on many old ideas.

## Rationale

Clojure is an excellent tool for developing software components and systems by hand. By adding a thin layer of homogeneity and structure to these components, we gain the ability to develop systems by machine. Where macros enable metaprogramming, Factfold enables metacomputing.

If we can programmatically assemble not just programs but systems of programs, we can navigate the full component granularity spectrum from within a single programming environment.

## How does it work

For the Clojure-savvy, see the [adorably tiny implementation](src/factfold/core.cljc).

Factfold processes data by applying **models** (logically-ordered vectors of property label/formula maps) to **facts** (discrete input data). Models can be nested arbitrarily.

## Examples

Here's a simple model which encodes the [Mandelbrot set](https://en.wikipedia.org/wiki/Mandelbrot_set) point value formula:

```clj
(require '[factfold.core :refer [evaluate]])

(defn brot
  [ctx]
  (evaluate
    [ {:c (fn [state _] (state :c)) ; first property, :c, is given and remains the same
       :n (fn [state _] (inc (or (state :n) 0)))} ; second property, :n, increases each run
      {:z (fn [{:keys [c z]} _] (+ (* z z) c))}] ; third property, :z, is second-order and depends upon c
    ctx
    nil))

(brot {:z 0 :c 0.23}) ; {:z 0.23, :c 0.23 :n 1}
(brot (brot {:z 0 :c 0.23 :n 1})) ; {:z 0.28290000000000004, :c 0.23 :n 2}

(defn mandelbrot-value
  "Return the number of iterations a value takes to escape"
  [c escape]
  (loop [ctx (brot {:z 0 :c c :n 0})]
    (if (escape ctx) (state :n) (recur (brot ctx)))))

(mandelbrot-value 0.23 #(or (> (state :z) 2) (> (state :n) 10000)))
```

For a more interactive example, consider a web application which tracks hits to unique paths. The state is modeled here:

```clj
(require '[factfold.core :refer [advance!]])

(def model
  [{:path-counts
    (fn [state req]
      (update (state :path-counts) (req :path) #(inc (or % 0))))}])

(def app-state (atom {:path-counts {}}))
(defn process-request! [req] (advance! app-state model req))

user=> (process-request! {:path "/foo"})
{:counts {"/foo" 0}}
user=> (process-request! {:path "/foo"})
{:counts {"/foo" 1}}
```

## Related / Prior Art

- [Petri nets](https://en.wikipedia.org/wiki/Petri_net)

- [Futamura_projections](https://en.wikipedia.org/wiki/Partial_evaluation#Futamura_projections)

- [Tree transducers](https://en.wikipedia.org/wiki/Tree_transducer)

- [Sequent calculus](https://en.wikipedia.org/wiki/Sequent_calculus)

- [Computer algebra systems](https://en.wikipedia.org/wiki/List_of_computer_algebra_systems)

- [Functional reactive programming](https://en.wikipedia.org/wiki/Functional_reactive_programming)

- [Rete algorithm](https://en.wikipedia.org/wiki/Rete_algorithm)

- [Plant (control theory)](https://en.wikipedia.org/wiki/Plant_(control_theory))

- [Truth maintenance systems](https://en.wikipedia.org/wiki/Reason_maintenance)


## License

Licensed under the Eclipse Public License, same as Clojure.

Copyright Duncan Smith 2017
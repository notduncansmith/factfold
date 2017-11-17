# Factfold - A dead-simple complex computation engine

Factfold is an interpreter-interpreter that simplifies the incremental construction of higher-order software models by representing them as Clojure data structures and functions.

It's also an in-memory database, a [complex event processing](https://en.wikipedia.org/wiki/Complex_event_processing) engine, or a "[forward-chaining](https://en.wikipedia.org/wiki/Forward_chaining)" [rules](https://en.wikipedia.org/wiki/Business_rules_engine)/[inference engine](https://en.wikipedia.org/wiki/Inference_engine) depending on your persuasion.

It is suitable for describing logical relationships between events/objects/facts/records. It is also suitable for describing the one-way graph of data structures in a well-designed program, and the concurrent maintenance of independent state trees in multi-user environments.

## Why does this exist

Most software development comes down to choosing the right data structures. Configuring and connecting these data structures is a relatively high-level type of programming, but we still do it with lower-level tools.

This library abstracts away the lower-level concerns of connecting data structures together, and offers a layer of declarative composability on top of Clojure code.

By representing software processes as data, we jump from metaprogramming to metacomputing.

## Examples

Here's a simple model which encodes the [Mandelbrot set](https://en.wikipedia.org/wiki/Mandelbrot_set) point value formula:

```clj
(require '[factfold.model :as model])

(defn brot
  [ctx]
  (model/evaluate-model
    [ {:c (fn [state _] (state :c))}
      {:z (fn [{:keys [c z]} _] (+ (* z z) c))}]
    ctx
    nil))

user=> (brot {:z 0 :c 0.23})
{:z 0.23, :c 0.23}

user=> (brot (brot {:z 0 :c 0.23}))
{:z 0.28290000000000004, :c 0.23}
```

In this case, the model has 2 orders: the first order properties, of which there is one, `:c`; and the second order properties, of which there is also one, `:z`. It also iterates purely based on its initial state, no input is required.

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

## License

Licensed under the Eclipse Public License, same as Clojure.

Copyright Duncan Smith 2017
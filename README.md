# Factfold

Factfold is a Clojure library that provides facilities for describing and executing dependent computation graphs.

Specifically, it:

1. Turns a dataflow DAG into a fold function
2. Executes this fold function continuously an input stream
3. Continuously groups and filters these fold results

## Why does this exist

Computers are fast enough that in most cases, simply choosing close-enough-to-optimal data structures will result in close-enough-to-optimal performance. **All** data structures (i.e. all programs) [can be specified](http://www.cs.nott.ac.uk/~pszgmh/fold.pdf) in terms of a fold function.

Factfold makes it both simpler and easier to specify this fold function and be confident in its correctness, by breaking it down into labeled, as-ordered-as-necessary steps. Think of it like Excel, but more structured. These fold function specifications are called "models".

This allows for rapidly developing interactive applications which can run efficiently on a huge number of targets out of the box (right now JVM and browsers with Clojure, but this implementation would be trivial to reproduce in other environments).

Here's an example which iterates a given point through the [Mandelbrot set](https://en.wikipedia.org/wiki/Mandelbrot_set) function:

```clj
(defn brot
  [ctx]
  (factfold.model/apply-model
    [ {:c (fn [state input] (state :c))}
      {:z (fn [{:keys [c z]} _] (+ (* z z) c))}]
    ctx
    [{}]))

user=> (brot {:z 0 :c 0.23})
{:z 0.23, :c 0.23}

user=> (brot (brot {:z 0 :c 0.23}))
{:z 0.28290000000000004, :c 0.23}
```

In this case, the model has 2 orders: the first order properties, of which there is one, `:c`; and the second order properties, of which there is also one, `:z`. It also iterates purely based on its initial state, no input is required.

Here's a more interactive example, a web application which tracks hits to unique paths:

```clj
(def app-state (atom {}))

(def model
  [{:counts (fn [state request]
              (update (state :counts) (request :path) #(if % (inc %) 0)))}])

(defn process-request!
  [request]
  (swap! app-state #(factfold.model/apply-model model % [request])))

user=> (process-request! {:path "/foo"})
{:counts {"/foo" 0}}
user=> (process-request! {:path "/foo"})
{:counts {"/foo" 1}}
```

## Notes

This is a little less magical than Plumatic's excellent [plumbing](https://github.com/plumatic/plumbing) library. I think that `fnk` is a great usability affordance for many use cases, but for my own I wanted something with a simpler design, and decoupled from dependency resolution. While this library can be used for data modeling on its own, my end goal is something with a UI that will feed into an engine using this library. Feel free to email me more about this if you're interested: github@dms.sh

Copyright Duncan Smith 2017
# Factfold

Factfold is a Clojure library that provides facilities for describing and executing dependent computation graphs.

The most succinct explanation is that it is a realization of the [third Futamura projection](http://blog.sigfpe.com/2009/05/three-projections-of-doctor-futamura.html).

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

In this case, the model has 2 orders: the first order properties, of which there is one, `:c`; and the second order properties, of which there is also one, `:z`. It also iterates purely based on its initial state, no input is required (as opposed to web servers, mobile apps, games, etc).

Here's a more interactive example, a web application which tracks hits to unique paths:

```clj
(def app-state (atom {}))

(def model
  [{:counts (fn [{:keys [counts]} {:keys [path]}]
              (update counts path #(if % (inc %) 0)))}])

(defn process-request!
  [request]
  (swap! app-state #(factfold.model/apply-model model % [request])))

user=> (process-request! {:path "/foo"})
{:counts {"/foo" 0}}
user=> (process-request! {:path "/foo"})
{:counts {"/foo" 1}}
```

Hopefully it's easy to imagine how more complex applications might evolve.

Copyright Duncan Smith 2017
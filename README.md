# Factfold - Design as data

[![Clojars Project](https://img.shields.io/clojars/v/factfold.svg)](https://clojars.org/factfold)

> The utility of a language as a tool of thought increases with the range of topics it can treat, but decreases with the amount of vocabulary and the complexity of grammatical rules which the user must keep in mind. Economy of notation is therefore important.
>
> Kenneth E. Iverson, 1979

Factfold is a Clojure library that makes it easier to write organized, predictable, parallelizable code. It is a reference implementation of a portable API that encourages data-oriented program design by composing behaviors with structured [meta-circular evaluation](https://en.wikipedia.org/wiki/Meta-circular_evaluator). This API is essentially the computational Curry-Howard interpretation of the [(sychronous) calculus of resources and processes](https://en.wikipedia.org/wiki/Bunched_logic#Resources_and_Processes).

## A brief note on data and behavior

The language of program behavior is at the lowest level defined, and consequently constrained, to and by each individual programming environment's syntax, grammar, and runtime implementation. Our language for discussing *types of relationships between behaviors* is that of "design patterns" and "best practices", both of which require years of dedicated practice to interpret correctly across programming environments and in complex projects.

For discussing *types of relationships between data* in a program, we use the language of "data structures". Borrowed from and comingled with mathematics, this language is shared among all programmers and by implicitly or explicitly underlying any discussion of "software architecture" or "program design", transcends it altogether.

If a data structure is well-understood, a faithful implementation will offer few surprises, and its performance can be reasoned about fairly strongly before a line of code is written.

By restricting the design of software to the description of data structures, and its development to their faithful implementation, we eliminate much unnecessary indirection, ambiguity, and complexity from both.

## Whither Factfold?

If the design of a program is a labeled, dependency-ordered list of data structures, then Factfold allows you to directly embed your program design as a data structure.

Factfold processes data by applying **models** to **facts**† in chronological order. Models associate **property** names with functions to compute their values. Grouping model properties into **orders** makes their logical dependencies clear to human readers, and provides an obvious concurrency barrier. Each property's value is computed from a snapshot of the current state and a new datum.

Thanks to Clojure's efficient immutable data structures, we can maintain as many copies of the state as there are properties to compute while only storing the changes they make.

† Feel free to substitute "applying models to facts" with "calculating views of documents", "dispatching messages to actors/objects/processes", "executing instructions on a thread", "handling events", "updating relations with records", etc.

## Enough talk! Show me examples!

This model's single first-order property, `:subject`, has a constant value of `"world"`. The model also has a second-order property, `:greeting`, which depends on `:subject`.

```clj
(require '[factfold.core :refer [evaluate]])

(def model
  [{:subject (fn [state fact] "world")}
   {:greeting (fn [s f] (str "hello " (or (f :subject) (s :subject)) "!"))}])

(evaluate model {}) ; {:subject "world" :greeting "hello world!"}
(evaluate model {:subject "Github"}) ; {:subject "world" :greeting "hello Github!"}
```

This model encodes the [Mandelbrot set](https://en.wikipedia.org/wiki/Mandelbrot_set) function:

```clj
(defn brot
  [ctx]
  (evaluate
    [ {:c (fn [state _] (state :c)) ; first property, :c, is given and remains the same
       :n (fn [state _] (inc (or (state :n) 0)))} ; second property, :n, increases each run
      {:z (fn [{:keys [c z]} _] (+ (* z z) c))}] ; third property, :z, is second-order and depends upon c
    ctx
    nil)) ; ε if you're fancy

(brot {:z 0 :c 0.23}) ; {:z 0.23, :c 0.23 :n 1}
(brot (brot {:z 0 :c 0.23 :n 1})) ; {:z 0.28290000000000004, :c 0.23 :n 2}

(defn mandelbrot-value
  "Return the number of iterations a value takes to escape"
  [c escape]
  (loop [ctx (brot {:z 0 :c c :n 0})]
    (if (escape ctx) (ctx :n) (recur (brot ctx)))))

(mandelbrot-value 0.23 #(or (> (% :z) 2) (> (% :n) 10000))) ; 10001
(mandelbrot-value 0.26 #(or (> (% :z) 2) (> (% :n) 10000))) ; 30
```

Consider a web application which tracks hits to unique paths. The state is managed here:

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

This pairs well with [`[factual/durable-queue "0.1.5"]`](https://github.com/Factual/durable-queue) and Clojure's [agents](https://clojure.org/reference/agents). For ideas about persistence for these atoms or agents, consider the [hitchiker tree](https://github.com/datacrypt-project/hitchhiker-tree) library.

More examples to come.

## License

Licensed under the Eclipse Public License, same as Clojure.

Copyright Duncan Smith 2017
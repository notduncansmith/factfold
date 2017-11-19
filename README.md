# Factfold

[![Clojars Project](https://img.shields.io/clojars/v/factfold.svg)](https://clojars.org/factfold)

> The utility of a language as a tool of thought increases with the range of topics it can treat, but decreases with the amount of vocabulary and the complexity of grammatical rules which the user must keep in mind. Economy of notation is therefore important.
> Kenneth E. Iverson, 1979

Factfold is a code orchestration library that makes it easier to write maintainable software.

It's also an embedded database, complex event processor, rules/inference engine, computer algebra system, monadic application state container, computational modeling layer, virtual universal Turing machine, or simply "interpreter", depending on your persuasion.

## Rationale

All programs are interpreters, and thus all programming is some level of metaprogramming. This hints at an extreme degree of [redundancy](https://en.wikipedia.org/wiki/Greenspun's_tenth_rule) in the practice of software development, turning tweet-sized concepts into pages of code.

This project is an attempt to alleviate some of this redundancy. It is a **hosted interpreter** which, rather than taking strings as input, evaluates host data structures which describe in a flexibly executable way the structure of complex programs in the host language. In effect, it is a portable and disposable metaprogramming environment that discourages logic errors by design.

Lisps demonstrate the power of treating code and its evaluation as data, born of the need to elegantly express complicated things to a machine. Most programmers will never use a Lisp, but that oughtn't be an excuse for producing bloated code. Inspired by the tools that made humbling levels of complexity managable, Factfold aims to make anything less complex absolutely trivial. The hope is that this [rather small reference implementation](src/factfold/core.cljc) will inspire ports (like [this](https://github.com/notduncansmith/factjs) pure-Javascript one), and that we may see some standardization of **higher-order software models**.

Complex programs are those with multiple data structures who derive their values from each other, aka concurrent [random access machines](https://en.wikipedia.org/wiki/Random-access_machine). In complex programs today, the instructions for these heterogeneous machines are blended together into a mess of indirect access, leading to unnecessarily long seek times at best and tedious-to-debug errors on average.

Factfold encourages you to separate and name these concurrent subprocesses (with clear relationships and no shared responsibilities), leading to less "essay" code and more "well-groomed Wikipedia entry" code - navigably structured, recursively skimmable, appropriately hyperlinked, and thus instantly accessible to anyone who can read.

## How does it work

Factfold processes data by applying **models** to **facts**† in chronological order. Models associate **property** names with functions to compute their values. Grouping model properties into **orders** makes their logical dependencies clear to human readers, and provides a natural concurrency barrier. Each property's value is computed from a snapshot of the current state and a new datum.

Thanks to Clojure's efficient immutable data structures, we can maintain as many copies of the state as there are properties to compute while only storing the changes they make.

† Feel free to substitute "applying models to facts" with "calculating views of documents", "dispatching messages to actors/objects/processes", "executing instructions on a thread", "handling events", "updating relations with records", etc.

## Enough talk! Show me examples!

This model's single first-order property, `:subject`, has a constant value of `"world"`. The model also has a second-order property, `:greeting`, which depends on `:subject`.

```clj
(def model
  [{:subject (fn [state fact] "world")}
   {:greeting (fn [s f] (str "hello " (or (f :subject) (s :subject)) "!"))}])
```

This model encodes the [Mandelbrot set](https://en.wikipedia.org/wiki/Mandelbrot_set) function:

```clj
(require '[factfold.core :refer [evaluate]])

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

More complex examples to come.

## License

Licensed under the Eclipse Public License, same as Clojure.

Copyright Duncan Smith 2017
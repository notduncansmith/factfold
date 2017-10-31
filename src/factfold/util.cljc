(ns factfold.util)

(defn map-kv
  "Apply f to each kv pair in m"
  [f m]
  (reduce-kv #(assoc % %2 (f %2 %3)) {} m))

(defn filter-kv
  "Remove kv pairs of m that do not pass predicate f"
  [f m]
  (reduce-kv #(if (f %2 %3) (assoc % %2 %3) %) {} m))

(defn greater-of
  "Return the greater of two comparables"
  [a b]
  (if (> a b) a b))
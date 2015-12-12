(ns instantetymonline.autocomplete)

(def dict (read-string (slurp "resources/dict.dat")))

(def sorted-keys (into [] (sort (keys dict))))

(defn min' [x y] (if (nil? x) y (min x y)))

(defn subs? [s r]
  (= s (apply str (take (count s) r))))

(defn find-first-substring
  "Binary searches over ss until it finds the first substring."
  [s ss]
  (loop [lo 0
         hi (dec (count ss))
         res nil]
    (if (< hi lo) res
        (let [mid (quot (+ lo hi) 2)
              cmp (compare s (nth ss mid))
              sub (subs? s (nth ss mid))
              newres (if sub (min' res mid) res)]
          (cond
            (neg? cmp) (recur lo (dec mid) newres)
            (pos? cmp) (recur (inc mid) hi newres)
            :else newres)))))

(defn get-substrings [s ss n]
  (if-let [i (find-first-substring s ss)]
    (filter
     #(subs? s %)
     (map #(nth ss (+ i %)) (range n)))))

(defn autocomplete [word]
  (for [k (get-substrings word sorted-keys 10)]
    [k (get dict k)]))

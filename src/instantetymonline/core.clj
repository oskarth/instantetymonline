(ns instantetymonline.core
  (:require [net.cgrand.enlive-html :as html]))

;; There are ~1000 pages in total.

;; Basic algorithm:
;; For each letter
;; Go through all the pages
;; Download the three (two?) things and put them into some structure
;; Persist this

;; Given words and descriptions, how do we want to represent things?
;; We want O(1) access to words, so that's probably map with a key for now.
;; {word1 description1, ...}
;; Problem with this is that it's not straightforward how we do partial
;; matching. Does that not require a trie or something? We could have both,
;; though. Curious domain: how does such partial searches usually work?

;; Want something like this but way more fuzzy
;; (get etyms "elitism (n.)")
;; For now, let's just split by space (misses some like "Ellis Island")

;; Persistence



(defn gen-url [letter page]
  (str "http://etymonline.com/index.php?l=" letter "&p=" page))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

;; TODO: Add back verb/noun info.
;; TODO: Keep "Ellis Island" (right now it's turned into "Ellis").
(defn etym-words [hnodes]
  (let [raw (map html/text (html/select hnodes [:dt :a]))
        text (remove empty? raw)]
    (map #(first (clojure.string/split % #" "))
         text)))

;; TODO: Use links with cross-references from here.
(defn etym-links [hnodes]
  (map html/text (html/select hnodes [:dd.highlight :a])))

(defn etym-descriptions [hnodes]
  (map html/text (html/select hnodes [:dd])))

(defn etyms [hnodes]
  (zipmap (etym-words hnodes)
          (etym-descriptions hnodes)))

;; NOTE: Temporary - fetch hnodes once though
#_(def e9 (fetch-url "e" "9")) ;; e9 is a seq of hnodes

(def e9-map (etyms e9))

;; Persistence
;; (spit "data/e9" e9-map)
;; (def e9a (read-string (slurp "data/e9")))

;; To look up a word:
;; (get e9a "else") ;; => description string
;; (get e9a "nosuchword") ;; => nil

(ns instantetymonline.core
  (:require [net.cgrand.enlive-html :as html]))

(def ^:dynamic *base-url* "http://etymonline.com/index.php?l=e&p=9")

;; this is letter e and page 10. There are ~1000 pages in total.
;;(def http://etymonline.com/index.php?l=e&p=9

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

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn etym-words []
  (map html/text (html/select (fetch-url *base-url*) [:dt :a])))

(defn etym-links []
  (map html/text (html/select (fetch-url *base-url*) [:dd.highlight :a])))

(defn etym-descriptions []
  (map html/text (html/select (fetch-url *base-url*) [:dd])))

#_(def words (remove empty? (etym-words)))
;; Elihu,  -, Elijah, "", eliminate [these are all the titles]

#_(def links (etym-links))
;; trickiest to match up, but can do with "see X" and cross ref, I think.
;; don't think about this one for now
;; ex- limit Elijah [all the linked words in description, I think]

#_(def descriptions (etym-descriptions))
;; masc. proper name, name of grat Old TE.. [all the descriptions]

(def etyms (zipmap words descriptions))

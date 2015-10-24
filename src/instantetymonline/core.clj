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

;; Scraping

(def letter-page-map
  {"a" 60
   "b" 53
   "c" 84
   "d" 46
   "e" 38
   "f" 42
   "g" 35
   "h" 41
   "i" 38
   "j" 10
   "k" 9
   "l" 31
   "m" 56
   "n" 20
   "o" 22
   "p" 81
   "q" 6
   "r" 46
   "s" 108
   "t" 46
   "u" 24
   "v" 15
   "w" 22
   "x" 1
   "y" 4
   "z" 3})

(defn save-html! [letter page]
  (let [content (fetch-url (gen-url letter page))]
    (do (spit (str "raw_html/" letter page) (pr-str content))
        (println "saved" letter page))))

(defn read-html [letter page]
  (read-string (slurp (str "raw_html/" letter page))))

(defn persist-letter! [letter]
  (dotimes [n (get letter-page-map letter)]
    (save-html! letter (str n))))

(defonce words (atom {}))

(defn html->etyms []
  (doseq [char (keys letter-page-map)] ;; the alphabet
    (dotimes [n (get letter-page-map char)]
      (do (swap! words #(merge % (etyms (read-html char (str n)))))
          (println "read into atom" char (str n))))))

(comment
  ;; Ran this for all letters on October 24, 2015
  (persist-letter! "a")
  
  ;; To read it into mem, then save and merge with dict map
  (etyms (read-html "y" "1"))

  (html->etyms)

  (spit "src/dict2.cljs" @words)

  
  ;; This is cheap so we can do this multiple times I think
  ;; Also we can just read all files from raw_html directory


;;  (slurp (first (.listFiles (clojure.java.io/file "raw_html"))))
  )

(ns instantetymonline.core
  (:require [net.cgrand.enlive-html :as html]
            [org.httpkit.server :refer [run-server]]
            [clojure.core.async :as async :refer [<! go close!]]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route]
            [ring.util.response :refer [resource-response]]
            [ring.middleware.keyword-params]
            [ring.middleware.params]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit
             :refer [sente-web-server-adapter]]))

(defn gen-url [letter page]
  (str "http://etymonline.com/index.php?l=" letter "&p=" page))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn etym-words [hnodes]
  (let [raw (map html/text (html/select hnodes [:dt :a]))
        text (remove empty? raw)]
    text))

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

;; Websocket stuff
(let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn
              connected-uids]}
      (sente/make-channel-socket! sente-web-server-adapter {})]
  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv)
  (def chsk-send!                    send-fn)
  (def connected-uids                connected-uids))

;; Routes and handlers
(defroutes app-routes
  (GET "/" req (resource-response "index.html" {:root "public"}))
  (GET  "/chsk" req (ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req (ring-ajax-post                req))
  (route/resources "/"))

(def app (-> app-routes
             ring.middleware.keyword-params/wrap-keyword-params
             ring.middleware.params/wrap-params))

;; Server
(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn start! []
  (reset! server (run-server #'app {:port 8080})))

;; XXX: Still can't get the shutdown hook thing to work.
;; Componentize it?

(defn handle-events []
  (go (while true
        (let [val (<! ch-chsk)]
          (get-and-send! (:event val))))))

;; we get a message, then we want to broadcast it to client. How?
;; How broadcast in general?
(defn get-and-send! [[_ text]]
  (do
    (println "RECV2" text)
    (doseq [uid (:any @connected-uids)]
      (chsk-send! uid
                  [:output/text {:autocomplete "hi there"
                                 :uid uid}]))))

;; nil-uid
;;(:any @connected-uids)

;; for each uid, how know what it is? in event?
;; (chsk-send! foo )
;; ok so it updated? goodie, well only one of em

;; when we get this, then what?
;; [:input/text hello there]

;; now I can't close it! Problem.

(comment
  (start!)

  (def ch (atom nil))
  (reset! ch (handle-events))

  (close! @ch)
  )


;;; (handle-events)

;; (handle-events)

;; XXX: Trying to close go event handler.
; (close! @ch)
;; https://stackoverflow.com/questions/20485188/gracefully-exit-a-clojure-core-async-go-loop-on-kill


(comment
  ;; Ran this for all letters on October 24, 2015
  ;;(persist-letter! "a")

  ;; To read it into mem, then save and merge with dict map
  (etyms (read-html "y" "1"))

  (html->etyms)
  (first @words)

  (spit "src/dict2.cljs" @words)
  )

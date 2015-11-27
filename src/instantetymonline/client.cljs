(ns ^:figwheel-always instantetymonline.client
    (:require [reagent.core :as r]
              [instantetymonline.autocomplete :refer [autocomplete]]
              [taoensso.sente  :as sente :refer [cb-success?]]
              [cljs.core.async :as async :refer [<! >! put!]])
    (:require-macros [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

;; XXX: Warning: Every element in a seq should have a unique :key

;; Websocket sente stuf
(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk" {:type :auto})]
  (def chsk       chsk)
  (def ch-chsk    ch-recv)
  (def chsk-send! send-fn)
  (def chsk-state state))

(defn etym-lookup []
  (let [local (r/atom {:word ""})]   ;; not included in render
    (fn []                           ;; render from here
      (let [word (get @local :word)]
        [:div
         [:form
          [:input.h2
           {:id "etym"
            :type "text"
            :value word
            :on-change
            (fn [e]
              (let [word (-> e .-target .-value)]
                (chsk-send! [:input/text word])
                (reset! local {:word word})))}]]
         [:br]
         (for [[w def] (autocomplete word)]
           [:p {:style {"max-width" "35em"}} [:b w] " " def])]))))


(defn main-component []
  [:div.p1
   [:h1 "Etym Deli Test"]
   [:p "Made as part of an "
    [:a {:href "http://experiments.oskarth.com/etym/"} "experiment"]
    ". All credit of the etymologies belongs to Douglas Harper and his "
    [:a {:href "http://etymonline.com/"} "Online Etymology Dictionary" ] "."]
   [etym-lookup]])

(r/render-component [main-component]
  (. js/document (getElementById "app")))

;; connect to repl here
;; can't put liek that.
 ;; (put! chsk "foo")


;; With 3449 I get constant
;; WARN [taoensso.sente] - Chsk is closed: will try reconnect (10).
;; sente.cljs:811 WebSocket connection to 'ws://localhost:3449/chsk?client-id=730c1bc4-3edb-4551-b877-679e829eedcb' failed: Error during WebSocket handshake: Unexpected response code: 404

;; Chsk send against closed chsk, at 8080
;; it is indeed connected to 8080/chsk
;; apparent success?

(chsk-send! [:some/text "foo"])
(println "hi")

(defn update-autocomplete! [event]
  (println "INCOMING EVENT: " event))

(defn handle-events []
  (go (while true
        (let [val (<! ch-chsk)]
          (update-autocomplete! (:event val))))))

;; When run this?
;; (handle-events)

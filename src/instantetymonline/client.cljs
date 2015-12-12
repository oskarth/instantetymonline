(ns ^:figwheel-always instantetymonline.client
    (:require [reagent.core :as r]
;;              [instantetymonline.autocomplete :refer [autocomplete]]
              [taoensso.sente  :as sente :refer [cb-success?]]
              [cljs.core.async :as async :refer [<! >! put!]])
    (:require-macros [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

;; XXX: Warning: Every element in a seq should have a unique :key

(def autocompleted (atom nil))

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
      (let [word (get @local :word)
            autocompleted @autocompleted]
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
         ;; Want this thing here
         (for [[w def] autocompleted]
           [:p {:style {"max-width" "35em"}} [:b w] " " def])]))))

(defn main-component []
  [:div.p1
   [:h1 "Etym Deli Test"]
   [:p "Made as part of an "
    [:a {:href "http://experiments.oskarth.com/etym/"} "experiment"]
    ". All credit of the etymologies belongs to Douglas Harper and his "
    [:a {:href "http://etymonline.com/"} "Online Etymology Dictionary" ] "."]
   [etym-lookup]])

(defn update-autocomplete! [[_ event]]
  (reset! autocompleted (:autocomplete (second event))))


(defn handle-events []
  (go (while true
        (let [val (<! ch-chsk)]
          (update-autocomplete! (:event val))))))

;; When run this? When we connect to client
(handle-events)

(ns ^:figwheel-always instantetymonline.client
    (:require [reagent.core :as r]
              [instantetymonline.dict :refer [dict]]))

(enable-console-print!)

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
            :on-change (fn [e] (reset! local {:word (-> e .-target .-value)}))}]]
         [:br]
         [:p {:style {"max-width" "35em"}} (get dict (.toLowerCase word))]]))))


(defn main-component []
  [:div.p1
   [:h1 "Etym Deli"]
   [:p "Made as part of an "
    [:a {:href "http://experiments.oskarth.com/etym/"} "experiment"]
    ". All credit of the etymologies belongs to Douglas Harper and his "
    [:a {:href "http://etymonline.com/"} "Online Etymology Dictionary" ] "."]
   
   [etym-lookup]])

(r/render-component [main-component]
  (. js/document (getElementById "app")))

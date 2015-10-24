(ns ^:figwheel-always instantetymonline.client
    (:require [reagent.core :as r :refer [atom]]
              [instantetymonline.dict :refer [dict]]))

(enable-console-print!)

(defonce word (atom ""))

(defn text-input [label]
  [:div
   [:input {:type "text"
            :value @word
            :on-change (fn [e] (reset! word (-> e .-target .-value)))}]])

(defn main-component []
  [:body
   [text-input "word"]
   [:p (get dict @word)]])

(r/render-component [main-component]
  (. js/document (getElementById "app")))

(ns ^:figwheel-always instantetymonline.client
    (:require [reagent.core :as r]
              [instantetymonline.dict :refer [dict]]))

(enable-console-print!)

(defn etym-lookup []
  (let [local (r/atom {:word ""})]   ;; not included in render
    (fn []                           ;; render from here
      (let [word (get @local :word)]
        [:div.center
         [:form
          [:input.h2 {:id "etym"
                   :type "text"
                   :value word
                   :on-change (fn [e]
                                (reset! local {:word (-> e .-target .-value)}))}]]
         [:div [:p (get dict word)]]]))))

(defn main-component []
  [:div
   [:h1.center "Etymologically delicious"]
   [etym-lookup]])

(r/render-component [main-component]
  (. js/document (getElementById "app")))

(ns ^:figwheel-always instantetymonline.client
  (:require [reagent.core :as r :refer [atom]]))

(enable-console-print!)

(defn main-component []
  [:body "Hello world"])

(r/render-component [main-component]
  (. js/document (getElementById "app")))

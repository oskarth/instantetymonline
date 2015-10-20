(defproject instantetymonline "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.122"]
                 [enlive "1.1.6"]
                 [ring/ring-core "1.4.0"]
                 [reagent "0.5.1"]]
  :plugins      [[lein-figwheel "0.4.1"]]
  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src/"]
              :figwheel true
              :compiler {:main "instantetymonline.client"
                         :asset-path "js/out"
                         :output-to "resources/public/js/example.js"
                         :output-dir "resources/public/js/out"}}]})

(require 'cljs.build.api)

(cljs.build.api/build "src"
                      {:output-to "resources/public/js/main.js"
                       :optimizations :advanced})

(System/exit 0)

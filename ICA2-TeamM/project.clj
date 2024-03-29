(defproject TownTraversal "0.1.0-SNAPSHOT"
  :description "A clojure application comparing inference engines in a town traversal problem"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"] [criterium "0.4.4"]]
  :main ^:skip-aot TownTraversal.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})

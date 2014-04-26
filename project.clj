(defproject hypermq "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.6"]
                 [liberator "0.11.0"]
                 [korma "0.3.0-RC5"]
                 [clj-time "0.7.0"]]
  :plugins [[lein-ring "0.8.10"]]
  :ring {:handler hypermq.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]
                        [org.xerial/sqlite-jdbc "3.7.2"]]}})

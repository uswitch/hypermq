(defproject hypermq "0.1.0-SNAPSHOT"
  :uberjar-name "hypermq.jar"
  :repositories [["eaio.com" {:url "http://eaio.com/maven2/"
                              :checksum :ignore}]]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.eaio.uuid/uuid "3.4"]
                 [compojure "1.1.6"]
                 [liberator "0.11.0"]
                 [mysql/mysql-connector-java "5.1.6"]
                 [korma "0.3.0-RC5"]
                 [clj-time "0.7.0"]
                 [org.clojure/tools.logging "0.2.6"]
                 [org.clojure/data.json "0.2.4"]
                 [org.clojure/tools.reader "0.8.4"]
                 [bobby-conf "0.0.2"]
                 ;; logging
                 [org.clojure/tools.logging "0.3.1"]
                 [org.slf4j/slf4j-api              "1.7.24"]
                 [org.slf4j/log4j-over-slf4j       "1.7.24"]
                 [ch.qos.logback/logback-classic   "1.2.1" :exclusions [org.slf4j/slf4j-api]] ]
  :plugins [[lein-ring "0.8.10"]
            [lein-debian "0.2.0-SNAPSHOT"]
            [drift "1.5.2"]]
  :ring {:handler hypermq.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]
                        [org.xerial/sqlite-jdbc "3.7.2"]
                        [midje "1.5.1"]
                        [drift "1.5.2"]]}
   :uberjar {:aot :all}})

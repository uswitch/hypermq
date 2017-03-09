(ns hypermq.config
  (:require [clojure.tools.reader.edn :as edn]
            [clojure.java.io          :as io]
            [bobby-conf.core          :as bc]))

(bc/init :environments [:development :production])

(def dev-config {:db {:db "development.db"}
                 :hostname "localhost:4756"})

(def config
  (if (production?)
    (-> "config/production.edn" io/resource slurp edn/read-string)
    dev-config))

(def db
  (config :db))

(def hostname
  (config :hostname))

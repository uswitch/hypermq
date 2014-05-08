(ns hypermq.config
  (:require [clojure.tools.reader.edn :as edn]
            [bobby-conf.core          :as bc]))

(bc/init :environments [:development :production])

(def dev-config {:db {:db "development.db"}
                 :hostname "localhost:4756"})

(def config
  (if (production?)
    (edn/read-string (slurp "/opt/uswitch/hypermq/etc/hypermq-server.config"))
    dev-config))

(def db
  (config :db))

(def hostname
  (config :hostname))

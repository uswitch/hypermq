(ns hypermq.config
  (:require [clojure.tools.reader.edn :as edn]))

(def config
  (edn/read-string (slurp "/opt/uswitch/hypermq/etc/hypermq-server.config")))

(def db
  (config :db))

(def hostname
  (config :hostname))

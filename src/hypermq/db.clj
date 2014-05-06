(ns hypermq.db
  (:require [korma.core               :refer :all]
            [korma.db                 :refer [defdb mysql]]
            [hypermq.util             :as util]
            [clojure.tools.reader.edn :as edn]
            [hypermq.uuid             :as uuid]))

(defdb db (mysql
  (edn/read-string (slurp "/opt/uswitch/hypermq/etc/hypermq-server.config"))))

(defentity message
  (prepare   (util/mutate-row :body util/serialize))
  (transform (util/mutate-row :body util/de-serialize)))

(defentity acknowledgement
  (belongs-to message {:fk :message}))

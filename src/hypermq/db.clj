(ns hypermq.db
  (:require [korma.core               :refer :all]
            [korma.db                 :refer [defdb mysql sqlite3]]
            [hypermq.config           :as config]
            [hypermq.util             :as util]
            [hypermq.uuid             :as uuid]))

(defdb db (if (config/production?)
            (mysql config/db)
            (sqlite3 config/db)))

(defentity message
  (prepare   (util/mutate-row :body util/serialize))
  (transform (util/mutate-row :body util/de-serialize)))

(defentity acknowledgement
  (belongs-to message {:fk :message}))

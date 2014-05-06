(ns hypermq.db
  (:require [korma.core               :refer :all]
            [korma.db                 :refer [defdb mysql]]
            [hypermq.config           :as config]
            [hypermq.util             :as util]
            [hypermq.uuid             :as uuid]))

(defdb db (mysql config/db))

(defentity message
  (prepare   (util/mutate-row :body util/serialize))
  (transform (util/mutate-row :body util/de-serialize)))

(defentity acknowledgement
  (belongs-to message {:fk :message}))

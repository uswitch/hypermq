(ns hypermq.db
  (:require [korma.core   :refer :all]
            [korma.db     :refer [defdb sqlite3]]
            [hypermq.util :as util]
            [hypermq.uuid :as uuid]))

(defdb db (sqlite3 {:db "development.db"}))

(defentity message
  (prepare   (util/mutate-row :body util/serialize))
  (transform (util/mutate-row :body util/de-serialize)))

(defentity acknowledgement
  (belongs-to message {:fk :message}))

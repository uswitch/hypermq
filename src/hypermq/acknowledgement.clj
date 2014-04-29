(ns hypermq.acknowledgement
  (:require [hypermq.db :as db]))

(defn create
  [queue client {:keys [uuid]}]
  (db/insert-acknowledgement (queue :id) client uuid))

(defn get-latest
  [queue client]
  (first (db/select-acknowledgement (queue :id) client)))

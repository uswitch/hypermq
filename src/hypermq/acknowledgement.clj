(ns hypermq.acknowledgement
  (:require [hypermq.db :as db]))

(defn create
  [queue-title {:keys [client uuid]}]
  (let [queue (db/find-queue queue-title)]
    (db/insert-acknowledgement (queue :id) client uuid)))

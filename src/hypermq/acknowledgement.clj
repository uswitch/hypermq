(ns hypermq.acknowledgement
  (:require [hypermq.db :as db]))

(defn create
  [queue {:keys [client uuid] :as ack}]
  (db/insert-acknowledgement (queue :id) client uuid))

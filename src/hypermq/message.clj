(ns hypermq.message
  (:require [hypermq.db :as db]))

(defn find-by
  [uuid]
  {::item (db/get-message {:uuid uuid})})

(defn create
  [queue {:keys [title author content] :as ev}]
  (let [queue-id (db/find-or-create-queue queue)
        event-id (db/insert-message queue-id title author content)]
    {::item (db/get-message {:id event-id})}))

(defn build-url [message]
  (format "http://localhost:3000/m/%s" (message :uuid)))

(defn display
  [message]
  (assoc message :_links {:self {:href (build-url message)}}))

(defn total
  [queue]
  (db/message-count queue))

(defn get-page
  [queue page page-size]
  (db/messages-by-page queue page page-size))

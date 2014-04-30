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
  (format "/m/%s" (message :uuid)))

(defn display
  [message]
  (assoc message :_links {:self {:href (build-url message)}}))

(defn total
  [queue]
  (db/message-count queue))

(defn next-page
  [queue uuid page-size]
  (db/next-messages queue uuid page-size))

(defn prev-page
  [queue uuid page-size]
  (when uuid
    (reverse (db/prev-messages queue uuid page-size))))

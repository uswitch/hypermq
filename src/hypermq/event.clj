(ns hypermq.event
  (:require [hypermq.db :as db]))

(defn create
  [context queue]
  (let [queue-id (db/find-or-create-queue queue)]
    {::id  (db/insert-event
            queue-id
            (context :title)
            (context :author)
            (context :content))}))

(defn build-url [id]
  (format "http://localhost:3000/e/%s" id))

(defn display
  [event]
  (merge event {:_links {:self {:href (build-url (event :id))}}}))

(defn find-by
  [uuid]
  (db/find-event uuid))

(defn total
  [queue]
  (db/event-count queue))

(defn get-page
  [queue page page-size]
  (db/get-events queue page page-size))

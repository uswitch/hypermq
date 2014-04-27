(ns hypermq.event
  (:require [hypermq.db :as db]))

(defn find-by
  [uuid]
  {::item (db/get-event {:uuid uuid})})

(defn create
  [queue {:keys [title author content] :as ev}]
  (let [queue-id (db/find-or-create-queue queue)
        event-id (db/insert-event queue-id title author content)]
    {::item (db/get-event {:id event-id})}))

(defn build-url [event]
  (format "http://localhost:3000/m/%s" (event :uuid)))

(defn display
  [event]
  (merge event {:_links {:self {:href (build-url event)}}}))

(defn total
  [queue]
  (db/event-count queue))

(defn get-page
  [queue page page-size]
  (db/events-by-page queue page page-size))

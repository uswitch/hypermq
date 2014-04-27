(ns hypermq.db
  (:require [korma.core :refer :all]
            [korma.db :refer [defdb sqlite3]]
            [hypermq.util :as util]))

(defdb db (sqlite3 {:db "development.db"}))

(declare queue message)

(defentity queue
  (has-many message))

(defentity message
  (belongs-to queue {:fk :queue-id})
  (prepare (util/mutate-row :content util/serialize))
  (transform (util/mutate-row :content util/de-serialize)))

(defn get-message
  [selector]
  (first
   (select message
           (where selector))))

(defn message-count
  [queue-title]
  (-> (select message
              (aggregate (count 1) :total)
              (join :inner queue (= :queue.id :queue_id))
              (where {:queue.title queue-title}))
      first
      :total))

(defn messages-by-page
  [queue-title page page-size]
  (-> (select message
              (join :inner queue (= :queue.id :queue_id))
              (where {:queue.title queue-title})
              (order :id :ASC)
              (offset (* page page-size))
              (limit page-size))))

(defn find-queue
  [title]
  (first (select queue (where {:title title}))))

(defn last-insert-id
  [result]
  (-> result first val))

(defn insert-queue
  [title]
  (last-insert-id
   (insert queue (values {:title title :uuid (util/uuid)}))))

(defn find-or-create-queue
  [title]
  (let [queue (find-queue title)]
    (if queue
      (queue :id)
      (insert-queue title))))

(defn insert-message
  [queue-id title author content]
  (last-insert-id (insert message
                          (values {:uuid (util/uuid)
                                   :queue_id queue-id
                                   :title title
                                   :author author
                                   :content content
                                   :created (util/timestamp)}))))

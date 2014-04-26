(ns hypermq.db
  (:require [korma.core :refer :all]))

(defdb db (sqlite3 {:db "development.db"}))

(declare queue event)

(defentity queue
  (has-many event))

(defentity event
  (belongs-to queue))

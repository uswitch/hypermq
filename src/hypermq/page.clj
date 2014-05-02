(ns hypermq.page
  (:require [korma.core   :refer :all]
            [hypermq.db   :refer :all]))

(def page-size 2)

(defn previous-page
  [queue msg-id]
  (let [msg-id (or msg-id "")])
  (-> (select message
               (fields :id)
               (where {:queue queue :id [<= msg-id]})
               (order :id :DESC)
               (offset page-size)
               (limit 1))
      first
      :id))

(defn next-page
  [messages]
  (when (= page-size (count messages)) (:id (last messages))))

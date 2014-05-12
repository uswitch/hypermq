(ns hypermq.page
  (:require [korma.core      :refer :all]
            [hypermq.db      :refer :all]))

(def page-size 100)

(defn previous-page
  [queue msg-uuid]
  (let [msg    (first (select message (fields :id) (where {:uuid msg-uuid})))
        msg-id (or (:id msg) 0)]
    (-> (select message
                 (fields :uuid)
                 (where {:queue queue :id [<= msg-id]})
                 (order :id :DESC)
                 (offset page-size)
                 (limit 1))
        first
        :uuid)))

(defn next-page
  [messages]
  (when (= page-size (count messages)) (:uuid (last messages))))

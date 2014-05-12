(ns hypermq.queue
  (:require [korma.core      :refer :all]
            [hypermq.db      :refer :all]
            [hypermq.message :as msg]
            [hypermq.hal     :as hal]))

(defn latest
  []
  (select message
          (fields :queue :uuid)
          (aggregate (max :created) :last-modified)
          (group :queue)
          ;; This order by is a fudge to get the uuid of the last message in
          ;; each group (which is implicitly ordered by id)
          (order :queue :desc)))

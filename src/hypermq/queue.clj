(ns hypermq.queue
  (:require [korma.core      :refer :all]
            [hypermq.db      :refer :all]
            [hypermq.message :as msg]
            [hypermq.hal     :as hal]))

(defn latest
  []
  (select message
          (fields :queue, :uuid)
          (aggregate (max :created) :last-modified)
          (group :queue)))

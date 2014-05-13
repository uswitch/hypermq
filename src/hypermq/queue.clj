(ns hypermq.queue
  (:require [korma.core      :refer :all]
            [hypermq.db      :refer :all]
            [hypermq.message :as msg]
            [hypermq.hal     :as hal]))

(defn latest
  []
  (select message
          (fields :queue :uuid :created)
          (where {:id [in (subselect message
                                     (aggregate (max :id) :id)
                                     (group :queue))]})
          (order :queue :desc)))

(ns hypermq.ack
  (:require [korma.core   :refer :all]
            [hypermq.db   :refer :all]
            [hypermq.util :as util]))

(defn acknowledged?
  [queue client msg-id]
  (first (select acknowledgement
                 (fields :message)
                 (where {:queue queue :client client :message msg-id}))))

(defn create
  [queue client msg-id]
  (or (acknowledged? queue client msg-id)
      (insert acknowledgement
              (values {:queue queue
                       :client client
                       :message msg-id
                       :created (util/timestamp)}))))

(defn latest
  ([queue]
   (select acknowledgement
           (fields :client :message)
           (aggregate (max :created) :last-modified)
           (where {:queue queue})
           (group :client)))

  ([queue client]
   (first (select acknowledgement
                  (fields :message)
                  (where {:queue queue :client client})
                  (order :created :DESC)
                  (limit 1)))))

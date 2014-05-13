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
           (fields [:acknowledgement.client :client]
                   [:acknowledgement.message :message])
           (join message (= :acknowledgement.message :message.uuid))
           (where {:acknowledgement.queue queue
                   :message.id [in (subselect message
                                              (aggregate (max :id) :id)
                                              (where {:queue queue}))]})
           (group :client)
           (order :client :asc)))

  ([queue client]
   (first (select acknowledgement
                  (where {:queue queue :client client})
                  (order :created :DESC)
                  (limit 1)))))

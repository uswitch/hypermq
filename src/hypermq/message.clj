(ns hypermq.message
  (:refer-clojure :exclude [get])
  (:require [korma.core   :refer :all]
            [hypermq.db   :refer :all]
            [hypermq.page :as page]
            [hypermq.uuid :as uuid]
            [hypermq.util :as util]))

(defn get
  [msg-uuid]
  (first  (select message
                  (where {:uuid msg-uuid}))))

(defn fetch
  [queue last-seen]
  (let [uuid (or last-seen "")
        msg-id (or (:id (get uuid)) 0)]
    (select message
            (fields :uuid :queue :producer :created :body)
            (where {:queue queue :id [> msg-id]})
            (limit page/page-size)
            (order :id :ASC))))

(defn create
  [queue {:keys [producer body]}]
  (insert message
          (values {:uuid (uuid/generate)
                   :queue queue
                   :producer producer
                   :body body
                   :created (util/timestamp)})))

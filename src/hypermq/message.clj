(ns hypermq.message
  (:refer-clojure :exclude [get])
  (:require [korma.core   :refer :all]
            [hypermq.db   :refer :all]
            [hypermq.page :as page]
            [hypermq.uuid :as uuid]
            [hypermq.util :as util]))

(defn get
  [msg-id]
  (first  (select message
                  (where {:id msg-id}))))

(defn fetch
  [queue last-seen]
  (let [msg-id (or last-seen "")]
    (select message
            (where {:queue queue :id [> msg-id]})
            (limit page/page-size)
            (order :id :ASC))))

(defn create
  [queue {:keys [producer body]}]
  (insert message
          (values {:id (uuid/generate)
                   :queue queue
                   :producer producer
                   :body body
                   :created (util/timestamp)})))

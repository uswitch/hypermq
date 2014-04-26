(ns hypermq.queue
  (:require [hypermq.event :as event]))

(defn display
  [queue]
  {:queue queue
   :items (map event/display (event/get-all))})

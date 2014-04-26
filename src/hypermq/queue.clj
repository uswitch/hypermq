(ns hypermq.queue
  (:require [hypermq.event :as event]))

(def page-size 2)

(defn total-pages
  [queue]
  (quot (- (event/total queue) 1) page-size))

(defn current-page
  [page total-pages]
  (if (nil? page)
    total-pages
    (Integer/parseInt page)))

(defn display
  [queue & [page]]
  (let [total (total-pages queue)
        current (current-page page total)]
    {:queue queue
     :items (map event/display (event/get-page queue current page-size))}))

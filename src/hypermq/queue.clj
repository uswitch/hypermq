(ns hypermq.queue
  (:require [hypermq.event :as event]
            [hypermq.db :as db]))

(def page-size 2)

(defn total-pages
  [queue]
  (quot (- (event/total queue) 1) page-size))

(defn find-by
  [title]
  (db/find-queue title))

(defn current-page
  [page total-pages]
  (if (nil? page)
    total-pages
    (Integer/parseInt page)))

(defn build-url
  [queue page]
  (format "http://localhost:3000/q/%s/%s" queue page))

(defn build-links
  [queue current-page total-pages]
  (cond-> {:self {:href (build-url queue current-page)}}

          (< current-page total-pages)
          (merge {:next {:href (build-url queue (inc current-page))}})

          (< 0 current-page)
          (merge {:prev {:href (build-url queue (dec current-page))}})))

(defn display
  [queue & [page]]
  (let [total (total-pages queue)
        current (current-page page total)]
    {:queue queue
     :_links (build-links queue current total)
     :_embedded {:event (map event/display (event/get-page queue current page-size))}}))

(ns hypermq.queue
  (:require [hypermq.message :as msg]
            [hypermq.db :as db]))

(def page-size 2)

(defn total-pages
  [queue]
  (quot (- (msg/total queue) 1) page-size))

(defn current-page
  [page total-pages]
  (if (nil? page)
    total-pages
    (Integer/parseInt page)))

(defn etag
  [{:keys [messages]}]
  (-> messages first :uuid))

(defn last-modified
  [{:keys [messages]}]
  (-> messages first :created))

(defn messages-for
  [queue-title & [page]]
  (when-let [queue (db/find-queue queue-title)]
    (let [total (total-pages queue-title)
          current (current-page page total)
          messages (msg/get-page queue-title current page-size)]
      (merge queue {:messages messages
                    :current-page current
                    :total-pages total}))))

(defn build-url
  [queue page]
  (format "http://localhost/q/%s/%s" queue page))

(defn build-links
  [queue current-page total-pages]
  (cond-> {:self {:href (build-url queue current-page)}}

          (< current-page total-pages)
          (merge {:next {:href (build-url queue (inc current-page))}})

          (< 0 current-page)
          (merge {:prev {:href (build-url queue (dec current-page))}})))

(defn display
  [{:keys [title uuid messages current-page total-pages]}]
  {:queue title
   :uuid uuid
   :_links (build-links title current-page total-pages)
   :_embedded {:message (map msg/display messages)}})

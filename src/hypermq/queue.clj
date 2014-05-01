(ns hypermq.queue
  (:require [hypermq.message :as msg]
            [hypermq.db :as db]))

(def page-size 2)

(defn etag
  [{:keys [messages]}]
  (-> messages last :uuid))

(defn last-modified
  [{:keys [messages]}]
  (-> messages last :created))

(defn find-by
  [queue-title]
  (db/find-queue queue-title))

(defn next-page-uuid
  [messages]
  (when (= (count messages) page-size)
    (:uuid (last messages))))

(defn prev-page-uuid
  [messages]
  (when (= (count messages) page-size)
    (:uuid (first messages))))

(defn messages-for
  [queue-title & [uuid]]
  (when-let [queue (find-by queue-title)]
    (let [messages (msg/next-page queue uuid page-size)
          prev-page-messages (msg/prev-page queue uuid page-size)]
      (merge queue {:messages messages
                    :current-page uuid
                    :next-page (next-page-uuid messages)
                    :prev-page (prev-page-uuid prev-page-messages)}))))

(defn build-url
  [queue uuid]
  (if uuid
    (format "/q/%s/%s" queue uuid)
    (format "/q/%s" queue)))

(defn build-links
  [{:keys [title current-page next-page prev-page]}]
  (cond-> {:self {:href (build-url title current-page)}}

          next-page
          (merge {:next {:href (build-url title next-page)}})

          (or current-page prev-page)
          (merge {:prev {:href (build-url title prev-page)}})))

(defn display
  [{:keys [title uuid messages] :as queue}]
  {:queue title
   :uuid uuid
   :_links (build-links queue)
   :_embedded {:message (map msg/display messages)}})

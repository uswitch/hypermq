(ns hypermq.hal
  (:require [clojure.string  :as s]
            [hypermq.config  :as config]
            [hypermq.page    :as page]))

(defn- url
  [queue msg-uuid]
  (if msg-uuid
    (format "http://%s/q/%s/%s" config/hostname queue msg-uuid)
    (format "http://%s/q/%s" config/hostname queue)))

(defn- links
  [queue msg-uuid messages]
  (let [prev-page (page/previous-page queue msg-uuid)
        next-page (page/next-page messages)]

    (cond->
      {:self {:href (url queue msg-uuid)}}

      (or prev-page msg-uuid)
      (merge {:prev {:href (url queue prev-page)}})

      next-page
      (merge {:next {:href (url queue next-page)}}))))

(defn page
  [queue msg-uuid messages]
  {:queue     queue
   :_embedded {:message messages}
   :_links    (links queue msg-uuid messages)})

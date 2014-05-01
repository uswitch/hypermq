(ns hypermq.hal
  (:require [clojure.string :as s]
            [hypermq.page   :as page]))

(def host "localhost:3000")

(defn- url
  [queue msg-id]
  (if msg-id
    (format "http://%s/q/%s/%s" host queue msg-id)
    (format "http://%s/q/%s" host queue)))

(defn- links
  [queue msg-id messages]
  (let [prev-page (page/previous-page queue msg-id)
        next-page (page/next-page messages)]

    (cond->
      {:self {:href (url queue msg-id)}}

      (or prev-page msg-id)
      (merge {:prev {:href (url queue prev-page)}})

      next-page
      (merge {:next {:href (url queue next-page)}}))))

(defn page
  [queue msg-id messages]
  {:queue     queue
   :_embedded {:message messages}
   :_links    (links queue msg-id messages)})

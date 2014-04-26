(ns hypermq.event
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]))

(defn timestamp [] (c/to-long (t/now)))

(defonce events (ref [{:id 1 :timestamp (timestamp) :author "Christian" :body "foo"}]))

(defn create
  [context]
  (dosync
   (let [next-id (inc (count @events))]
     (alter events conj {:id next-id :timestamp (timestamp)})
     {::id next-id})))

(defn build-url [id]
  (format "http://localhost:3000/e/%s" id))

(defn display
  [event]
  (merge event {:link (build-url (event :id))}))

(defn display-all
  [queue]
  {:queue queue :items (map display @events)})

(defn find-by
  [id]
  (prn "find-by" id " in " @events)
  (when-let [e (@events (dec (Integer/parseInt id)))]
    {::event e}))

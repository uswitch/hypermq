(ns hypermq.queue
  (:require [hypermq.event :as event]))

(def page-size 2)

(defn total-archives
  [queue]
  (prn "total-achives" queue)
  (quot (event/total queue) page-size))

(defn archive-number
  [queue archive]
  (if (nil? archive)
    (total-archives queue)
    (Integer/parseInt archive)))

(defn display
  [queue & [archive]]
  (prn "ARCHIVE" (or archive (total-archives queue)))
  (let [archive (archive-number queue archive)]
    {:queue queue
     :items (map event/display (event/get-page queue archive page-size))}))

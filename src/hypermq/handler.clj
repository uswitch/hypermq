(ns hypermq.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [liberator.core :refer [defresource]]
            [hypermq.queue :as queue]
            [hypermq.event :as event]))

(defresource archive-events
  [queue archive]
  :available-media-types ["application/json" "application/hal+json"]
  :allowed-methods [:get]
  :handle-ok (fn [_] (queue/display queue archive)))

(defresource recent-events
  [queue-title]
  :available-media-types ["application/json" "application/hal+json"]
  :allowed-methods [:get :post]
  :exists? (fn [_] (queue/find-by queue-title))
  :post! #(event/create % queue-title)
  :post-redirect? true
  :location (fn [context] (event/build-url (:hypermq.event/id context)))
  :handle-ok (fn [_] (queue/display queue-title)))

(defresource event
  [id]
  :allowed-methods [:get]
  :available-media-types ["application/json" "application/hal+json"]
  :exists? (fn [_] (event/find-by id))
  :handle-ok (fn [context] (event/display (context :hypermq.event/event)))
  :handle-not-found "Event not found!")

(defroutes app-routes
  (GET "/" [] "Home Page")
  (ANY "/q/:queue" [queue] (recent-events queue))
  (ANY "/q/:queue/:archive" [queue archive] (archive-events queue archive))
  (ANY "/e/:id" [id] (event id))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

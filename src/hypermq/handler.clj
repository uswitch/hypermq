(ns hypermq.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [liberator.core :refer [defresource]]
            [hypermq.queue :as queue]
            [hypermq.event :as event]))

(defresource list-events
  [queue]
  :available-media-types ["application/json" "application/hal+json"]
  :allowed-methods [:get :post]
  :post! event/create
  :post-redirect? true
  :location (fn [context] (event/build-url (:hypermq.event/id context)))
  :handle-ok (fn [_] (queue/display queue)))

(defresource event
  [id]
  :allowed-methods [:get]
  :available-media-types ["application/json" "application/hal+json"]
  :exists? (fn [_] (event/find-by id))
  :handle-ok (fn [context] (event/display (context :hypermq.event/event)))
  :handle-not-found "Event not found!")

(defroutes app-routes
  (GET "/" [] "Home Page")
  (ANY "/q/:queue" [queue] (list-events queue))
  (ANY "/e/:id" [id] (event id))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

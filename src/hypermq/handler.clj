(ns hypermq.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [liberator.core :refer [defresource]]))

(defresource queue
  [name]
  :available-media-types ["text/plain"]
  :handle-ok (fn [_] (format "<h1>Events for %s queue</h1>" name)))

(defresource event
  [id]
  :available-media-types ["text/plain"]
  :handle-ok (fn [_] (format "<h1>This is the event %s</h1>" id)))

(defroutes app-routes
  (GET "/" [] "Home Page")
  (ANY "/q/:name" [queue] (queue name))
  (ANY "/e/:id" [id] (event id))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

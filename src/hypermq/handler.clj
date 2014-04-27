(ns hypermq.handler
  (:use compojure.core)
  (:require [compojure.handler   :as handler]
            [compojure.route     :as route]
            [liberator.core      :refer [defresource]]
            [hypermq.queue       :as queue]
            [hypermq.event       :as event]
            [hypermq.json        :as js]))

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
  :malformed? js/parse-body
  :post! (fn [context] (event/create queue-title (context :data)))
  :post-redirect? true
  :location (fn [context] (event/build-url (context :hypermq.event/item)))
  :handle-ok (fn [_] (queue/display queue-title)))

(defresource event
  [uuid]
  :allowed-methods [:get]
  :available-media-types ["application/json" "application/hal+json"]
  :exists? (fn [_] (event/find-by uuid))
  :handle-ok (fn [context] (event/display (context :hypermq.event/item)))
  :handle-not-found "Event not found!")

(defroutes app-routes
  (GET "/" [] "Home Page")
  (ANY "/q/:queue" [queue] (recent-events queue))
  (ANY "/q/:queue/:archive" [queue archive] (archive-events queue archive))
  (ANY "/e/:uuid" [uuid] (event uuid))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

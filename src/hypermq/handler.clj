(ns hypermq.handler
  (:use compojure.core)
  (:require [compojure.handler   :as handler]
            [compojure.route     :as route]
            [liberator.core      :refer [defresource]]
            [hypermq.queue       :as queue]
            [hypermq.event       :as msg]
            [hypermq.json        :as js]))

(defresource archive-messages
  [queue-title archive]
  :available-media-types ["application/json" "application/hal+json"]
  :allowed-methods [:get]
  :exists? (fn [_]
             (when-let [items (queue/find-by queue-title archive)]
               {:items items}))
  :handle-ok (fn [context] (queue/display (context :items))))

(defresource recent-messages
  [queue-title]
  :available-media-types ["application/json" "application/hal+json"]
  :allowed-methods [:get :post]
  :exists? (fn [_]
             (when-let [items (queue/find-by queue-title)]
               {:items items}))
  :malformed? js/parse-body
  :post! (fn [context] (msg/create queue-title (context :data)))
  :post-redirect? true
  :location (fn [context] (msg/build-url (context :hypermq.event/item)))
  :handle-ok (fn [context] (queue/display (context :items))))

(defresource message
  [uuid]
  :allowed-methods [:get]
  :available-media-types ["application/json" "application/hal+json"]
  :exists? (fn [_] (msg/find-by uuid))
  :handle-ok (fn [context] (msg/display (context :hypermq.event/item)))
  :handle-not-found "Event not found!")

(defroutes app-routes
  (GET "/" [] "Home Page")
  (ANY "/q/:queue" [queue] (recent-messages queue))
  (ANY "/q/:queue/:archive" [queue archive] (archive-events queue archive))
  (ANY "/m/:uuid" [uuid] (message uuid))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

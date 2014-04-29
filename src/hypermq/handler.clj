(ns hypermq.handler
  (:use compojure.core)
  (:require [compojure.handler       :as handler]
            [compojure.route         :as route]
            [liberator.core          :refer [defresource]]
            [hypermq.queue           :as queue]
            [hypermq.message         :as msg]
            [hypermq.json            :as js]
            [hypermq.acknowledgement :as ack]
            [hypermq.views           :as view]))

(defresource archive-messages
  [queue-title archive]
  :available-media-types ["application/json" "application/hal+json"]
  :allowed-methods [:get]
  :exists? (fn [_]
             (when-let [items (queue/messages-for queue-title archive)]
               {:items items}))
  :etag (fn [context] (queue/etag (context :items)))
  :last-modified (fn [context] (queue/last-modified (context :items)))
  :handle-ok (fn [context] (queue/display (context :items))))

(defresource recent-messages
  [queue-title]
  :available-media-types ["application/json" "application/hal+json"]
  :allowed-methods [:get :post]
  :exists? (fn [_]
             (when-let [items (queue/messages-for queue-title)]
               {:items items}))
  :etag (fn [context] (queue/etag (context :items)))
  :last-modified (fn [context] (queue/last-modified (context :items)))
  :malformed? js/parse-body
  :post! (fn [context] (msg/create queue-title (context :data)))
  :post-redirect? true
  :location (fn [context] (msg/build-url (context :hypermq.message/item)))
  :handle-ok (fn [context] (queue/display (context :items))))

(defresource message
  [uuid]
  :allowed-methods [:get]
  :available-media-types ["application/json" "application/hal+json"]
  :exists? (fn [_] (msg/find-by uuid))
  :handle-ok (fn [context] (msg/display (context :hypermq.message/item)))
  :handle-not-found "Message not found!")

(defresource acknowledgement
  [queue-title client]
  :allowed-methods [:get :post]
  :available-media-types ["application/json"]
  :exists? (fn [_] (when-let [queue (queue/find-by queue-title)] {:queue queue}))
  :malformed? js/parse-body
  :can-post-to-missing? false
  :post! (fn [context] (ack/create (context :queue) client (context :data)))
  :handle-ok (fn [context] (ack/get-latest (context :queue) client)))

(defroutes app-routes
  (GET "/" [] "Home Page")
  (GET "/monitoring" [] (view/monitoring))
  (ANY "/q/:queue" [queue] (recent-messages queue))
  (ANY "/q/:queue/:archive" [queue archive] (archive-messages queue archive))
  (ANY "/m/:uuid" [uuid] (message uuid))
  (ANY "/ack/:queue/:client" [queue client] (acknowledgement queue client))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

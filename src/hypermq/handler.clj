(ns hypermq.handler
  (:use compojure.core)
  (:require [compojure.handler       :as handler]
            [compojure.route         :as route]
            [hypermq.resources       :as resource]
            [hypermq.views           :as view]))

(defroutes app-routes
  (GET  "/"                   []             (view/home))
  (GET  "/health"             []             "OK")
  (GET  "/monitoring"         []             (view/monitoring))

  (GET  "/m/:msg-id"          {params :params} (resource/message params))
  (POST "/q/:queue"           {params :params} (resource/message params))

  (GET  "/q/:queue"           [queue]        (resource/queue queue))
  (GET  "/q/:queue/:msg-id"   [queue msg-id] (resource/queue queue msg-id))

  (ANY  "/ack/:queue/:client" [queue client] (resource/ack queue client))

  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

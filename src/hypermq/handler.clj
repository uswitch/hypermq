(ns hypermq.handler
  (:use compojure.core)
  (:require [compojure.handler       :as handler]
            [compojure.route         :as route]
            [hypermq.resources       :as resource]
            [hypermq.views           :as view]))

(defroutes app-routes
  (GET "/" [] (view/home))
  (GET "/monitoring" [] (view/monitoring))
  (ANY "/q/:queue" [queue] (resource/list-message-head queue))
  (ANY "/q/:queue/:uuid" [queue uuid] (resource/list-message-tail queue uuid))
  (ANY "/m/:uuid" [uuid] (resource/message uuid))
  (ANY "/ack/:queue/:client" [queue client] (resource/acknowledgement queue client))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

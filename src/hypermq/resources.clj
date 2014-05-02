(ns hypermq.resources
  (:require [liberator.core  :refer [defresource]]
            [hypermq.queue   :as queue]
            [hypermq.hal     :as hal]
            [hypermq.message :as msg]
            [hypermq.json    :as js]
            [hypermq.ack     :as ack]))

(declare media-types etag last-modified display-messages
         create-message unread-messages? ack-message display-ack)

(defresource message
  [queue]
  :available-media-types ["application/json" "application/hal+json"]
  :allowed-methods       [:post]
  :malformed?            js/parse-body
  :post!                 (create-message queue))

(defresource queue
  [queue & [msg-id]]
  :available-media-types ["application/json" "application/hal+json"]
  :allowed-methods       [:get]
  :exists?               (unread-messages? queue msg-id)
  :etag                  etag
  :last-modified         last-modified
  :handle-ok             (display-messages queue msg-id))

(defresource ack
  [queue client]
  :allowed-methods       [:get :post]
  :available-media-types ["application/json"]
  :malformed?            js/parse-body
  :can-post-to-missing?  false
  :post!                 (ack-message queue client)
  :handle-ok             (display-ack queue client))

(defn- etag
  [context]
  (-> context :items last :id))

(defn- last-modified
  [context]
  (-> context :items last :created))

(defn- display-messages
  [queue msg-id]
  (fn [context] (hal/page queue msg-id (context :items))))

(defn- create-message
  [queue]
  (fn [context] (msg/create queue (context :data))))

(defn- unread-messages?
  [queue msg-id]
  (fn [_] (when-let [items (msg/fetch queue msg-id)] {:items items})))

(defn- ack-message
  [queue client]
  (fn [context] (ack/create queue client (get-in context [:data :id]))))

(defn- display-ack
  [queue client]
  (fn [context] (ack/latest queue client)))

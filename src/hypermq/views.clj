(ns hypermq.views
  (:use [hiccup core page])
  (:require [hypermq.db   :as db]
            [hypermq.util :as util]))

(defn- layout
   [{:keys [body]}]
  (html5
   [:head
    [:title "Hypermq - Hypermedia message queue server"]
    [:link {:rel "stylesheet" :href "//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css"}]
    [:link {:rel "stylesheet" :href "//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap-theme.min.css"}]]
   [:body

    [:div.navbar.navbar-inverse {:role "navigation"}
     [:div.container
      [:div.navbar-header
       [:button.navbar-toggle {:type "button" :data-toggle "collapse" :data-target ".navbar-collapse"}
        [:span.sr-only "Toggle navigation"]
        [:span.icon-bar]
        [:span.icon-bar]
        [:span.icon-bar]]
       [:a.navbar-brand {:href ""} "Hypermq Dashboard"]]
      [:div.collapse.navbar-collapse
       [:ul.nav.navbar-nav
        [:li.active [:a {:href "/"} "Home"]]
        [:li [:a {:href "/monitoring"} "Monitoring"]]]]]]

    [:div.container
     body
     [:script {:src "//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"}]]]))

(defn- acknowledgement
  [queue-uuid {:keys [client uuid]}]
  (let [up-to-date (= uuid queue-uuid)]
    [:li [:button {:type "button" :class (str  "btn btn-xs " (if up-to-date "btn-success" "btn-danger"))} client]]))

(defn- queue-status
  [{:keys [title uuid queue_id]}]
  [:li.col-md-3
   [:h4 title]
   [:p.text-muted [:small uuid]]
   [:ul.list-inline
    (map (partial acknowledgement uuid) (db/latest-acknowledgements queue_id))]])

(defn monitoring
  []
  (layout {:body
    [:div
     [:h1 "Hypermq Monitoring Dashboard"]
     [:h2 "Queue / Client status"]
     [:ul.list-inline
      (map queue-status (db/queue-latest))]]}))

(defn index
  []
  (layout {:body
           [:div [:h1 "Hypermq Server Home"]]}))

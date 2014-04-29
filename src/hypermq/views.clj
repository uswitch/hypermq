(ns hypermq.views
  (:use [hiccup core page])
  (:require [hypermq.db   :as db]
            [hypermq.util :as util]))

(defn monitoring
  []
  (html5
   [:head
    [:title "Hello World"]
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
     [:h1 "Hypermq Monitoring Dashboard"]
     (for [q (db/queue-latest)]
       [:div.col-md-4
        [:dl
         [:dt (q :title) " " [:span.text-info (util/timestamp->str (q :last-modified))]]
         [:dd.text-info (q :uuid)]
         (for [ack (db/latest-acknowledgements (q :queue_id))]
           [:dd.text-muted [:strong (ack :client)] " : " (ack :uuid) " " (util/timestamp->str (ack :last-modified))]
           )]])]]))

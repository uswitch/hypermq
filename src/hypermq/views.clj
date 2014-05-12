(ns hypermq.views
  (:use [hiccup core page])
  (:require [hypermq.db    :as db]
            [hypermq.ack   :as ack]
            [hypermq.queue :as queue]
            [hypermq.util  :as util]))

(defn tab-active?
  [tab expected]
  (when (= tab expected) {:class "active"}))

(defn- layout
   [{:keys [tab body]}]
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
        [:li (tab-active? tab :home) [:a {:href "/"} "Home"]]
        [:li (tab-active? tab :monitoring) [:a {:href "/monitoring"} "Monitoring"]]]]]]

    [:div.container
     body
     [:script {:src "//code.jquery.com/jquery-1.11.0.min.js"}]
     [:script {:src "//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"}]]]))

(defn- acknowledgement
  [queue-latest {:keys [client message]}]
  (let [up-to-date (= message queue-latest)]
    [:li [:button {:type "button" :class (str  "btn btn-xs " (if up-to-date "btn-success" "btn-danger"))} client]]))

(defn- queue-status
  [{:keys [queue uuid last-modified]}]
  [:li.col-md-3
   [:h4 queue]
   [:p (util/timestamp->str last-modified)]
   [:p.text-muted [:small uuid]]
   [:ul.list-inline
    (map (partial acknowledgement uuid) (ack/latest queue))]])

(defn monitoring
  []
  (layout {:tab :monitoring
           :body [:div
                  [:h1 "Hypermq Monitoring Dashboard"]
                  [:h2 "Queue / Client status"]
                  [:ul.list-inline
                   (map queue-status (queue/latest))]]}))

(defn home
  []
  (layout {:tab :home
           :body
           [:div
            [:h1 "Hypermq Server Home"]
            [:h2 "Active Queues"]
            [:ul.list-inline
             (for [{:keys [queue]} (queue/latest)]
               [:li {:style "margin-bottom:10px;"} [:a.btn.btn-info.btn-lg {:role "button" :href (str "/q/" queue)} queue]])]]}))

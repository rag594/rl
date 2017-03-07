(ns play.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [ajax.core :refer [GET POST]]
            [play.ajax :refer [load-interceptors!]]
            [play.handlers]
            [play.subscriptions])
  (:import goog.History))

(defn nav-link [uri title page collapsed?]
  (let [selected-page (rf/subscribe [:page])]
    [:li.nav-item
     {:class (when (= page @selected-page) "active")}
     [:a.nav-link
      {:href uri
       :on-click #(reset! collapsed? true)} title]]))

(defn navbar []
  (r/with-let [collapsed? (r/atom true)]
    [:nav.navbar.navbar-dark.bg-primary
     [:button.navbar-toggler.hidden-sm-up
      {:on-click #(swap! collapsed? not)} "☰"]
     [:div.collapse.navbar-toggleable-xs
      (when-not @collapsed? {:class "in"})
      [:a.navbar-brand {:href "#/"} "play"]
      [:ul.nav.navbar-nav
       [nav-link "#/" "Home" :home collapsed?]
       [nav-link "#/about" "About" :about collapsed?]]]]))

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     [:img {:src (str js/context "/img/warning_clojure.png")}]]]])

(defn counter []
  [:div.panel
   [:button.btn
    {:on-click #(rf/dispatch [:increase-counter])} "Increase"]
   [:p.panel @(rf/subscribe [:counter])]
   [:button.btn
    {:on-click #(rf/dispatch [:decrease-counter])} "Decrease"]])

(defn pagination []
  [:div.pager
    [:button {:class "btn btn-primary"} "Previous"]
    [:button.btn "Next"]])

(defn table-row [item]
  "Create a table row with items as row's value"
  ^{:key item} ;metadata needed for react
  [:tr
   (map  ; td tag for each item
     #(-> ^{:key %} [:td %])
     item)])

(defn table []
  "Create a table with headings and items as subscribed"
  [:table.table
    [:tbody
      [:tr
       (map ; Generate <th> for each heading
         #(-> ^{:key %} [:th %])
          @(rf/subscribe [:headings]))]
      (map ; Table row for each item
        #(table-row %)
        @(rf/subscribe [:data-items]))]])

(defn home-page []
  "Home Component"
  [:div
    [table]
    [:button
      {:on-click #(rf/dispatch [:get-hacker-news-article])
        :class "btn btn-default"}
      "Refresh"]])

(def pages
  {:home #'home-page
   :about #'about-page})

(defn page []
  [:div
   [navbar]
   [(pages @(rf/subscribe [:page]))]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (rf/dispatch [:set-active-page :home]))

(secretary/defroute "/about" []
  (rf/dispatch [:set-active-page :about]))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(rf/dispatch [:set-docs %])}))

(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))

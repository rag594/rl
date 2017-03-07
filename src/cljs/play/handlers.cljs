(ns play.handlers
  (:require [play.db :as db]
            [ajax.core :as ajax]
            [day8.re-frame.http-fx]
            [re-frame.core :refer [dispatch reg-event-db reg-event-fx]]))

(reg-event-db
  :initialize-db
  (fn [_ _]
    (dispatch [:get-hacker-news-article])
    db/default-db))

(reg-event-fx
  :get-hacker-news-article
  (fn [{:keys [db]} _]
    {:http-xhrio {
                  :method :get
                  :uri "https://hacker-news.firebaseio.com/v0/topstories.json"
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [:process-response]
                  :on-failure [:bad-response]}
     :db (assoc db :isLoading? true)}))

(reg-event-db
  :process-response
  (fn [db [_ response]]
      (-> db
          (assoc :loading? false)
          (assoc-in [:data :values] (map #(-> [%]) response)))))

(reg-event-db
  :bad-response
  (fn [db [_ response]]
    (.log js/console response)))


(reg-event-db
  :set-active-page
  (fn [db [_ page]]
    (assoc db :page page)))

(reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))

(reg-event-db
  :increase-counter
  (fn [db [_ _]]
    (update-in db [:counter] inc)))

(reg-event-db
  :decrease-counter
  (fn [db [_ _]]
    (update-in db [:counter] dec)))

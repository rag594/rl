(ns play.subscriptions
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :page
  (fn [db _]
    (:page db)))

(reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(reg-sub
  :counter
  (fn [db _]
    (:counter db)))

(reg-sub
  :data
  (fn [db _]
    (:data db)))

(reg-sub
  :headings
  (fn [db _]
    (:keys (:data db))))

(defn min-count [key data]
  (min (key data) (count (:values data))))

(reg-sub
  :data-items 
  (fn [db _]
    (let [data (:data db)]
      (take 5 (:values data)))))

(reg-sub
  :nav-info
  (fn [db _]
    (let [data (:data db)]
      {:from (:current-from data)
        :to (:current-to data)})))

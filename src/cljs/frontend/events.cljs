(ns frontend.events
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]
            [day8.re-frame.http-fx]))

;;dispatchers

(rf/reg-event-db
  :navigate
  (fn [db [_ page]]
    (assoc db :page page)))

(rf/reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))

(rf/reg-event-fx
  :fetch-docs
  (fn [_ _]
    ;; https://github.com/Day8/re-frame-http-fx
    {:http-xhrio {:method :get
                  :uri "/docs"
                  :response-format (ajax/raw-response-format)
                  :on-success [:set-docs]}}))

;;subscriptions
(rf/reg-sub
  :page
  (fn [db _]
    (:page db)))

(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))

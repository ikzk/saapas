(ns frontend.core
  (:require [reagent.core :as r]
            [common.hello :refer [foo-cljc]]))

;; Reagent application state
;; Defonce used to that the state is kept between reloads
(defonce app-state (r/atom {:y 2017}))

(defn main []
  [:div
   [:h1 (foo-cljc (:y @app-state))]
   [:div.btn-toolbar
    [:button.btn.btn-danger
     {:type "button"
      :on-click #(swap! app-state update :y inc)} "+"]
    [:button.btn.btn-success
     {:type "button"
      :on-click #(swap! app-state update :y dec)} "-"]
    [:button.btn.btn-default
     {:type "button"
      :on-click #(js/console.log @app-state)}
     "Console.log"]]])

(defn start! []
  (js/console.log "Starting the app")
  (r/render-component [main] (js/document.getElementById "app")))

;; When this namespace is (re)loaded the Reagent app is mounted to DOM
(start!)

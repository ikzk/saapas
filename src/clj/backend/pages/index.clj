(ns backend.pages.index
  (:require
   [hiccup.core :refer [html]]
   [hiccup.page :refer [html5 include-js include-css]]))

(def index-page
  (html
   (html5
    [:head
     [:title "Saapas"]
     [:meta {:charset "utf-8"}]
     [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
     [:style {:id "_stylefy-constant-styles_"}]
     [:style {:id "_stylefy-styles_"}]
     (include-css "/css/main.css")]
    [:body
     [:div.container [:div#app.app-wrapper]]
     (include-js "/js/main.js")])))

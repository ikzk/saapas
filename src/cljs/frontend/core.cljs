(ns frontend.core
  (:require [baking-soda.core :as b]
            [taoensso.timbre :as timbre]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [bide.core :as bide]
            [frontend.events]
            [stylefy.core :as stylefy]))

; the navbar components are implemented via baking-soda [1]
; library that provides a ClojureScript interface for Reactstrap [2]
; Bootstrap 4 components.
; [1] https://github.com/gadfly361/baking-soda
; [2] http://reactstrap.github.io/

(defn nav-link [uri title page]
  [b/NavItem [b/NavLink
              {:href   uri
               :active (when (= page @(rf/subscribe [:page])) "active")}
              title]])

(defn navbar []
  (r/with-let [expanded? (r/atom true)]
    [b/Navbar {:light true
               :class-name "navbar-dark bg-primary"
               :expand "md"}
     [b/NavbarBrand {:href "/"} "my-app"]
     [b/NavbarToggler {:on-click #(swap! expanded? not)}]
     [b/Collapse {:is-open @expanded? :navbar true}
      [b/Nav {:class-name "mr-auto" :navbar true}
       [nav-link "#/" "Home" :home]
       [nav-link "#/about" "About" :about]]]]))

(defn about-page []
  (fn []
    [:div.container
     [:div.row
      [:div.col-md-12
       "about page"]]]))

(defn home-page []
  (fn []
    [:div.container
     [:div.row>div.col-sm-12
      [:h2.alert.alert-info "Ahaha"]]
     (when-let [docs @(rf/subscribe [:docs])]
       [:div.row>div.col-sm-12
        [:div (stylefy/use-style {:text-align "center"}) docs]])]))

(defn page []
  (fn []
    [:div
     [navbar]
     (case @(rf/subscribe [:page])
       :home  [home-page]
       :about [about-page])]))

;; -------------------------
;; Routes

(def bide-router
  (bide/router [["/" :home]
                ["/about" :about]]))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn on-navigate
  [name params query]
  (rf/dispatch [:navigate name]))

(defn hook-browser-navigation! []
  (bide/start! bide-router {:default :home
                            :on-navigate on-navigate}))

;; -------------------------
;; Initialize app
(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (timbre/info "Starting the app")
  (rf/dispatch-sync [:navigate :home]) ;; must be sync here to avoid race condition
  (rf/dispatch-sync [:fetch-docs])
  (stylefy/init)
  (hook-browser-navigation!)
  (mount-components))

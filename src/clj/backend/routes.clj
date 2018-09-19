(ns backend.routes
  (:require [clojure.java.io :as io]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [resources files]]
            [compojure.handler :refer [api]]
            [ring.util.response :refer [redirect]]
            [ring.util.http-response :refer :all]
            [backend.pages.index :refer [index-page]]))


(defroutes routes
  ;; Note: when running uberjar from project dir, it is
  ;; possible that this dir exists.
  ;; (if (.exists (io/file "dev-output/js"))
  ;;   (files "/js" {:root "dev-output/js"})
  (resources "/js" {:root "js"})
  ;; )

  ;; (if (.exists (io/file "dev-output/css"))
  ;;   (files "/css" {:root "dev-output/css"})
  (resources "/css" {:root "css"})
  ;; )

  (GET "/" []
       ;; Use (resource-response "index.html") to serve index.html from classpath
       (-> (ok index-page) (content-type "text/html")))

  (GET "/docs" []
       ;; Use (resource-response "index.html") to serve index.html from classpath
       (-> (ok "something") (content-type "text/html"))))

(ns backend.server
  (:require [clojure.java.io :as io]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [resources files]]
            [compojure.handler :refer [api]]
            [ring.util.response :refer [redirect]]
            [ring.util.http-response :refer :all]
            [immutant.web :as immutant]
            [mount.core :as mount]
            [backend.config :refer [env]]
            [clojure.set :refer [rename-keys]]

            [taoensso.timbre :as timbre]
            [backend.index :refer [index-page test-page]]))

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
    ; Use (resource-response "index.html") to serve index.html from classpath
    (-> (ok index-page) (content-type "text/html")))
  (GET "/test" []
    (-> (ok test-page) (content-type "text/html"))))

(defn run-options [opts]
  (let [immutant-opts (merge {:host "0.0.0.0"}
                             (-> opts
                                 ;;options contain EVN path
                                 ;;Immutant path is found at :handler-path
                                 (dissoc :path :contexts)
                                 (rename-keys {:handler-path :path})
                                 (select-keys
                                  (-> #'immutant/run meta :valid-options))))]
    (timbre/info "immutant http server opts" immutant-opts)
    immutant-opts))

(defn start [{:keys [handler port] :as opts}]
  (try
    (timbre/info "starting HTTP server on port" port)
    (immutant/run handler (run-options opts))
    (catch Throwable t
      (timbre/error t (str "server failed to start on port " port))
      (throw t))))

(defn stop [server]
  (immutant/stop server)
  (timbre/info "HTTP server stopped"))

(mount/defstate ^{:on-reload :noop} http-server
  :start
  (start (-> env
             (assoc  :handler #'backend.server/routes)
             (update :io-threads #(or % (* 2 (.availableProcessors (Runtime/getRuntime)))))
             (update :port #(or (-> env :options :port) %))))
  :stop
  (stop http-server))

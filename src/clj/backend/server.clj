(ns backend.server
  (:require [immutant.web :as immutant]
            [mount.core :as mount]
            [backend.config :refer [env]]
            [backend.routes]
            [clojure.set :refer [rename-keys]]
            [taoensso.timbre :as timbre]))

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
             (assoc  :handler #'backend.routes/routes)
             (update :io-threads #(or % (* 2 (.availableProcessors (Runtime/getRuntime)))))
             (update :port #(or (-> env :options :port) %))))
  :stop
  (stop http-server))

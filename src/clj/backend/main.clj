(ns backend.main
  (:require [mount.core :refer [defstate] :as mount]
            [taoensso.timbre :as timbre]
            [plumbing.core :refer :all]
            [backend.server])
  (:gen-class))

(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (timbre/info component "stopped"))
  (shutdown-agents))

(defn start-app []
  (doseq [component (-> (mount/start) :started)]
    (timbre/info component "started"))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& args]
  (start-app))

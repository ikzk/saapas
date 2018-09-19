(set-env! :dependencies '[[seancorfield/boot-tools-deps "0.4.5" :scope "test"]])
(require '[boot-tools-deps.core :refer [deps]])
(deps :aliases [:test] :quick-merge true :verbose 2)

(require
 '[taoensso.timbre :as timbre]
 '[adzerk.boot-cljs       :refer [cljs]]
 '[adzerk.boot-cljs-repl  :refer [start-repl]]
 '[adzerk.boot-reload     :refer [reload]]
 '[metosin.bat-test       :refer [bat-test]]
 '[metosin.boot-deps-size :refer [deps-size]]
 '[deraen.boot-sass       :refer [sass]]
 '[crisptrutski.boot-cljs-test :refer [test-cljs]])

(task-options!
  pom {:project 'saapas
       :version "0.1.0-SNAPSHOT"
       :description "Application template for Cljs/Om with live reloading, using Boot."
       :license {"The MIT License (MIT)" "http://opensource.org/licenses/mit-license.php"}}
  aot {:namespace #{'backend.main}}
  jar {:main 'backend.main}
  sass {:source-map true})

(deftask start-server
  "Runs the project without building class files. This does not pause execution."
  []
  (require 'backend.main)
  (let [start-app (resolve 'backend.main/start-app)]
    (with-pass-thru _
      (start-app))))


(deftask dev
  "Start the dev env..."
  [s speak           bool "Notify when build is done"]
  (System/setProperty "conf" "resources/config-dev.edn")
  (timbre/info "boot env:" (get-env))
  (comp
    (watch)
    (reload :open-file "emacsclient -n +%s:%s %s"
            ;; Only inject reloading into these builds (= .cljs.edn files)
            :ids #{"js/main"})
    (sass)
    ; This starts a repl server with piggieback middleware
    (adzerk.boot-cljs-repl/cljs-repl :ids #{"js/main"})
    (cljs :ids #{"js/main"})
    ;; Remove cljs output from classpath but keep with in fileset with output role
    ;; don't do this, as compojure (resources) function needs to have these files on classpath
    ;; (sift :to-asset #{#"^js/.*" #"^css/.*"})
    ;; Write the resources to filesystem for dev server
    (target :dir #{"dev-output"})
    (show :fileset true)
    (start-server)
    (if speak (boot.task.built-in/speak) identity)))

;; WARNING: test already refers to: #'clojure.core/test in namespace: boot.user, being replaced by: #'boot.user/testTesting:
;; (ns-unmap *ns* 'test)

(deftask test
  []
  (comp
   (bat-test)
   (test-cljs :js-env :node)))

(deftask autotest []
  (comp
    (watch)
    (test)))

(deftask package
  "Build the package"
  []
  (comp
    (sass :compression true)
    (cljs :optimizations :advanced
          :compiler-options {:preloads nil})
    (aot)
    (pom)
    (uber)
    (jar :file "saapas.jar")
    (sift :include #{#".*\.jar"})
    (target)))

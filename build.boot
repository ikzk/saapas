(set-env!
 :dependencies '[[seancorfield/boot-tools-deps "0.4.5" :scope "test"]])
(require '[boot-tools-deps.core :refer [deps]])
(deps :aliases [:test] :overwrite-boot-deps true :verbose 2)

(require
  '[adzerk.boot-cljs       :refer [cljs]]
  '[adzerk.boot-cljs-repl  :refer [start-repl]]
  '[adzerk.boot-reload     :refer [reload]]
  '[metosin.bat-test       :refer [bat-test]]
  '[metosin.boot-deps-size :refer [deps-size]]
  '[deraen.boot-less       :refer [less]]
  '[crisptrutski.boot-cljs-test :refer [test-cljs]]
  '[backend.boot           :refer [start-app]]
  '[reloaded.repl          :refer [go reset start stop system]])

(task-options!
  pom {:project 'saapas
       :version "0.1.0-SNAPSHOT"
       :description "Application template for Cljs/Om with live reloading, using Boot."
       :license {"The MIT License (MIT)" "http://opensource.org/licenses/mit-license.php"}}
  aot {:namespace #{'backend.main}}
  jar {:main 'backend.main}
  less {:source-map true})

(deftask dev
  "Start the dev env..."
  [s speak           bool "Notify when build is done"
   p port       PORT int  "Port for web server"
   t test-cljs       bool "Compile and run cljs tests"]
  (comp
    (watch)
    (reload :open-file "emacsclient -n +%s:%s %s"
            ;; Only inject reloading into these builds (= .cljs.edn files)
            :ids #{"js/main"})
    (less)
    ; This starts a repl server with piggieback middleware
    (adzerk.boot-cljs-repl/cljs-repl :ids #{"js/main"})
    (cljs :ids #{"js/main"})
    ;; Remove cljs output from classpath but keep with in fileset with output role
    (sift :to-asset #{#"^js/.*"})
    ;; Write the resources to filesystem for dev server
    (target :dir #{"dev-output"})
    (start-app :port port)
    (if speak (boot.task.built-in/speak) identity)))

;; WARNING: test already refers to: #'clojure.core/test in namespace: boot.user, being replaced by: #'boot.user/testTesting:
;; (ns-unmap *ns* 'test)

(deftask test
  []
  (comp
   (bat-test)
   ;; FIXME: This is not a good place to define which namespaces to test
   (test-cljs :namespaces #{"frontend.core-test"})))

(deftask autotest []
  (comp
    (watch)
    (test)))

(deftask package
  "Build the package"
  []
  (comp
    (less :compression true)
    (cljs :optimizations :advanced
          :compiler-options {:preloads nil})
    (aot)
    (pom)
    (uber)
    (jar :file "saapas.jar")
    (sift :include #{#".*\.jar"})
    (target)))

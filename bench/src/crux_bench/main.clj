(ns crux-bench.main
  (:gen-class)
  (:require
   [clojure.tools.logging :as log]
   [amazonica.aws.s3 :as s3]
   [clojure.pprint :as pp]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [crux-bench.watdiv.main :as wat-main]
   [crux-bench.api :as bench-api]
   [crux-bench.config :as conf]
   [crux.api :as api]
   [crux.io :as crux-io]
   [taoensso.timbre :as tim]
   [yada.yada :refer [listener]])
  (:import crux.api.IndexVersionOutOfSyncException
           java.io.Closeable))

;; getApproximateMemTableStats


(defrecord BenchMarkRunner [status crux-node]
  Closeable
  (close [_]
    (when-let [watdiv-runner (:watdiv-runner @status)]
      (.close ^Closeable watdiv-runner))))

(defn ^BenchMarkRunner bench-mark-runner [crux-node]
  (map->BenchMarkRunner
   {:crux-node crux-node
    :status (atom {:running? false})}))

(defn run-node
  [{:keys [server-port] :as options} with-node-fn]
  (with-open [crux-node (case (System/getenv "CRUX_MODE")
                          "CLUSTER_NODE" (api/start-cluster-node options)
                          (api/start-standalone-system options))

              benchmark-runner (bench-mark-runner crux-node)

              http-server
              (let [l (listener
                       (bench-api/application-resource
                        {:crux crux-node
                         :benchmark-runner benchmark-runner})
                       {:port server-port})]
                (log/info "started webserver on port:" server-port)
                (reify Closeable
                  (close [_]
                    ((:close l)))))]
    (with-node-fn
      {:crux crux-node
       :benchmark-runner benchmark-runner})))


(defn- status-logger [node]
  (while true
    (Thread/sleep 3000)
    (log/info
     (with-out-str
       (pp/pprint {:max-memory (.maxMemory (Runtime/getRuntime))
                   :total-memory (.totalMemory (Runtime/getRuntime))
                   :free-memory (.freeMemory (Runtime/getRuntime))})))
    (log/info
     (with-out-str
       (pp/pprint (some-> node :benchmark-runner :status deref))))))


(defn -main []
  (log/info "bench runner starting")
  (try
    (run-node conf/crux-options status-logger)
    (catch IndexVersionOutOfSyncException e
      (crux-io/delete-dir conf/index-dir)
      (-main)))
  (log/info "bench runner exiting"))

(comment
  (def s (future
           (try
             (run-node
              crux-options
              (fn [c]
                (def crux c)
                (Thread/sleep Long/MAX_VALUE)))
             (catch Exception e
               (println e)
               (throw e)))))
  (future-cancel s))

(comment
  (let [crux-node (api/start-standalone-system conf/crux-options)
        _ (tim/debug "crux node:" crux-node)
        bench-runner (bench-mark-runner crux-node)
        node {:crux crux-node :benchmark-runner bench-runner}]
    (wat-main/start-and-run :crux node 4 4))
  )

(ns crux-bench.watdiv.datomic
  (:require
   [clojure.tools.logging :as log]
   [crux-bench.watdiv.base :refer :all]
   [crux.rdf :as rdf]
   [clojure.java.io :as io]
   [datomic.api :as d]
   [crux-bench.config :as conf]
   [crux-bench.watdiv.datomic-schema :refer :all]
   )
  (:import
   java.util.Date)
  )

(defn entity->datomic [e]
  (let [id (:crux.db/id e)
        tx-op-fn (fn tx-op-fn [k v]
                   (if (set? v)
                     (vec (mapcat #(tx-op-fn k %) v))
                     [[:db/add id k v]]))]
    (->> (for [[k v] (dissoc e :crux.db/id)]
           (tx-op-fn k v))
         (apply concat)
         (vec))))

(defn load-rdf-into-datomic [conn resource]
  (with-open [in (io/input-stream (io/resource resource))]
    (->> (rdf/ntriples-seq in)
         (rdf/statements->maps)
         (map #(rdf/use-default-language % rdf/*default-language*))
         (partition-all conf/datomic-tx-size)
         (reduce (fn [^long n entities]
                   (let [done? (atom false)]
                     (while (not @done?)
                       (try
                         (when (zero? (long (mod n rdf/*ntriples-log-size*)))
                           (log/debug "submitted" n))
                         @(d/transact conn (mapcat entity->idents entities))
                         @(d/transact conn (->> (map entity->datomic entities)
                                                (apply concat)
                                                (vec)))
                         (reset! done? true)
                         (catch Exception e
                           (println (ex-data e))
                           (println (ex-data (.getCause e)))
                           (println "retry again to submit!")
                           (Thread/sleep 10000))))
                     (+ n (count entities))))
                 0))))


(defrecord DatomicBackend [conn]
  WatdivBackend
  (backend-info [this]
    {:backend :datomic})
  (execute-with-timeout [this datalog]
    (d/query {:query datalog
              :timeout conf/query-timeout-ms
              :args [(d/db conn)]}))
  (ingest-watdiv-data [this resource]
    (when-not (d/entity (d/db conn) [:watdiv/ingest-state :global])
      (log/info "starting to ingest watdiv data into datomic")
      (let [time-before (Date.)]
        (load-rdf-into-datomic conn resource)
        (let [ingest-time (- (.getTime (Date.)) (.getTime time-before))]
          (log/infof "completed datomic watdiv ingestion time taken: %s" ingest-time)
          @(d/transact conn [{:watdiv/ingest-state :global
                              :watdiv/ingest-time ingest-time}]))))))

(defmethod start-watdiv-runner :datomic
  [_ node]
  (let [uri (str "datomic:free://"
                 (or (System/getenv "DATOMIC_TRANSACTOR_URI") "datomic")
                 ":4334/bench?password=password")
        _ (d/create-database uri)
        conn (d/connect uri)]
    @(d/transact conn datomic-watdiv-schema)
    (map->DatomicBackend {:conn conn})))

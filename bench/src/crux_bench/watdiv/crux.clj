(ns crux-bench.watdiv.crux
  (:require
   [clojure.java.io :as io]
   [crux.rdf :as rdf]
   [crux.api :as api]
   [crux-bench.config :as conf]
   [crux-bench.watdiv.base :refer :all])
  (:import
   java.util.Date))

(defrecord CruxBackend [crux]
  WatdivBackend
  (backend-info [this]
    (let [ingest-stats (api/entity (api/db crux) ::watdiv-ingestion-status)]
      (merge
       {:backend :crux}
       (select-keys ingest-stats [:watdiv/ingest-start-time
                                  :watdiv/kafka-ingest-time
                                  :watdiv/ingest-time])
       (select-keys (api/status crux) [:crux.version/version
                                        :crux.version/revision
                                        :crux.kv/kv-backend]))))

  (execute-with-timeout [this datalog]
    (let [db (api/db crux)]
      (with-open [snapshot (api/new-snapshot db)]
        (let [query-future (future (count (api/q db snapshot datalog)))]
          (or (deref query-future conf/query-timeout-ms nil)
              (do (future-cancel query-future)
                  (throw (IllegalStateException. "Query timed out."))))))))

  (ingest-watdiv-data [this resource]
    (when-not (:done? (api/entity (api/db crux) ::watdiv-ingestion-status))
      (let [time-before (Date.)
            submit-future (future
                            (with-open [in (io/input-stream (io/resource resource))]
                              (rdf/submit-ntriples (:tx-log crux) in 1000)))]
        (assert (= 521585 @submit-future))
        (let [kafka-ingest-done (Date.)
              {:keys [crux.tx/tx-time]}
              (api/submit-tx
               crux
               [[:crux.tx/put
                 {:crux.db/id ::watdiv-ingestion-status :done? false}]])]
          (api/db crux tx-time tx-time) ;; block until indexed
          (api/db
           crux (Date.)
           (:crux.tx/tx-time
            (api/submit-tx
             crux
             [[:crux.tx/put
               {:crux.db/id ::watdiv-ingestion-status
                :watdiv/ingest-start-time time-before
                :watdiv/kafka-ingest-time (- (.getTime kafka-ingest-done) (.getTime time-before))
                :watdiv/ingest-time (- (.getTime (Date.)) (.getTime time-before))
                :done? true}]]))))))))

(defmethod start-watdiv-runner :crux
  [_ {:keys [crux]}]
  (map->CruxBackend {:crux crux}))

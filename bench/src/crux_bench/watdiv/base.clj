(ns crux-bench.watdiv.base
  (:require
   [crux.index :as idx])
  (:import java.io.Closeable))

(defprotocol WatdivBackend
  (backend-info [this])
  (execute-with-timeout [this datalog])
  (ingest-watdiv-data [this resource]))

(defrecord WatdivRunner [running-future]
  Closeable
  (close [_]
    (future-cancel running-future)))

(defmulti start-watdiv-runner
  (fn [key node] key))

(defn entity->idents [e]
  (cons
   {:db/ident (:crux.db/id e)}
   (for [[_ v] e
         v (idx/normalize-value v)
         :when (keyword? v)]
     {:db/ident v})))

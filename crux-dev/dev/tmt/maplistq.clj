(ns tmt.maplistq
  (:require [crux.api :as api]))

(def opts
  {:crux.node/topology :crux.standalone/topology
   :crux.node/kv-store "crux.kv.memdb/kv"
   :crux.kv/db-dir "data/db-dir-1"
   :crux.standalone/event-log-dir "data/eventlog-1"
   :crux.standalone/event-log-kv-store "crux.kv.memdb/kv"})

(def node
  (api/start-node opts))

(api/submit-tx
  node
  [[:crux.tx/put
    {:crux.db/id :me
     :list ["carrots" "peas" "shampoo"]
     :pockets/left ["lint" "change"]
     :pockets/right ["phone"]}]
   [:crux.tx/put
    {:crux.db/id :you
     :list ["carrots" "tomatoes" "wig"]
     :pockets/left ["wallet" "watch"]
     :pockets/right ["spectacles"]}]])

(api/q (api/db node) '{:find [e l]
                       :where [[e :list l]]
                       :args [{l "carrots"}]})
;; => #{[:you "carrots"] [:me "carrots"]}

(api/q (api/db node) '{:find [e p]
                       :where [[e :pockets/left p]]
                       :args [{p "watch"}]})
;; => #{[:you "watch"]}

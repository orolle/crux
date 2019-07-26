(ns crux-bench.view
  (:require
   [amazonica.aws.s3 :as s3]
   [clojure.pprint :as pp]
   [hiccup2.core :refer [html]]
   [crux-bench.watdiv :as watdiv]
   [clojure.java.shell :refer [sh]])
  (:import org.rocksdb.RocksDB))

(defn- body-wrapper
  [content]
  (str
   "<!DOCTYPE html>"
   (html
    [:html {:lang "en"}
     [:head
      [:title "Crux BenchMarker"]
      [:meta {:charset "utf-8"}]
      [:meta {:http-equiv "Content-Language" :content "en"}]
      [:meta {:name "google" :content "notranslate"}]
      [:link {:rel "stylesheet" :type "text/css" :href "/static/styles/normalize.css"}]
      [:link {:rel "stylesheet" :type "text/css" :href "/static/styles/main.css"}]]
     [:body
      [:div.content content]]])))

(defn- previus-run-reports
  []
  [:div.previus-benchmarks
   [:h2 "Previous run reports"]
   (for [obj (:object-summaries
              (s3/list-objects-v2
               :bucket-name (System/getenv "CRUX_BENCHMARK_BUCKET")))]
     [:div
      [:a {:href (s3/get-url (System/getenv "CRUX_BENCHMARK_BUCKET") (:key obj))}
       (:key obj)]])])

(defn index-handler
  [ctx node]
  (body-wrapper
   [:div
    [:h1 "Benchmark runner"]
    (previus-run-reports)]))

(defn admin-handler
  [ctx node]
  (body-wrapper
    [:div
     [:header
      [:h2 [:a {:href "/"} "Bench Mark runner"]]
      [:pre
       (with-out-str
         (pp/pprint
           (into
             {}
             (for [p ["rocksdb.estimate-table-readers-mem"
                      "rocksdb.size-all-mem-tables"
                      "rocksdb.cur-size-all-mem-tables"
                      "rocksdb.estimate-num-keys"]]
               (let [^RocksDB db (-> node :crux :kv-store :kv :db)]
                 [p (-> db (.getProperty (.getDefaultColumnFamily db) p))])))))]
      [:pre
       (with-out-str
         (pp/pprint (.status ^crux.api.ICruxAPI (:crux node))))]

      [:pre
       (with-out-str
         (pp/pprint {:max-memory (.maxMemory (Runtime/getRuntime))
                     :total-memory (.totalMemory (Runtime/getRuntime))
                     :free-memory (.freeMemory (Runtime/getRuntime))}))]

      [:pre
       (slurp
         (java.io.FileReader.
           (format "/proc/%s/status" (.pid (java.lang.ProcessHandle/current)))))]

      [:pre
       (with-out-str
         (pp/pprint (-> node :benchmark-runner :status deref)))]

      [:div.buttons
       [:form {:action "/start-bench" :method "POST"}
        [:div
         [:label "Test Count: (default 100)"]
         [:input {:type "input" :name "test-count"}]]
        [:div
         [:label "Thread Count: (default 1)"]
         [:input {:type "input" :name "thread-count"}]]
        [:div
         [:label "Backend"]
         [:select {:name "backend"}
          (for [backend watdiv/supported-backends]
            [:option {:value backend} backend])]]
        [:input {:value "Run!" :type "submit"}]]

       [:form {:action "/stop-bench" :method "POST"}
        [:input {:value "Stop!" :name "run" :type "submit"}]]]]

     [:hr]
     [:div.status-content
      [:h3 "Status"]
      [:pre
       (when-let [f (-> node :benchmark-runner :status deref
                        :watdiv-runner :out-file)]
         (:out (sh "tail" "-40" (.getPath ^java.io.File f))))]]

     (previus-run-reports)]))


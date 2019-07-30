(ns crux-bench.watdiv.main
  (:require [amazonica.aws.s3 :as s3]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [crux.api :as api]
            [crux.rdf :as rdf]
            [crux.sparql :as sparql]
            [crux-bench.config :as conf]
            [crux-bench.watdiv.base :refer :all]
            [crux-bench.watdiv.crux :refer :all]
            [crux-bench.watdiv.datomic :refer :all])
  (:import com.amazonaws.services.s3.model.CannedAccessControlList
           [java.io Closeable File]
           java.time.Duration
           java.util.Date))

;; TODO name the resulting file based on what test was run!
;;      and how many tests that were run!
(defn upload-watdiv-results
  [^File out-file]
  (s3/put-object
   :bucket-name (System/getenv "CRUX_BENCHMARK_BUCKET")
   :key (.getName out-file)
   :acl :public-read
   :file out-file)
  (s3/set-object-acl
   (System/getenv "CRUX_BENCHMARK_BUCKET")
   (.getName out-file)
   CannedAccessControlList/PublicRead))

(defn- write-result-thread [out-file backend num-tests num-threads all-jobs-completed completed-queue
                            all-jobs-submitted]
  (with-open [out (io/writer out-file)]
    (.write out "{\n")
    (.write out (str ":test-time " (pr-str (System/currentTimeMillis)) "\n"))
    (.write out (str ":backend-info " (pr-str (backend-info backend)) "\n"))
    (.write out (str ":num-tests " (pr-str num-tests) "\n"))
    (.write out (str ":num-threads " (pr-str num-threads) "\n"))
    (.write out (str ":tests " "\n"))
    (.write out "[\n")
    (loop []
      (when-let [{:keys [idx q error results time]} (if @all-jobs-completed
                                                      (.poll completed-queue)
                                                      (.take completed-queue))]
        (.write out "{")
        (.write out (str ":idx " (pr-str idx) "\n"))
        (.write out (str ":query " (pr-str q) "\n"))
        (if error
          (.write out (str ":error " (pr-str (str error)) "\n"))
          (.write out (str ":backend-results " results "\n")))
        (.write out (str ":time " (pr-str time)))
        (.write out "}\n")
        (.flush out)
        (recur)))
    (.write out "]}")))

(defn- run-jobs [all-jobs-submitted job-queue backend completed-queue]
  (when-let [{:keys [idx q] :as job} (if @all-jobs-submitted
                                       (.poll job-queue)
                                       (.take job-queue))]
    (let [start-time (System/currentTimeMillis)
          result
          (try
            {:results (execute-with-timeout backend (sparql/sparql->datalog q))}
            (catch java.util.concurrent.TimeoutException t
              {:error t})
            (catch IllegalStateException t
              {:error t})
            (catch Throwable t
              (log/error t "unkown error running watdiv tests")
              ;; datomic wrapps the error multiple times
              ;; doing this to get the cause exception!
              (when-not (instance? java.util.concurrent.TimeoutException
                                   (.getCause (.getCause (.getCause t))))
                (throw t))))]
      (.put completed-queue
            (merge
             job result
             {:time (- (System/currentTimeMillis) start-time)}))
      (recur all-jobs-submitted job-queue backend completed-queue))))

(defn execute-stress-test
  [backend tests-run out-file num-tests ^Long num-threads]
  (let [all-jobs-submitted (atom false)
        all-jobs-completed (atom false)
        pool (java.util.concurrent.Executors/newFixedThreadPool (inc num-threads))
        job-queue (java.util.concurrent.LinkedBlockingQueue. num-threads)
        completed-queue (java.util.concurrent.LinkedBlockingQueue. ^Long (* 10 num-threads))
        writer-future (.submit pool (partial write-result-thread
                                             out-file backend num-tests num-threads all-jobs-completed
                                             completed-queue all-jobs-submitted))
        job-features (vec (for [i (range num-threads)]
                            (.submit pool (partial run-jobs
                                                   all-jobs-submitted job-queue backend completed-queue))))]
    (try
      (with-open [desc-in (io/reader (io/resource "watdiv/data/watdiv-stress-100/test.1.desc"))
                  sparql-in (io/reader (io/resource "watdiv/data/watdiv-stress-100/test.1.sparql"))]
        (doseq [[idx [d q]] (->> (map vector (line-seq desc-in) (line-seq sparql-in))
                                 (take (or num-tests 100))
                                 (map-indexed vector))]
          (.put job-queue {:idx idx :q q})))
      (reset! all-jobs-submitted true)
      (doseq [^java.util.concurrent.Future f job-features] (.get f))
      (reset! all-jobs-completed true)
      (.get writer-future)

      (catch InterruptedException e
        (.shutdownNow pool)
        (throw e)))))

(defn run-watdiv-test [backend num-tests num-threads]
  (let [status (atom nil)
        tests-run (atom 0)
        out-file (io/file (format "watdiv_%s.edn" (System/currentTimeMillis)))]
    (map->WatdivRunner
     {:status status
      :tests-run tests-run
      :out-file out-file
      :num-tests num-tests
      :num-threads num-threads
      :backend backend
      :running-future
      (future
        (try
          (reset! status :ingesting-watdiv-data)
          (ingest-watdiv-data backend "watdiv/data/watdiv.10M.nt")
          (reset! status :running-benchmark)
          (execute-stress-test backend tests-run out-file num-tests num-threads)
          (reset! status :uploading-results)
          (upload-watdiv-results out-file)
          (reset! status :benchmark-completed)
          (catch Throwable t
            (log/error t "watdiv testrun failed")
            (reset! status :benchmark-failed)
            false)))})))

(defn start-and-run [backend-name node num-tests num-threads]
  (let [backend (start-watdiv-runner backend-name node)]
    (run-watdiv-test backend num-tests num-threads)))



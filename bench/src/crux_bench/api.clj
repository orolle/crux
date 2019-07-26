(ns crux-bench.api
  (:require
   [yada.resource :refer [resource]]
   [crux-bench.view :as view]
   [buddy.hashers :as hashers]
   [crux-bench.config :as conf]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [crux-bench.watdiv :as watdiv]
   [yada.resources.classpath-resource])
  (:import java.io.Closeable))

(defn application-resource
  [{:keys [benchmark-runner] :as node}]
  ["/"
   [[""
     (resource
      {:methods
       {:get {:produces "text/html"
              :response #(view/index-handler % node)}}})]

    ["admin"
     (resource
      {:access-control
       {:authentication-schemes
        [{:scheme "Basic"
          :verify (fn [[user password]]
                    :bcrypt+blake2b-512
                    (when (and (= user "admin")
                               (hashers/check password conf/secret-password))
                      {:roles #{:admin-user}}))}]

        :authorization
        {:methods {:get :admin-user}}}

       :methods
       {:get {:produces "text/html"
              :response #(view/admin-handler % node)}}})]

    ["start-bench"
     (resource
      {:methods
       {:post {:consumes "application/x-www-form-urlencoded"
               :produces "text/html"
               :parameters {:form {:test-count String
                                   :thread-count String
                                   :backend String}}
               :response
               (fn [ctx]
                 (let [num-tests (let [t (some-> ctx :parameters :form :test-count)]
                                   (if (str/blank? t)
                                     100
                                     (Integer/parseInt t)))
                       num-threads (let [t (some-> ctx :parameters :form :thread-count)]
                                     (if (str/blank? t)
                                       1
                                       (Integer/parseInt t)))
                       backend (some-> ctx :parameters :form :backend keyword)]
                   (log/info "starting benchmark tests")
                   (swap!
                    (:status benchmark-runner)
                    merge
                    {:running? true
                     :watdiv-runner
                     (watdiv/start-and-run backend node num-tests num-threads)})
                   (assoc (:response ctx)
                          :status 302
                          :headers {"location" "/"})))}}})]

    ["stop-bench"
     (resource
      {:methods
       {:post {:consumes "application/x-www-form-urlencoded"
               :produces "text/html"
               :response
               (fn [ctx]
                 (log/info "stopping benchmark tests")
                 (when-let [watdiv-runner (:watdiv-runner @(:status benchmark-runner))]
                   (.close ^Closeable watdiv-runner))
                 (reset! (:status benchmark-runner) {:running? false})
                 (assoc (:response ctx)
                        :status 302
                        :headers {"location" "/"}))}}})]

    ["static"
     (yada.resources.classpath-resource/new-classpath-resource
      "static")]]])

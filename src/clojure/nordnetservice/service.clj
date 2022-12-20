(ns nordnetservice.service
  (:gen-class)
  (:require
   [nordnetservice.common :refer [default-json-response req-oid]]
   [nordnetservice.config  :refer [get-context]]
   [nordnetservice.core :as core]
   [io.pedestal.http :as http]
   [io.pedestal.http.route :as route]))

(def env :demo)

(def ctx (get-context env))


(def stockoptions
  (default-json-response ::stockoptions 200 true
                         (fn [req]
                           (let [oid (req-oid req)]
                             (core/stock-options ctx oid true)))))

(def calls
  (default-json-response ::calls 200 true
                         (fn [req]
                           (let [oid (req-oid req)]
                             (core/calls ctx oid true)))))

(def nocache-calls
  (default-json-response ::nocache-calls 200 true
                         (fn [req]
                           (let [oid (req-oid req)]
                             (core/calls ctx oid false)))))

(def puts
  (default-json-response ::puts 200 true
                         (fn [req]
                           (let [oid (req-oid req)]
                             (core/puts ctx oid true)))))

(def nocache-puts
  (default-json-response ::nocache-puts 200 true
                         (fn [req]
                           (let [oid (req-oid req)]
                             (core/puts ctx oid false)))))

(def find-option
  (default-json-response ::find-option 200 true
                         (fn [req]
                           (let [oid (req-oid req)]
                             (core/find-option ctx oid true)))))

(def nocache-find-option
  (default-json-response ::nocache-find-option 200 true
                         (fn [req]
                           (let [oid (req-oid req)]
                             (core/find-option ctx oid false)))))

(def routes
  (route/expand-routes
   #{["/stockoptions/:oid" :get stockoptions]
     ["/nocache/calls/:oid" :get nocache-calls]
     ["/nocache/puts/:oid" :get nocache-puts]
     ["/nocache/option/:ticker" :get nocache-find-option]
     ["/calls/:oid" :get calls]
     ["/puts/:oid" :get puts]
     ["/option/:ticker" :get find-option]}))

;; Map-based routes
;(def routes `{"/" {:interceptors [(body-params/body-params) http/html-body]
;                   :get home-page
;                   "/about" {:get about-page}}})

;; Terse/Vector-based routes
;(def routes
;  `[[["/" {:get home-page}
;      ^:interceptors [(body-params/body-params) http/html-body]
;      ["/about" {:get about-page}]]]])


;; Consumed by nordnet-service.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env env
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::http/interceptors []
              ::http/routes routes

              ::http/secure-headers {:content-security-policy-settings {:object-src "'none'"}}
              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::http/allowed-origins ["scheme://host:port"]

              ;; Tune the Secure Headers
              ;; and specifically the Content Security Policy appropriate to your service/application
              ;; For more information, see: https://content-security-policy.com/
              ;;   See also: https://github.com/pedestal/pedestal/issues/499
              ;;::http/secure-headers {:content-security-policy-settings {:object-src "'none'"
              ;;                                                          :script-src "'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:"
              ;;                                                          :frame-ancestors "'none'"}}

              ;; Root for resource interceptor that is available by default.
              ::http/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ;;  This can also be your own chain provider/server-fn -- http://pedestal.io/reference/architecture-overview#_chain_provider
              ::http/type :jetty
              ;;::http/host "localhost"
              ::http/port 8082
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2? false
                                        ;:keystore "test/hp/keystore.jks"
                                        ;:key-password "password"
                                        ;:ssl-port 8443
                                        :ssl? false
                                        ;; Alternatively, You can specify your own Jetty HTTPConfiguration
                                        ;; via the `:io.pedestal.http.jetty/http-configuration` container option.
                                        ;:io.pedestal.http.jetty/http-configuration (org.eclipse.jetty.server.HttpConfiguration.)
                                        }})

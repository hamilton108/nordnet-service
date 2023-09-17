(ns nordnetservice.server
  (:gen-class) ; for -main method in uberjar
  (:require [io.pedestal.http :as server]
            [nordnetservice.service :as service]))

;; This is an adapted service map, that can be started and stopped
;; From the REPL you can call server/start and server/stop on this service
(defonce runnable-service (server/create-server service/service))

;; (defn run-dev
;;   "The entry-point for 'lein run-dev'"
;;   [& args]
;;   (println "\nCreating your [DEV] server...")
;;   (-> service/service ;; start with production configuration
;;       (merge {:env :dev
;;               ;; do not block thread that starts web server
;;               ::server/join? false
;;               ;; Routes can be a function that resolve routes,
;;               ;;  we can use this to set the routes to be reloadable
;;               ::server/routes #(route/expand-routes (deref #'service/routes))
;;               ;; all origins are allowed in dev mode
;;               ::server/allowed-origins {:creds true :allowed-origins (constantly true)}
;;               ;; Content Security Policy (CSP) is mostly turned off in dev mode
;;               ::server/secure-headers {:content-security-policy-settings {:object-src "'none'"}}})
;;       ;; Wire up interceptor chains
;;       server/default-interceptors
;;       server/dev-interceptors
;;       server/create-server
;;       server/start))

(defn parse-args [args]
  (if (= (ount args) 0)
    {:profile "demo" }
    ))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (let [args0 (first args)
       profile (keyword (if (= args0 nil) "demo" args0))]
    (println "\nPROFILE: " profile)))
    ;(service/init-profile profile)
    ;(server/start runnable-service)))

;; Another option is to store the call to the constructor as an anonymous function. In our case:
;; (def a #(String. %1))
;; (a "111"); "111"

;; The most elegant solution is to write construct that does the same as new but is able to receive a class dynamically:
;; This solution overcomes the limitation of @mikera's answer (see comments).
;;  (defn construct [klass & args]
;;     (clojure.lang.Reflector/invokeConstructor klass (into-array Object args)))
;;  (def a HashSet)
;;  (construct a '(1 2 3)); It works!!!

;; Mikera's answer:
;; (defn construct [klass & args]
;;   (.newInstance
;;     (.getConstructor klass (into-array java.lang.Class (map type args)))
;;     (object-array args)))

;; (construct a "Foobar!")

;; If you package the service up as a WAR,
;; some form of the following function sections is required (for io.pedestal.servlet.ClojureVarServlet).

;;(defonce servlet  (atom nil))
;;
;;(defn servlet-init
;;  [_ config]
;;  ;; Initialize your app here.
;;  (reset! servlet  (server/servlet-init service/service nil)))
;;
;;(defn servlet-service
;;  [_ request response]
;;  (server/servlet-service @servlet request response))
;;
;;(defn servlet-destroy
;;  [_]
;;  (server/servlet-destroy @servlet)
;;  (reset! servlet nil))


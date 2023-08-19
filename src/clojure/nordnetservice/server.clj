(ns nordnetservice.server
  (:gen-class) ; for -main method in uberjar
  (:require [io.pedestal.http :as server]
            [clojure.java.io :as io]
            [nordnetservice.service :as service]
            [nordnetservice.common :refer [load-properties]]))

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

;;(with-open [in (io/input-stream (io/resource "file.dat"))] ;; resources/file.dat
;;   (io/copy in (io/file "/path/to/extract/file.dat"))))

(defn property-file-name [profile] 
  (let [fname 
        (if (= profile nil) 
          "application.properties"
          (str "application-" profile ".properties"))]
    fname))
    
    
(defn read-property-file [profile]
  (let [fname (property-file-name profile)
        url (io/resource fname)]
    (load-properties url)))

(defn read-properties [args]
  (let [profile (first args)
        defaults (read-property-file nil)]
    (if (= profile nil)
      defaults
      (let [profile-props (read-property-file profile)]
        (merge defaults profile-props)))))


(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server...")
  (prn (read-properties args)))

  
  ;(server/start runnable-service))

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



;; import java.io.BufferedInputStream;
;; import java.io.InputStream;
;; import java.io.InputStreamReader;
;; import java.io.Reader;
;; import java.net.URL;

;; public class MainClass {

;;   public static void main(String[] args) throws Exception {

;;     URL u = new URL("http://www.java2s.com");
;;     InputStream in = u.openStream();

;;     in = new BufferedInputStream(in);

;;     Reader r = new InputStreamReader(in);
;;     int c;
;;     while ((c = r.read()) != -1) {
;;       System.out.print((char) c);
;;     }
;;   }

;; }
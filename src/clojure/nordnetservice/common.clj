(ns nordnetservice.common
  (:gen-class)
  (:import
   (com.fasterxml.jackson.databind ObjectMapper))
  (:require
   [cheshire.core :as json]))

(def oid->string
  {3 "YAR"})

(defn rs [v]
  (if (string? v)
    (let [vs (if-let [v (re-seq #"(\d+),(\d+)" v)]
               (let [[a b c] (first v)] (str b "." c))
               v)]
      (read-string vs))
    v))

(defn req-oid [request]
  (let [oid (get-in request [:path-params :oid])]
    (rs oid)))

(def not-nil? (comp not nil?))

(def om (ObjectMapper.))

(defn om->json [bean]
  (let [data (.writeValueAsString om bean)]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body data}))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})

(defn json-req-parse [req]
  (let
   [r (slurp (:body req))]
    (json/parse-string r true)))

(defn default-json-response
  [route-name
   http-status
   req-fn
   om-json
   has-body]
   ;& {:keys [om-json has-body] :or [om-json false has-body false]}]
  {:name route-name
   :enter
   (fn [context]
     context)
   :error
   (fn [context ex-info]
     (assoc context
            :response {:status 500
                       :body (.getMessage ex-info)}))})

;; (defn default-json-response
;;   [route-name
;;    http-status
;;    req-fn
;;    & {:keys [om-json has-body] :or [om-json false has-body false]}]
;;   {:name route-name
;;    :enter
;;    (fn [context]
;;      (let [req (:request context)
;;            result (if (= has-body true)
;;                     (let [body (json-req-parse req)]
;;                       (req-fn body req))
;;                     (req-fn  req))
;;            response (if (= om-json true)
;;                       (om->json result)
;;                       (json-response result http-status))]
;;        (assoc context :response response)))
;;    :error
;;    (fn [context ex-info]
;;      (assoc context
;;             :response {:status 500
;;                        :body (.getMessage ex-info)}))})
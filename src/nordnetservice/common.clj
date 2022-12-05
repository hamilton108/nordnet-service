(ns nordnetservice.common
  (:gen-class)
  (:import
   (com.fasterxml.jackson.databind ObjectMapper))
  (:require
   [cheshire.core :as json]))

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
   body-fn
   om-json]
  {:name route-name
   :enter
   (fn [context]
     (let [req (:request context)
           body (json-req-parse req)
           result (body-fn body req)
           response (if (= om-json true)
                      (om->json result)
                      (json-response result http-status))]
       (assoc context :response response)))
   :error
   (fn [context ex-info]
     (assoc context
            :response {:status 500
                       :body (.getMessage ex-info)}))})
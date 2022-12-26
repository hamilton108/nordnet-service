(ns nordnetservice.common
  (:gen-class)
  (:import
   (java.net URL)
   (com.fasterxml.jackson.databind ObjectMapper))
  (:require
   [cheshire.core :as json]))


;; 5 | ACY    | Acergy               |      0 |               1
;; 25 | AKERBP | Aker BP              |      0 |               1
;; 18 | AKSO   | Aker Solutions       |      1 |               1
;; 27 | BAKKA  | Bakkafrost           |      1 |               1
;; 26 | BWLPG  | BW LPG               |      1 |               1
;; 19 | DNB    | DNB                  |      1 |               1
;; 10 | DNBNOR | DnB NOR              |      0 |               1
;; 20 | DNO    | DNO International    |      1 |               1
;; 2 | EQNR   | Equinor              |      1 |               1
;; 21 | GJF    | Gjensidige Forsikr   |      1 |               1
;; 28 | GOGL   | Golden Ocean Group   |      1 |               1
;; 8 | MHG    | Marine Harvest       |      0 |               1
;; 22 | NSG    | Norske Skogindustr   |      0 |               1
;; 1 | NHY    | Norsk hydro          |      1 |               1
;; 29 | NAS    | Norw. Air Shuttle    |      1 |               1
;; 9 | ORK    | Orkla                |      1 |               1
;; 24 | OSEBX  | Oslo Børs Benchmark  |      0 |               3
;; 12 | PGS    | Petroleum Geo-Serv   |      1 |               1
;; 11 | REC    | Renewable Energy Cor |      0 |               1
;; 13 | RCL    | Royal Caribbean Crui |      0 |               1
;; 4 | SDRL   | Seadrill             |      0 |               1
;; 14 | STB    | Storebrand           |      1 |               1
;; 23 | SUBC   | Subsea 7             |      1 |               1
;; 15 | TAA    | Tandberg             |      0 |               1
;; 6 | TEL    | Telenor              |      1 |               1
;; 16 | TGS    | TGS-NOPEC Geophysica |      1 |               1
;; 17 | TOM    | Tomra                |      1 |               1
;; 7 | OBX    | Total Return Index   |     -1 |               3
;; 3 | YAR    | Yara                 |      1 |               1


(defn find-first [f coll]
  (first (drop-while (complement f) coll)))

(def oid->string
  {18 "AKSO"
   27 "BAKKA"
   26 "BWLPG"
   19 "DNB"
   20 "DNO"
   2  "EQNR"
   21 "GJF"
   28 "GOGL"
   1  "NHY"
   29 "NAS"
   7  "OBX"
   9  "ORK"
   12 "PGS"
   14 "STB"
   23 "SUBC"
   6  "TEL"
   16 "TGS"
   17 "TOM"
   3  "YAR"})

(def ticker->oid
  {"AKSO" 18
   "BAKKA" 27
   "BWLPG" 26
   "DNB" 19
   "DNO" 20
   "EQNR" 2
   "GJF" 21
   "GOGL" 28
   "NHY" 1
   "NAS" 29
   "OBX" 7
   "ORK" 9
   "PGS" 12
   "STB" 14
   "SUBC" 23
   "TEL" 6
   "TGS" 16
   "TOM" 17
   "YAR" 3})


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

(defn option-ticker [request]
  (get-in request [:path-params :ticker]))

(def not-nil? (comp not nil?))

(def om (ObjectMapper.))

(defn om->json [bean http-status]
  (let [data (.writeValueAsString om bean)]
    {:status http-status
     :headers {"Content-Type" "application/json"}
     :body data}))

(defn json-response [data & [http-status]]
  {:status (or http-status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})

(defn json-req-parse [req]
  (let
   [r (slurp (:body req))]
    (json/parse-string r true)))

;& {:keys [om-json has-body] :or [om-json false has-body false]}]

(defn default-json-response
  [route-name
   http-status
   om-json
   ;has-body
   req-fn]
  {:name route-name
   :enter
   (fn [context]
     (let [req (:request context)
           result (req-fn req)
           response (if (= om-json true)
                      (om->json result http-status)
                      (json-response result http-status))]
       (assoc context :response response)))
   :error
   (fn [context ex-info]
     (assoc context
            :response {:status 500
                       :body (.getMessage ex-info)}))})

(defn url-path-query-for
  [^String ticker
   ^String nordnetUnixTime]
  (str "/market/options?currency=NOK&underlyingSymbol=" ticker "&expireDate=" nordnetUnixTime))

(defn url-for
  "String -> String -> URL"
  [^String ticker
   ^String nordnetUnixTime]
  (URL. "https" "www.nordnet.no" (url-path-query-for ticker nordnetUnixTime)))

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


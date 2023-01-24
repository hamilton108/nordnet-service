(ns nordnetservice.adapter.redisadapter
  (:require
   [nordnetservice.common :refer [not-nil? url-for]]
   [nordnetservice.stockoption :refer [nordnet-millis]]
   [clojure.core.match :refer [match]])
  (:import
   (java.time LocalDate)
   (redis.clients.jedis Jedis)
   (nordnet.financial OpeningPrices)))


(def jedis
  (memoize
   (fn [env]
     (let [j (Jedis. "172.20.1.2" 6379)]
       (match env
         :prod (.select j 0)
         :demo (.select j 4)
         :test (.select j 5))
       j))))

;; (defn url-path-query-for
;;   [^String ticker
;;    ^String nordnetUnixTime]
;;   (str "/market/options?currency=NOK&underlyingSymbol=" ticker "&expireDate=" nordnetUnixTime))

;; (defn url-for
;;   "String -> String -> URL"
;;   [^String ticker
;;    ^String nordnetUnixTime]
;;   (URL. "https" "www.nordnet.no" (url-path-query-for ticker nordnetUnixTime)))

(defn exp-fn
  [^String ticker
   ^long cur-dx
   ^String v]
  (let [x (Long/parseLong v)]
    (if (< x cur-dx)
      nil
      (url-for ticker v))))

(defn url-all
  "ctx -> String -> LocalDate -> [URLInfo]"
  [env ^String ticker ^LocalDate dx]
  (let [^Jedis j (jedis env)
        exp (.smembers j "expiry")
        millis (nordnet-millis dx)]
    (filterv not-nil? (map (partial exp-fn ticker millis) exp))))

(defn test-url [env]
  (let [^Jedis j (jedis env)
        url (if (= env :test) "test-url" "demo-url")]
    (.get j url)))

;(defrecord OpeningPricesImpl [ctx]
;  OpeningPrices

(defn opening-price [env ^String ticker]
  (let [^Jedis j (jedis env)
        op (.hget ^Jedis j "openingprices" ticker)]
    (Double/parseDouble op)))

(defrecord OpeningPricesImpl [ctx]
  OpeningPrices
  (fetchPrice [_ ticker]))
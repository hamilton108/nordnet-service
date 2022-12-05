(ns nordnetservice.adapter.redisadapter
  (:require
   [nordnetservice.common :refer [not-nil?]])
  ;[clojure.core.match :refer [match]])
  (:import
   (java.net URL)
   (redis.clients.jedis Jedis)
   (java.time.chrono ChronoLocalDateTime)
   (java.time
    LocalDate
    ZoneId
    ZoneOffset)
   (nordnet.downloader URLInfo)))

(def EUROPE_OSLO (ZoneId/of "Europe/Oslo"))

(def jedis
  (memoize
   (fn [ctx]
     (let [j (Jedis. "172.20.1.2" 6379)]
       (if (= :prod ctx)
         (.select j 0)
         (.select j 5))
       j))))

(defn url-path-query-for
  [^String ticker
   ^String nordnetUnixTime]
  (str "/market/options?currency=NOK&underlyingSymbol=" ticker "&expireDate=" nordnetUnixTime))

(defn url-for
  "String -> String -> URL"
  [^String ticker
   ^String nordnetUnixTime]
  (URL. "https" "www.nordnet.no" (url-path-query-for ticker nordnetUnixTime)))

(defn nordnet-millis
  "LocalDate -> long"
  [^LocalDate dx]
  (let [tm ^ChronoLocalDateTime (.atStartOfDay dx)
        zone (.atZone tm EUROPE_OSLO)
        ltm (.toLocalDateTime zone)
        inst (.toInstant ltm ZoneOffset/UTC)
        epoch (.getEpochSecond inst)]
    (* epoch 1000)))

(defn exp-fn
  [^String ticker
   ^long cur-dx
   ^String v]
  (let [x (Long/parseLong v)]
    (if (< x cur-dx)
      nil
      (let [u (url-for ticker v)]
        (URLInfo. (.toString u) v)))))

(defn url
  "ctx -> String -> LocalDate -> [URLInfo]"
  [ctx ^String ticker ^LocalDate dx]
  (let [j (jedis ctx)
        exp (.smembers j "expiry")
        millis (nordnet-millis dx)]
    (filterv not-nil? (map (partial exp-fn ticker millis) exp))))

(defn demo-url [ctx]
  (let [j (jedis ctx)]
    (.get j "demo-url")))
(ns nordnetservice.core
  (:gen-class)
  (:require
   [nordnetservice.adapter.nordnetadapter :as nordnet]
   [nordnetservice.adapter.redisadapter :as redis]
   [nordnetservice.stockoption :as option]
   [nordnetservice.config :as config]
   [nordnetservice.common :refer [oid->string find-first unix-time CALL PUT]])
  (:import
   (vega.financial.calculator BlackScholes)
   (oahu.exceptions  BinarySearchException)
   (org.slf4j LoggerFactory)
   (java.time LocalDate LocalTime)
   (java.util.concurrent TimeUnit)
   (com.github.benmanes.caffeine.cache Caffeine)))

(def logger (LoggerFactory/getLogger "nordnetservice.core"))

(def expiry-options (atom {:interval 120 :unit TimeUnit/MINUTES}))
(def expiry-fetch-option (atom {:interval 120 :unit TimeUnit/MINUTES}))

(def caffeine-1 (-> (Caffeine/newBuilder) (.expireAfterWrite (:interval @expiry-options) (:unit @expiry-options)) .build))
;(def caffeine-2 (-> (Caffeine/newBuilder) (.expireAfterWrite 120 TimeUnit/MINUTES) .build))
(def caffeine-2 (-> (Caffeine/newBuilder) (.expireAfterWrite (:interval @expiry-fetch-option) (:unit @expiry-fetch-option)) .build))

(def calculator (BlackScholes.))

(def days-in-a-year 365.0)

;; (defmacro calc-iv [spot ot x days price]
;;   (let [calc-fn (if (= ot 1) '.ivPut '.ivCall)]
;;     (prn "days: " days)
;;     `(try
;;        (let [years# (/ ~days days-in-a-year)
;;              iv# (~calc-fn calculator ~spot ~x years# ~price)]
;;          (prn (str "Spot: " ~spot ", ot: " ~ot "x: " ~x "days: " ~days ", years: " years# ", price: " ~price ", iv: " iv#))
;;          iv#)
;;        (catch BinarySearchException ex#
;;          -1.0))))

(defn calc-iv [spot ot x days price]
  (try
    (let [years (/ days days-in-a-year)
          iv (if (= ot 1)
               (.ivCall calculator spot x years price)
               (.ivPut calculator spot x years price))]
      iv)
    (catch BinarySearchException _
      -1.0)))

(defn calculate
  [spot {:keys [ot x days] :as option}]
  (if (nil? option)
    (do (.warn logger (str "[calculate] option was nil"))
        nil)
    (let [iv-buy (calc-iv spot ot x days (:buy option))
          iv-sell (calc-iv spot ot x days (:sell option))
          br-even 0.0]
      ;(prn (str "Ticker: " (:ticker option) ", iv-buy: " iv-buy ", iv-sell: " iv-sell ", spot: " spot ", ot: " ot ", x: " x " , days: " days ", buy: " (:buy option) ", sell: " (:sell option)))
      (conj option
            {:ivBuy iv-buy
             :ivSell iv-sell
             :brEven br-even}))))

(defn add-on
  ([cur-date info option]
   (if (nil? option)
     (do (.warn logger (str "[add-on] option was nil"))
         nil)
     (let [iso-days (option/iso-8601-and-days (:year info) (:month info) cur-date)]
       (conj option
             {:days (:days iso-days)
              :expiry (:iso iso-days)}))))
  ([cur-date option]
   (if (nil? option)
     (do (.warn logger (str "[add-on] option was nil"))
         nil)
     (let [optionticker (:ticker option)
           info (option/stock-opt-info-ticker optionticker)]
       (add-on cur-date info option)))))

(defn add-on-stock-price [env stock-ticker stock-price]
  (let [open-price (redis/opening-price env stock-ticker)
        tm (unix-time (LocalDate/now) (LocalTime/now))]
    (conj stock-price {:o open-price} {:unixtime tm})))

(defn fetch-stock-options [{:keys [dl cur-date env]} oid]
  (let [ticker (oid->string oid)
        pages (.downloadAll dl ticker)
        page-1 (nordnet/parse-2 (first pages))
        page-rest (map nordnet/parse (rest pages))
        opx (flatten (conj page-rest (:opx page-1)))
        stock-price (add-on-stock-price env ticker (:stock-price page-1))
        add-on-opx (map (partial add-on cur-date) opx)
        add-on-calc (map (partial calculate (:c stock-price)) add-on-opx)]
    {:stock-price stock-price :opx add-on-calc}))

(defn stock-options [ctx oid has-cache]
  (if (= has-cache true)
    ;if ----------------------------------
    (if-let [result (.getIfPresent caffeine-1 oid)]
      (do
        (.info logger "Fetching from cache...")
        result)
      (let [result (fetch-stock-options ctx oid)]
        (.info logger "[stock-options] Empty cache...")
        (.put caffeine-1 oid result)
        result))
    ;else ----------------------------------
    (let [result (fetch-stock-options ctx oid)]
      (.info logger "has-cache == false...")
      (.put caffeine-1 oid result)
      result)))

(defn get-spot [ctx oid]
  (let [result (stock-options ctx oid true)]
    (:stock-price result)))

(defn calls-or-puts
  [ctx
   oid
   ot
   has-cache]
  (let [raw (stock-options ctx oid has-cache)
        opts (filter #(= (:ot %) ot) (:opx raw))]
    {:stock-price (:stock-price raw) :opx opts}))

(defn calls [ctx oid has-cache]
  (calls-or-puts ctx oid CALL has-cache))

(defn puts [ctx oid has-cache]
  (calls-or-puts ctx oid PUT has-cache))

(defn download-and-parse [info dl]
  (let [page (.downloadForOption dl (:option info))]
    (nordnet/parse-2 page)))

(defn populate-cache [info dl]
  (let [cached (download-and-parse info dl)]
    (.put caffeine-2 (:ticker info) cached)))

(defn get-cache [info dl]
  (let [get-fn (fn [] (.getIfPresent caffeine-2 (:ticker info)))
        cached (if-let [tmp (get-fn)]
                 (do
                   (.info logger (str "[get-cache] Cache hit for: " (:ticker info)))
                   tmp)
                 (do
                   (.info logger (str "[get-cache] Empty cache for: " (:ticker info)))
                   (populate-cache info dl)
                   (get-fn)))]
    cached))

(defn find-option [{:keys [dl env cur-date]} optionticker]
  (let [info (option/stock-opt-info-ticker optionticker)
        hit (get-cache info dl)
        hit-op (find-first #(= optionticker (:ticker %)) (:opx hit))
        op (add-on cur-date info  hit-op)
        stock-price (add-on-stock-price env (:ticker info) (:stock-price hit))
        calc-op (calculate (:c stock-price) op)]
    (.info logger (str "[find-option] info: " info ", op: " calc-op))
    {:stock-price stock-price :option calc-op}))

(comment demo []
  (let [ctx (config/get-context :test)]
    (prn ctx)
    (calls ctx 3 true)))

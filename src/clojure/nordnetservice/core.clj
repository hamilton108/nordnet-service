(ns nordnetservice.core
  (:gen-class)
  (:require
   [nordnetservice.config :as config]
   [nordnetservice.adapter.nordnetadapter :as nordnet]
   [nordnetservice.adapter.redisadapter :as redis]
   [nordnetservice.stockoption :as option]
   [nordnetservice.common :refer [oid->string ticker->oid find-first]])
   ;[nordnetservice.adapter.critteradapter :as crit])
  (:import
   (org.slf4j LoggerFactory)
   (java.util.concurrent TimeUnit)
   (com.github.benmanes.caffeine.cache Caffeine)
   (nordnetservice.dto
    StockPriceAndOptions)))

(def logger (LoggerFactory/getLogger "nordnetservice.core"))
(def ca (-> (Caffeine/newBuilder) (.expireAfterWrite 5 TimeUnit/MINUTES) .build))
(def ca-2 (-> (Caffeine/newBuilder) (.expireAfterWrite 5 TimeUnit/SECONDS) .build))

;;(defn page->options [etrade sp page]
;;  (map #(OptionDTO. %) (.options etrade page sp)))

;; (defn fetch-stock-options [{:keys [etrade dl]} oid]
;;   (let [ticker (oid->string oid)
;;         pages (.downloadAll dl ticker)
;;         sp (.stockPrice etrade oid (first pages))
;;         maps (map (partial page->options etrade sp) pages)
;;         fm (flatten maps)
;;         sp-dto (StockPriceDTO. sp)]
;;     (StockPriceAndOptions. sp-dto fm)))

(defn fetch-stock-options [{:keys [dl]} oid]
  (let [ticker (oid->string oid)
        pages (.downloadAll dl ticker)
        page-1 (nordnet/parse-2 (first pages))
        page-rest (map nordnet/parse (rest pages))
        ;sp (nordnet/)
        ;sp (.stockPrice etrade oid (first pages))
        ;maps (map (partial page->options etrade sp) pages)
        ;sp-dto (StockPriceDTO. sp)
        opx (flatten (conj page-rest (:opx page-1)))]
    {:stock-price (:stock-price page-1) :opx opx}))

(defn stock-options [ctx oid has-cache]
  (if (= has-cache true)
    ;if ----------------------------------
    (if-let [result (.getIfPresent ca oid)]
      (do
        (.info logger "Fetching from cache...")
        result)
      (let [result (fetch-stock-options ctx oid)]
        (.info logger "[stock-options] Empty cache...")
        (.put ca oid result)
        result))
    ;else ----------------------------------
    (let [result (fetch-stock-options ctx oid)]
      (.info logger "has-cache == false...")
      (.put ca oid result)
      result)))

(defn calls-or-puts
  [ctx
   oid
   ot
   has-cache]
  (let [raw (stock-options ctx oid has-cache)
        opts (filter #(= (:ot %) ot) (:opx raw))]
    ;(StockPriceAndOptions. (.getStock raw) opts)))
    {:stock-price (:stock-price raw) :opx opts}))

(defn calls [ctx oid has-cache]
  (calls-or-puts ctx oid :call has-cache))

(defn puts [ctx oid has-cache]
  (calls-or-puts ctx oid :put has-cache))

(defn download-and-parse [info dl]
  (let [page (.downloadForOption dl (:option info))]
    (nordnet/parse-2 page)))

(defn populate-cache [info dl]
  (let [cached (download-and-parse info dl)]
    (.put ca-2 (:ticker info) cached)))

(defn get-cache [info dl]
  (let [get-fn (fn [] (.getIfPresent ca-2 (:ticker info)))
        cached (if-let [tmp (get-fn)]
                 tmp
                 (do
                   (.info logger "[get-cache] Empty cache...")
                   (populate-cache info dl)
                   (get-fn)))]
    cached))

(defn calculate [option]
  (if (nil? option)
    (do (.warn logger (str "[calculate] option was nil"))
        nil)
    (conj option
          {:ivBuy 0.0
           :ivSell 0.0
           :brEven 0.0})))

(defn add-on [option info cur-date]
  (if (nil? option)
    (do (.warn logger (str "[add-on] option was nil"))
        nil)
    (let [iso-days (option/iso-8601-and-days (:year info) (:month info) cur-date)]
      (conj option
            {:days (:days iso-days)
             :expiry (:iso iso-days)}))))

(defn find-option [{:keys [dl env cur-date]} optionticker]
  (let [info (option/stock-opt-info-ticker optionticker)
        hit (get-cache info dl)
        hit-op (find-first #(= optionticker (:ticker %)) (:opx hit))
        op (add-on hit-op info cur-date)
        calc-op (calculate op)
        open-price (redis/opening-price env (:ticker info))
        stock-price (conj (:stock-price hit) {:o open-price})]
    (.info logger (str "[find-option] info: " info ", op: " calc-op))
    {:stock-price stock-price :option calc-op}))

(defn demo []
  (let [ctx (config/get-context :test)]
    (prn ctx)
    (find-option ctx "YAR3A528.02X")))



;; (defn find-option_ [{:keys [etrade dl]} optionticker]
;;   (let [info (option/stock-opt-info-ticker optionticker)
;;         oid (ticker->oid (:ticker info))
;;         page (.downloadForOption dl optionticker)
;;         sp (.stockPrice etrade oid page)
;;         opx (map #(OptionDTO. %) (.options etrade page sp))
;;         hit (find-me opx)]
;;     (OptionWithStockPrice. (OptionDTO. hit) (StockPriceDTO sp))))


;("YAR2F550" "YAR2F510" "YAR2F470" "YAR2F410" "YAR2F395" "YAR2F390" "YAR2F375" "YAR2F370")

;; (defn demo []
;;   (let [{:keys [etrade dl]} (config/get-context :test)
;;         info (option/stock-opt-info-ticker "YAR2F370")]
;;     (get-cache info etrade dl)))

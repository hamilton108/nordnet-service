(ns nordnetservice.core
  (:gen-class)
  (:require
   [nordnetservice.config :as config]
   [nordnetservice.stockoption :as option]
   [nordnetservice.common :refer [oid->string ticker->oid find-first]])
   ;[nordnetservice.adapter.critteradapter :as crit])
  (:import
   (org.slf4j LoggerFactory)
   (java.util.concurrent TimeUnit)
   (com.github.benmanes.caffeine.cache Caffeine)
   (nordnetservice.dto
    StockPriceAndOptions
    OptionWithStockPrice
    StockPriceDTO
    OptionDTO)))

(def logger (LoggerFactory/getLogger "nordnetservice.core"))
;(def ca (-> (Caffeine/newBuilder) (.expireAfterWrite 15 TimeUnit/SECONDS) .build))
(def ca (-> (Caffeine/newBuilder) (.expireAfterWrite 5 TimeUnit/MINUTES) .build))
(def ca-2 (-> (Caffeine/newBuilder) (.expireAfterWrite 5 TimeUnit/SECONDS) .build))

(defn page->options [etrade sp page]
  (map #(OptionDTO. %) (.options etrade page sp)))

(defn fetch-stock-options [{:keys [etrade dl]} oid]
  (let [ticker (oid->string oid)
        pages (.downloadAll dl ticker)
        sp (.stockPrice etrade oid (first pages))
        maps (map (partial page->options etrade sp) pages)
        fm (flatten maps)
        sp-dto (StockPriceDTO. sp)]
    (StockPriceAndOptions. sp-dto fm)))

(defn stock-options [ctx oid has-cache]
  (if (= has-cache true)
    ;if ----------------------------------
    (if-let [result (.getIfPresent ca oid)]
      (do
        (.info logger "Fetching from cache...")
        result)
      (let [result (fetch-stock-options ctx oid)]
        (.info logger "Empty  cache...")
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
   is-call
   has-cache]
  (let [raw (stock-options ctx oid has-cache)
        opts (filter #(= (.isCall %) is-call) (.getOptions raw))]
    (StockPriceAndOptions. (.getStock raw) opts)))

(defn calls [ctx oid has-cache]
  (calls-or-puts ctx oid true has-cache))

(defn puts [ctx oid has-cache]
  (calls-or-puts ctx oid false has-cache))

(defn download-and-parse [info etrade dl]
  (let [page (.downloadForOption dl (:option info))
        sp (.stockPrice etrade (:oid info) page)
        opx (page->options etrade sp page)]
    {:sp (StockPriceDTO. sp) :opx opx}))

(defn populate-cache [info etrade dl]
  (let [cached (download-and-parse info etrade dl)]
    (.put ca-2 (:ticker info) cached)))

(defn get-cache [info etrade dl]
  (let [get-fn (fn [] (.getIfPresent ca-2 (:ticker info)))
        cached (if-let [tmp (get-fn)]
                 tmp
                 (do
                   (populate-cache info etrade dl)
                   (get-fn)))]
    cached))

(defn find-option [{:keys [etrade dl]} optionticker]
  (let [info (option/stock-opt-info-ticker optionticker)
        hit (get-cache info etrade dl)
        op (find-first #(= optionticker (.getTicker %)) (:opx hit))]
    (OptionWithStockPrice. op (:sp hit))))

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

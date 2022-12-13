(ns nordnetservice.core
  (:gen-class)
  (:require
   [nordnetservice.config :as config]
   [nordnetservice.common :refer [oid->string]])
   ;[nordnetservice.adapter.critteradapter :as crit])
  (:import
   (org.slf4j LoggerFactory)
   (java.util.concurrent TimeUnit)
   (com.github.benmanes.caffeine.cache Caffeine)
   (nordnetservice.dto StockPriceAndOptions StockPriceDTO OptionDTO)))

(def logger (LoggerFactory/getLogger "nordnetservice.core"))
;(def ca (-> (Caffeine/newBuilder) (.expireAfterWrite 15 TimeUnit/SECONDS) .build))
(def ca (-> (Caffeine/newBuilder) (.expireAfterWrite 5 TimeUnit/MINUTES) .build))

(defn page->options [etrade sp page]
  (map #(OptionDTO. %) (.options etrade page sp)))

(defn fetch-stock-options [{:keys [etrade dl]} oid]
  (let [ticker (oid->string oid)
        pages (.download dl ticker)
        sp (.stockPrice etrade oid (first pages))
        maps (map (partial page->options etrade sp) pages)
        fm (flatten maps)
        sp-dto (StockPriceDTO. sp)]
    (StockPriceAndOptions. sp-dto fm)))

(defn stock-options [ctx oid]
  (if-let [result (.getIfPresent ca oid)]
    (do
      (.info logger "Fetching from cache...")
      result)
    (let [result (fetch-stock-options ctx oid)]
      (.info logger "Empty  cache...")
      (.put ca oid result)
      result)))


;; (defn stock-options
;;   "Map -> Int -> [OptionDTO]"
;;   [ctx oid]
;;   (let [etrade (:etrade ctx)
;;         ;pages (.downloadDerivatives dl tif)
;;         ;page (first pages)
;;         sp (.stockPrice etrade oid page)
;;         opx (map #(OptionDTO. %) (.options etrade page sp))]
;;     opx))

(defn demo []
  (let [ctx (config/get-context :demo)]
    (stock-options ctx 3)))

;; (defn demo2 []
;;   (let [ctx (config/get-context :demo)]
;;     (prn (str "This Factory: " (:factory ctx)))
;;     (crit/stock-market-repos :demo nil)))

;; (defn demo3 [env]
;;   (config/get-context env))
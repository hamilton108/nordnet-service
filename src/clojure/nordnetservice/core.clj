(ns nordnetservice.core
  (:gen-class)
  (:require
   [nordnetservice.common :refer [oid->string]]
   [nordnetservice.config :as config]
   [nordnetservice.adapter.critteradapter :as crit])
  (:import (nordnetservice.dto StockPriceAndOptions OptionDTO)))


(defn page->options [etrade sp page]
  (map #(OptionDTO. %) (.options etrade page sp)))

(defn stock-options [{:keys [etrade dl]} oid]
  (let [ticker (oid->string oid)
        pages (.download dl ticker)
        sp (.stockPrice etrade oid (first pages))
        maps (map (partial page->options etrade sp) pages)]
    (flatten maps)))

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

(defn demo2 []
  (let [ctx (config/get-context :demo)]
    (prn (str "This Factory: " (:factory ctx)))
    (crit/stock-market-repos :demo nil)))

(defn demo3 [env]
  (config/get-context env))
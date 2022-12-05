(ns nordnetservice.core
  (:gen-class)
  (:require [nordnetservice.config :as config]))
  ;(:import (harborview.dto.html.options StockPriceAndOptions OptionDTO)))


(defn stock-options [ctx oid])

;; (defn stock-options
;;   "Map -> Int -> [OptionDTO]"
;;   [ctx oid]
;;   (let [etrade (:etrade ctx)
;;         ;pages (.downloadDerivatives dl tif)
;;         ;page (first pages)
;;         sp (.stockPrice etrade oid page)
;;         opx (map #(OptionDTO. %) (.options etrade page sp))]
;;     opx))
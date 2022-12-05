(ns nordnetservice.adapter.critteradapter
  (:gen-class)
  ;(:require
  ; [rapanui.common :refer [with-session]])

  (:import
   (java.util Optional)
   (critter.mybatis
    CritterMapper
    StockMapper
    StockOptionMapper)
   (critter.repos
    StockMarketRepository)))

(defn stock-market-repos [_]
  (reify StockMarketRepository
    (findStock [_ oid]
      nil)
              ;(.createStock factory oid))
    (findStockOption [_ stockOptInfo]
      (Optional/empty))
    (activePurchasesWithCritters [_  purchaseType]
      ;Int -> List<StockOptionPurchase>
      [])
    (purchasesWithSalesAll [_ purchaseType status optionType]
      ;Int -> Int -> StockOption.OptionType -> List<StockOptionPurchase> 
      [])))


;; (defn fetch-purchases [env]
;;   (with-session CritterMapper
;;     (.activePurchasesWithCritters it env)))


(ns nordnetservice.adapter.critteradapter
  (:gen-class)
  (:require
   [clojure.core.match :refer [match]])
  (:import
   (java.util Optional)
   ;(critter.mybatis
   ; CritterMapper
   ; StockMapper
   ; StockOptionMapper)
   (critter.repos
    StockMarketRepository)))

;(def factory (StockMarketFactory.))

(defn demo-test-repos [factory]
  (reify StockMarketRepository
    (findStock [_ oid]
      (.createStock factory oid))
    (findStockOption [_ _]
      (Optional/empty))
    (activePurchasesWithCritters [_ _]
      [])
    (purchasesWithSalesAll [_ _ _ _]
      [])))

(defn stock-market-repos [env factory]
  (prn env)
  (match env
    :demo
    (demo-test-repos factory)
    :test
    (demo-test-repos factory)
    :prod
    (reify StockMarketRepository
      (findStock [_ oid]
        (.createStock factory oid))
      (findStockOption [_ stockOptInfo]
        (Optional/empty))
      (activePurchasesWithCritters [_  purchaseType]
      ;Int -> List<StockOptionPurchase>
        [])
      (purchasesWithSalesAll [_ purchaseType status optionType]
      ;Int -> Int -> StockOption.OptionType -> List<StockOptionPurchase> 
        []))))


;; (defn fetch-purchases [env]
;;   (with-session CritterMapper
;;     (.activePurchasesWithCritters it env)))


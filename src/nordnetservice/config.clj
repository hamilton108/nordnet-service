(ns nordnetservice.config
  (:require
   [clojure.core.match :refer [match]]
   [nordnetservice.adapter.critteradapter :refer [stock-market-repos]])
  (:import
   (nordnet.html StockOptionParser3)
   (vega.financial.calculator BlackScholes)
   (critter.util StockOptionUtil)
   (nordnet.redis NordnetRedis)
   (java.time LocalDate)))

(defn redis [ct]
  (match ct
    :prod
    (NordnetRedis. "172.20.1.2" 0)
    :else
    (NordnetRedis. "172.20.1.2" 5)))

(defn stock-option-util [ct]
  (match ct
    :prod
    (StockOptionUtil.)
    :test
    (StockOptionUtil. (LocalDate/of 2022 5 25))
    :demo
    (StockOptionUtil. (LocalDate/of 2022 10 17))))

(defn repos [ct]
  stock-market-repos ct)


(defn etrade [ct]
  (let [calc (BlackScholes.)]
    (StockOptionParser3. calc (redis ct) (repos ct) (stock-option-util ct))))

(defn get-context [ct]
  {:etrade (etrade ct)
   :purchase-type (if (= :prod ct) 4 11)})
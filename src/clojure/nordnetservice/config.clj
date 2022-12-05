(ns nordnetservice.config
  (:require
   [clojure.core.match :refer [match]]
   [nordnetservice.adapter.downloadadapter]
   [nordnetservice.adapter.redisadapter]
   [nordnetservice.adapter.critteradapter :refer [stock-market-repos]])
  (:import
   (nordnet.html StockOptionParser3)
   (vega.financial.calculator BlackScholes)
   (critter.util StockOptionUtil)
   (nordnetservice.adapter.downloadadapter
    DemoDownloader
    DefaultDownloader)
   (nordnetservice.adapter.redisadapter OpeningPricesImpl)
   (java.time LocalDate)))

;; (defn redis [ct]
;;   (match ct
;;     :prod
;;     (NordnetRedis. "172.20.1.2" 0)
;;     :else
;;     (NordnetRedis. "172.20.1.2" 5)))

(defn redis [env]
  (OpeningPricesImpl. env))

(defn stock-option-util [env]
  (match env
    :prod
    (StockOptionUtil.)
    :test
    (StockOptionUtil. (LocalDate/of 2022 5 25))
    :demo
    (StockOptionUtil. (LocalDate/of 2022 10 17))))

(defn repos [env]
  stock-market-repos env)

(defn etrade [env]
  (let [calc (BlackScholes.)]
    (StockOptionParser3. calc (redis env) (repos env) (stock-option-util env))))

(defn downloader [env]
  (match env
    :prod
    (DefaultDownloader.)
    :else
    (DemoDownloader.)))

(defn get-context [env]
  {:etrade (etrade env)
   :dl (downloader env)
   :purchase-type (if (= :prod env) 4 11)})
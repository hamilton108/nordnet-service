(ns nordnetservice.config
  (:require
   [clojure.core.match :refer [match]]
   [nordnetservice.adapter.downloadadapter]
   [nordnetservice.adapter.redisadapter]
   [nordnetservice.adapter.critteradapter :refer [stock-market-repos]])
  (:import
   (nordnet.html StockOptionParser3)
   (vega.financial.calculator BlackScholes)
   (nordnetservice.factory StockMarketFactory)
   (critter.util StockOptionUtil)
   (nordnetservice.adapter.downloadadapter
    TestDownloader
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
    (StockOptionUtil. (LocalDate/of 2022 1 1))))

(defn repos [env factory]
  (stock-market-repos env factory))

(defn etrade [env factory]
  (let [calc (BlackScholes.)]
    (StockOptionParser3. calc (redis env) (repos env factory) (stock-option-util env))))

(defn factory [env]
  (StockMarketFactory. (stock-option-util env)))

(defn downloader [env]
  (match env
    :prod
    (DefaultDownloader.)
    :else
    (TestDownloader. env)))

(defn get-context [env]
  (let [f (factory env)]
    {:etrade (etrade env f)
     :dl (downloader env)
     :factory f
     :env env
     :purchase-type (if (= :prod env) 4 11)}))
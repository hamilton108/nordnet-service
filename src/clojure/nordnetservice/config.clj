(ns nordnetservice.config
  (:require
   [clojure.core.match :refer [match]]
   [nordnetservice.adapter.downloadadapter]
   [nordnetservice.adapter.redisadapter]
   [nordnetservice.adapter.critteradapter :refer [stock-market-repos]])
  (:import
   (nordnetservice.factory StockMarketFactory)
   (nordnetservice.adapter.downloadadapter
    TestDownloader
    DefaultDownloader)
   (java.time LocalDate)))

;; (defn redis [ct]
;;   (match ct
;;     :prod
;;     (NordnetRedis. "172.20.1.2" 0)
;;     :else
;;     (NordnetRedis. "172.20.1.2" 5)))

;; (defn stock-option-util [env]
;;   (match env
;;     :prod
;;     (StockOptionUtil.)
;;     :test
;;     (StockOptionUtil. (LocalDate/of 2022 5 25))
;;     :demo
;;     (StockOptionUtil. (LocalDate/of 2022 1 1))))

(defn current-date [env]
  (match env
    :prod
    (LocalDate/now)
    :test
    (LocalDate/of 2022 9 25)
    :demo
    (LocalDate/of 2022 1 1)))

(defn repos [env factory]
  (stock-market-repos env factory))

;; (defn etrade [env factory]
;;   (let [calc (BlackScholes.)]
;;     (StockOptionParser3. calc (redis env) (repos env factory) (current-date env))))

(defn factory [env]
  (StockMarketFactory. (current-date env)))

(defn downloader [env]
  (match env
    :prod
    (DefaultDownloader.)
    :else
    (TestDownloader. env)))

(defn get-context [env]
  (let [f (factory env)]
    {:dl (downloader env)
     :factory f
     :env env
     :cur-date (current-date env)
     :purchase-type (if (= :prod env) 4 11)}))
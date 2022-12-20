(ns nordnetservice.coretest
  (:require
   [clojure.test :refer [deftest is]]
   [nordnetservice.common :refer [oid->string not-nil?]]
   [nordnetservice.config :as config]
   [nordnetservice.core :as core])
  (:import (nordnet.html StockOptionParser3)))

(def get-page
  (memoize
   (fn []
     (let [ctx (config/get-context :test)
           dl (:dl ctx)
           pages (.download dl (oid->string 3))]
       (.get pages 0)))))

(defn find-option [ticker prices]
  (let [pred (fn [x] (= (-> x .getStockOption .getTicker) ticker))]
    (first (filter pred prices))))


;; (deftest normalize-ticker
;;   (let [ctx (config/get-context :test)
;;         parser (:etrade ctx)
;;         invalid (.elementToTicker parser "Norway YAR2F534.58X")
;;         valid (.elementToTicker parser "Norway YAR2F470")]
;;     (is (nil? invalid))
;;     (is (not-nil? valid))
;;     (is (= valid "YAR2F470"))))

;; (deftest test-core-stock-options
;;   (let [ctx (config/get-context :test)
;;         opts (core/stock-options ctx 3 true)]
;;     (is (= (count opts) 16))))

;; (defn demo []
;;   (let [page (get-page)
;;         ctx (config/get-context :test)
;;         parser (:etrade ctx)
;;         sp (.stockPrice parser 3 page)
;;         opts (.options parser page sp)]
;;     opts))

;; (deftest test-stock-options
;;   (let [page (get-page)
;;         ctx (config/get-context :test)
;;         parser (:etrade ctx)
;;         sp (.stockPrice parser 3 page)
;;         opts (.options parser page sp)
;;         f_550 (find-option "YAR2F550" opts)
;;         r_550 (find-option "YAR2R550" opts)]
;;     (is (not-nil? sp))
;;     (is (= (-> sp .getStock .getTicker) "YAR"))
;;     (is (= (-> sp .getOpn) 482.0))
;;     (is (= (-> sp .getHi) 488.7))
;;     (is (= (-> sp .getLo) 481.0))
;;     (is (= (-> sp .getCls) 487.4))
;;     (is (= (count opts) 16))
;;     (is (not-nil? f_550))
;;     (is (= (.getBuy f_550) 0.85))
;;     (is (= (.getSell f_550) 1.25))
;;     (is (= (.getX f_550) 550.0))
;;     (is (not-nil? r_550))
;;     (is (= (.getBuy r_550) 61.75))
;;     (is (= (.getSell r_550) 64.75))
;;     (is (= (.getX r_550) 550.0))))


(deftest test-url-for-ticker
  (is (= "whatever" (core/url-for "NNN"))))
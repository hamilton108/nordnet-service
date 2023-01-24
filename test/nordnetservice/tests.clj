(ns nordnetservice.tests
  (:require
   [clojure.test :refer [is deftest]]
   [nordnetservice.common :as COM]
   [nordnetservice.stockoption :as opt]
   [nordnetservice.adapter.redisadapter :as redis]
   [nordnetservice.adapter.nordnetadapter :as nordnet]
   [nordnetservice.config :as config])
  (:import
   (java.time LocalDate LocalTime)))

(defn implements [clazz interface]
  (let  [i (-> clazz .getClass .getInterfaces)
         c (count (filter #(> % 0) (map #(.indexOf (.getName %) interface) i)))]
    (> c 0)))

(deftest test-repos
  (let [interface "StockMarketRepository"
        prod (config/repos :prod nil)
        tst (config/repos :test nil)
        demo (config/repos :demo nil)]
    (is (= (implements prod interface) true))
    (is (= (implements tst  interface) true))
    (is (= (implements demo interface) true))))

;(def test-date (LocalDate/of 2023 3 30))

(deftest test-redis-expiry
  (let [test-date (LocalDate/of 2022 6 16)
        url (redis/url-all :test "NHY" test-date)]
    ;(doseq [u url] (prn (.getUrl u)))
    (is (= (.length url) 8))))

(deftest test-test-url
  (let [tu (redis/test-url :test)]
    (is (= "file:////home/rcs/opt/java/nordnet-repos/src/integrationTest/resources/html/derivatives/YAR-3.html" tu))))


(deftest test-nordnet-millis
  (let [test-date (LocalDate/of 2023 3 30)]
    (is (= 1680127200000 (opt/nordnet-millis test-date)))))

(def march-23
  {:exp 1679007600000
   :call-1
   (let [o "EQNR3C428.99Z"]
     {:t o :r {:ticker "EQNR", :option o :oid 2, :year 2023, :month 3, :ot :call}})
   :put-1
   (let [o "EQNR3O428.99Z"]
     {:t o :r {:ticker "EQNR", :option o :oid 2, :year 2023, :month 3, :ot :put}})})

(def june-23
  {:exp 1686866400000
   :call-1
   (let [o "NHY3F50.79X"]
     {:t o :r {:ticker "NHY", :option o :oid 1, :year 2023, :month 6, :ot :call}})
   :put-1
   (let [o "NHY3R140"]
     {:t o :r {:ticker "NHY", :option o :oid 1, :year 2023, :month 6, :ot :put}})})

(def december-23
  {:exp 1702594800000
   :call-1
   (let [o "YAR3L380"]
     {:t o :r {:ticker "YAR", :option o :oid 3, :year 2023, :month 12, :ot :call}})
   :put-1
   (let [o "YAR3X380"]
     {:t o :r {:ticker "YAR", :option o :oid 3, :year 2023, :month 12, :ot :put}})})

(deftest stock-opt-info-ticker
  (let [c1 (:call-1 march-23)
        p1 (:put-1 march-23)]
    (is (= (:r c1) (opt/stock-opt-info-ticker (:t c1))))
    (is (= (:r p1) (opt/stock-opt-info-ticker (:t p1)))))
  (let [c1 (:call-1 june-23)
        p1 (:put-1 june-23)]
    (is (= (:r c1) (opt/stock-opt-info-ticker (:t c1))))
    (is (= (:r p1) (opt/stock-opt-info-ticker (:t p1)))))
  (let [c1 (:call-1 december-23)
        p1 (:put-1 december-23)]
    (is (= (:r c1) (opt/stock-opt-info-ticker (:t c1))))
    (is (= (:r p1) (opt/stock-opt-info-ticker (:t p1))))))

(deftest millis-for-ticker-test
  (let [e1 (:exp march-23)
        c1 (get-in march-23 [:call-1 :t])]
    (is (= e1 (opt/millis-for c1))))
  (let [e1 (:exp june-23)
        c1 (get-in june-23 [:call-1 :t])]
    (is (= e1 (opt/millis-for c1))))
  (let [e1 (:exp december-23)
        c1 (get-in december-23 [:call-1 :t])]
    (is (= e1 (opt/millis-for c1)))))

;;----------------------------- common -----------------------------

(deftest test-unix-time
  (let [ld (LocalDate/of 2023 1 19)
        tm (LocalTime/of 17 33 0)]
    (is (= 1674149580000 (COM/unix-time ld tm)))))

;;----------------------------- nordnetadapter -----------------------------

(deftest test-match-ticker
  (let [ticker "YAR3A371.57X"
        s1 (str "Norwayx " ticker)
        s2 (str "Norway   " ticker)
        m1 (nordnet/match-ticker s1)
        m2 (nordnet/match-ticker s2)]
    (is (= m1 nil))
    (is (= m2 ticker))))


;; (def get-page
;;   (memoize
;;    (fn []
;;      (let [ctx (config/get-context :test)
;;            dl (:dl ctx)
;;            pages (.download dl (oid->string 3))]
;;        (.get pages 0)))))


;; (deftest get-cache-test
;;   (let [info (option/stock-opt-info-ticker "YAR2F370")]
;;         hit (core/get-cache info )]
;;     (is (not-nil? hit))
;;     (is (not-nil? (:sp hit)))
;;     (is (not-nil? (:opx hit)))
;;     (is (= 16 (count (:opx hit))))))

      ;;    (let [ticker "YAR2F370"
      ;;          hit (core/find-option (config/get-context :test) ticker)]
      ;;      (is (not-nil? hit))
      ;;      (let [op (.getOption hit)
      ;;            sp (.getStock hit)]
      ;;        (is (not-nil? sp))
      ;;        (is (not-nil? op))
      ;;        (is (= ticker (-> op .getTicker)))
      ;;        (is (= "YAR" (-> sp .getStockPrice .getTicker))))))


;; (defn find-option [ticker prices]
;; (let [pred (fn [x] (= (-> x .getStockOption .getTicker) ticker))]
;;   (first (filter pred prices))))


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


;; (deftest test-url-for-ticker
;; (is (= "whatever" (core/url-for "NNN"))))
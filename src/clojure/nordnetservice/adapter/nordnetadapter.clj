(ns nordnetservice.adapter.nordnetadapter
  (:gen-class)
  (:require
   [nordnetservice.adapter.downloadadapter :refer [url->page]]
   [nordnetservice.common :refer [rs]]
   [clojure.core.match :refer [match]])
  (:import
   (org.jsoup.nodes Element)
   (nordnet.downloader PageInfo)
   (java.util.regex Pattern Matcher)
   (org.jsoup Jsoup)))

;(def DOWNLOAD_TIME 10)
(def SP_CLS 4)
(def SP_HI 7)
(def SP_LO 8)

(def CALL_TICKER 1)
(def CALL_BID 3)
(def CALL_ASK 4)
(def X 7)
(def PUT_BID 9)
(def PUT_ASK 10)
(def PUT_TICKER 13)

(defn content [^PageInfo pi]
  (-> pi .getPage .getWebResponse .getContentAsString))

(defn element-text [^Element el]
  (.text ^Element (nth (.children el) 0)))


(def pat (Pattern/compile "Norway\\s*(\\S*)"))

(defn match-ticker [^String s]
  (let [^Matcher m (.matcher ^Pattern pat s)]
    (if (.matches m) (.group m 1) nil)))

(defn element->value [rc index format]
  (let [tx (nth rc index)
        sval (element-text tx)]
    (match format
      :double (rs sval)
      :ticker (match-ticker sval)
      :time nil)))

(defn parse-stockprice [^Element el]
  (let [stockprice-row ^Element (nth (.children el) 1)
        rc (.children stockprice-row)]
    {:h (element->value rc SP_HI :double)
     :l (element->value rc SP_LO :double)
     :c (element->value rc SP_CLS :double)}))

(defn parse-option [option-type ^Element el]
  (let [row (.children el)
        [i-tick i-buy i-sell] (if (= option-type :call)
                                [CALL_TICKER CALL_BID CALL_ASK]
                                [PUT_TICKER PUT_BID PUT_ASK])
        ticker (element->value row i-tick :ticker)
        x (element->value row X :double)
        buy (element->value row i-buy :double)
        sell (element->value row i-sell :double)]
    {:ticker ticker, :x x
     :buy buy
     :sell sell
     :ot option-type}))

(defn parse-options [^Element el]
  (let [rows (.children el)
        value-rows (drop 1 rows)
        puts (map (partial parse-option :put) value-rows)
        calls (map (partial parse-option :call) value-rows)]
    (concat puts calls)))

(defn parse [page]
  (let [s (content page)
        d (Jsoup/parse s)
        [_ opx] (.select d "[role=table]")]
    (parse-options opx)))

(defn parse-2 [page]
  (let [s (content page)
        d (Jsoup/parse s)
        [top opx] (.select d "[role=table]")]
    {:stock-price (parse-stockprice top) :opx (parse-options opx)}))

    ;(prn (str (class top) ", " (class opx)))
    ;{:stock-price (parse-stockprice top) :options (parse-options opx)}))

(defn page []
  (let [url  "file:////home/rcs/opt/java/nordnet-service/test/resources/html/yar.html"
        page (url->page url)]
    page))

;; {"options":[
;;             {"days":167.0,"x":550.0,"buy":0.85,"sell":1.25,"ticker":"YAR2F550",
;;              "ivBuy":0.09375,"ivSell":0.10312500000000001,"brEven":492.159765625,
;;              "expiry":"2022-06-17"},
;;             {"days":167.0,"x":510.0,"buy":6.0,"sell":6.9,"ticker":"YAR2F510",
;;              "ivBuy":0.07968750000000002,"ivSell":0.08750000000000001,
;;              "brEven":490.01787109375,"expiry":"2022-06-17"},
;;             {"days":167.0,"x":470.0,"buy":24.25,"sell":26.25,"ticker":"YAR2F470",
;;              "ivBuy":0.0,"ivSell":0.0,"brEven":0.0,"expiry":"2022-06-17"},
;;             {"days":167.0,"x":410.0,"buy":76.5,"sell":79.5,"ticker":"YAR2F410",
;;              "ivBuy":0.0,"ivSell":0.0,"brEven":0.0,"expiry":"2022-06-17"},
;;             {"days":167.0,"x":395.0,"buy":91.5,"sell":94.5,"ticker":"YAR2F395",
;;              "ivBuy":0.0,"ivSell":0.0,"brEven":0.0,"expiry":"2022-06-17"},
;;             {"days":167.0,"x":390.0,"buy":96.5,"sell":99.5,"ticker":"YAR2F390",
;;              "ivBuy":0.0,"ivSell":0.0,"brEven":0.0,"expiry":"2022-06-17"},
;;             {"days":167.0,"x":375.0,"buy":111.25,"sell":114.25,"ticker":"YAR2F375",
;;              "ivBuy":0.0,"ivSell":0.0,"brEven":0.0,"expiry":"2022-06-17"},
;;             {"days":167.0,"x":370.0,"buy":116.25,"sell":119.25,"ticker":"YAR2F370",
;;              "ivBuy":0.0,"ivSell":0.0,"brEven":0.0,"expiry":"2022-06-17"}],
;;  "stock":{"o":482.0,"h":488.7,"l":481.0,"c":487.4,"unixTime":1641036600000}}
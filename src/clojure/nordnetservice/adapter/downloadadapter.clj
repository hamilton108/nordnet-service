(ns nordnetservice.adapter.downloadadapter
  (:gen-class)
  (:require
   [nordnetservice.common :as common]
   [nordnetservice.stockoption :as option]
   [nordnetservice.ports :refer [Downloader]]
   [nordnetservice.adapter.redisadapter :as redis])
  (:import
   (java.time LocalDate)
   (com.gargoylesoftware.htmlunit WebClient)
   (nordnet.downloader PageInfo)))

(def web-client
  (let [result (WebClient.)]
    (-> result .getOptions (.setJavaScriptEnabled false))
    result))

(defn url->page [^String url]
  (PageInfo. (.getPage ^WebClient web-client url) nil nil))

    ;; private String urlFileFor(String ticker, String nordnetUnixTime) {
    ;;     return String.format("/market/options?currency=NOK&underlyingSymbol=%s&expireDate=%s", ticker,  nordnetUnixTime);
    ;; }

    ;; public URL urlFor(String ticker, String nordnetUnixTime) {
    ;;     try {
    ;;         return new URL("https", "www.nordnet.no",urlFileFor(ticker,nordnetUnixTime));



(defrecord TestDownloader [env]
  Downloader
  (downloadAll [_ _]
    (let [url (redis/test-url env)]
      [(url->page url)]))
  (downloadForOption [_ _]
    (let [url (redis/test-url env)]
      (url->page url))))

(defrecord DefaultDownloader []
  Downloader
  (downloadAll [_ ticker]
    (let [urls (redis/url-all :prod ticker (LocalDate/now))]
      (mapv url->page urls)))
  (downloadForOption [_ optionticker]
    (let [info (option/stock-opt-info-ticker optionticker)
          y (:year info)
          m (:month info)
          stock-ticker (:ticker info)
          unixtime (option/millis-for y m)
          url (common/url-for stock-ticker unixtime)]
      (url->page url))))

(defn demo []
  (let [dl (DefaultDownloader.)]
    (.downloadAll dl "YAR")))
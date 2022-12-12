(ns nordnetservice.adapter.downloadadapter
  (:gen-class)
  (:require
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

(defn url->page [url]
  (PageInfo. (.getPage web-client url) nil nil))

(defrecord TestDownloader [env]
  Downloader
  (download [_ _]
    (let [url (redis/test-url env)]
      [(url->page url)])))

(defrecord DefaultDownloader []
  Downloader
  (download [_ ticker]
    (let [urls (redis/url :prod ticker (LocalDate/now))]
      (mapv url->page urls))))
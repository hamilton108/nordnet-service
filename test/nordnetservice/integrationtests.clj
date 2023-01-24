(ns nordnetservice.integrationtests
  (:require
   [clojure.test :refer [is deftest]]
   [nordnetservice.stockoption :as opt]
   [nordnetservice.config  :refer [get-context]]
   [nordnetservice.adapter.downloadadapter :refer [url->page]]
   [nordnetservice.common :refer [url-for not-nil?]])
  (:import
   (java.time LocalDate)))


(deftest test-download-and-parse-yar
  (let [today (LocalDate/now)
        third-friday-next-month (opt/third-friday (.getYear today) (.getMonthValue today))
        millis (opt/nordnet-millis third-friday-next-month)
        url (url-for "YAR" (str millis))
        page-info (url->page url)
        ;page (.getPage info)
        ctx (get-context :prod)
        etrade (:etrade ctx)]
    (is (not-nil? (.getPage page-info)))
    (prn (.getPage page-info))
    (let
     [sp (.stockPrice etrade 3 page-info)]
      (is (not-nil? sp))
      (let [opx (.options etrade page-info sp)]
        (is (not-nil? opx))))))

(defn page-info []
  (let [today (LocalDate/now)
        third-friday-next-month (opt/third-friday (.getYear today) (.getMonthValue today))
        millis (opt/nordnet-millis third-friday-next-month)
        url (url-for "YAR" (str millis))]
    (url->page url)))

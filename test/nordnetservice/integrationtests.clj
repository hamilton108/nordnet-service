(ns nordnetservice.integrationtests
  (:require
   [clojure.test :refer [is deftest]]
   [nordnetservice.stockoption :as opt]
   [nordnetservice.adapter.downloadadapter :refer [url->page]]
   [nordnetservice.adapter.nordnetadapter :refer [parse-2]]
   [nordnetservice.common :refer [url-for not-nil?]])
  (:import
   (java.time LocalDate)))

(defn page-info []
  (let [today (LocalDate/now)
        third-friday-next-month (opt/third-friday (.getYear today) (.getMonthValue today))
        millis (opt/nordnet-millis third-friday-next-month)
        url (url-for "YAR" (str millis))]
    (prn url)
    (url->page url)))

(deftest test-download-and-parse-yar
  (let [pi (page-info)]
    (is (not-nil? (.getPage pi)))
    (let [result (parse-2 pi)]
      (prn result)
      (is (not-nil? result))
      (is (not-nil? (:stock-price result)))
      (is (not-nil? (:opx result))))))


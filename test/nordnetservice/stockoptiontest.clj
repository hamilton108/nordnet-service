(ns nordnetservice.stockoptiontest
  (:require
   [clojure.test :refer [is deftest]]
   [nordnetservice.stockoption :as opt])
  (:import
   (java.time LocalDate)))

(def test-date (LocalDate/of 2023 3 30))

(deftest test-nordnet-millis
  (is (= 1680127200000 (opt/nordnet-millis test-date))))

(def march-23
  {:exp 1679007600000
   :call-1 {:t "EQNR3C428.99Z" :r {:ticker "EQNR", :oid 2, :year 2023, :month 3, :ot :call}}
   :put-1  {:t "EQNR3O428.99Z" :r {:ticker "EQNR", :oid 2, :year 2023, :month 3, :ot :put}}})

(def june-23
  {:exp 1686866400000
   :call-1 {:t "NHY3F50.79X" :r {:ticker "NHY", :oid 1, :year 2023, :month 6, :ot :call}}
   :put-1  {:t "NHY3R140" :r {:ticker "NHY", :oid 1, :year 2023, :month 6, :ot :put}}})

(def december-23
  {:exp 1702594800000
   :call-1 {:t "YAR3L380" :r {:ticker "YAR", :oid 3, :year 2023, :month 12, :ot :call}}
   :put-1  {:t "YAR3X380" :r {:ticker "YAR", :oid 3, :year 2023, :month 12, :ot :put}}})

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
    (is (= e1 (opt/millis-for-ticker c1))))
  (let [e1 (:exp december-23)
        c1 (get-in december-23 [:call-1 :t])]
    (is (= e1 (opt/millis-for-ticker c1)))))

;; (comment test-url-for-ticker
;;          (let [o "NHY3A72"]
;;            (is (= (opt/url-for-ticker o) ""))))
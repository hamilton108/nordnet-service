(ns nordnetservice.adapter.redisadaptertest
  (:require
   [clojure.test :refer [deftest is]]
   [nordnetservice.adapter.redisadapter :as redis])
  (:import (java.time LocalDate)))


(def test-date (LocalDate/of 2022 3 30))

(deftest test-nordnet-millis
  (is (= 1648598400000 (redis/nordnet-millis test-date))))

(deftest test-url
  (let [url (redis/url :test "NHY" test-date)]
    ;(doseq [u url] (prn (.getUrl u)))
    (is (= (.length url) 8))))

;; (deftest test-demo-url
;;   (let [url (redis/demo-url :demo)]
;;     (is (= "s" url))))


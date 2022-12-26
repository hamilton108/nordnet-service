(ns nordnetservice.adapter.redisadaptertest
  (:require
   [clojure.test :refer [deftest is]]
   [nordnetservice.adapter.redisadapter :as redis])
  (:import (java.time LocalDate)))

(def test-date (LocalDate/of 2022 3 30))

(deftest test-url
  (let [url (redis/url-all :test "NHY" test-date)]
    ;(doseq [u url] (prn (.getUrl u)))
    (is (= (.length url) 8))))

(deftest test-test-url
  (let [tu (redis/test-url :test)]
    (is (= "file:////home/rcs/opt/java/nordnet-repos/src/integrationTest/resources/html/derivatives/YAR-3.html" tu))))

;; (deftest test-demo-url
;;   (let [url (redis/demo-url :demo)]
;;     (is (= "s" url))))


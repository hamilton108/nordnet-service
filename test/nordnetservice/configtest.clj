(ns nordnetservice.configtest
  (:require
   [clojure.test :refer [deftest is]]
   [nordnetservice.config :as config]))


;; // Get all interfaces implemented by the java.util.Date class and
;; // print their names.
;; Class<?>[] interfaces = date.getClass().getInterfaces();
;; for (Class<?> i : interfaces) {
;;     System.out.printf("Interface of %s = %s%n", date.getClass().getName(), i.getName());
;; }

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
    (is (= (implements prod interface) true))
    (is (= (implements prod interface) true))))

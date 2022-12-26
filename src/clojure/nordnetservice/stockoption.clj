(ns nordnetservice.stockoption
  (:require
   [nordnetservice.common :refer [ticker->oid]])
  (:import
   (java.util.regex Pattern)
   (java.time.temporal TemporalAdjusters)
   (java.time.chrono ChronoLocalDateTime)
   ;(vega.financial StockOption$OptionType)
   (java.time
    DayOfWeek
    LocalDate
    ZoneId)))

(def EUROPE_OSLO (ZoneId/of "Europe/Oslo"))

(def pattern  (Pattern/compile "(\\D+)(\\d)(\\D)\\.*" Pattern/CASE_INSENSITIVE))

(defn nordnet-millis
  "LocalDate -> long"
  [^LocalDate dx]
  (let [tm ^ChronoLocalDateTime (.atStartOfDay dx)
        zone (.atZone tm EUROPE_OSLO)
        epoch (.toEpochSecond zone)]
    (* epoch 1000)))

(defn third-friday
  "Int -> Int -> LocalDate"
  [year month]
  (-> (LocalDate/of year month 1)
      (.with
       (TemporalAdjusters/dayOfWeekInMonth 3 DayOfWeek/FRIDAY))))

(def str->option-type
  {"A"  {:ot :call :m 1}
   "B"  {:ot :call :m 2}
   "C"  {:ot :call :m 3}
   "D"  {:ot :call :m 4}
   "E"  {:ot :call :m 5}
   "F"  {:ot :call :m 6}
   "G"  {:ot :call :m 7}
   "H"  {:ot :call :m 8}
   "I"  {:ot :call :m 9}
   "J"  {:ot :call :m 10}
   "K"  {:ot :call :m 11}
   "L"  {:ot :call :m 12}
   "M"  {:ot :put :m 1}
   "N"  {:ot :put :m 2}
   "O"  {:ot :put :m 3}
   "P"  {:ot :put :m 4}
   "Q"  {:ot :put :m 5}
   "R"  {:ot :put :m 6}
   "S"  {:ot :put :m 7}
   "T"  {:ot :put :m 8}
   "U"  {:ot :put :m 9}
   "V"  {:ot :put :m 10}
   "W"  {:ot :put :m 11}
   "X"  {:ot :put :m 12}})

(def str->year
  {"2" 2022
   "3" 2023
   "4" 2024
   "5" 2025
   "6" 2026
   "7" 2027
   "8" 2028
   "9" 2029})


(defn stock-opt-info-ticker [ticker]
  (let [m (.matcher pattern ticker)]
    (if (.find m)
      (let [stik (.group m 1)
            year (str->year (.group m 2))
            oinfo (str->option-type (.group m 3))
            month (:m oinfo)
            oid (ticker->oid stik)]
        {:ticker stik
         :option ticker
         :oid oid
         :year year
         :month  month
         :ot (:ot oinfo)})
      {})))

(defn millis-for
  ([ticker]
   (let [info (stock-opt-info-ticker ticker)]
     (millis-for (:year info) (:month info))))
  ([year month]
   (let [friday-3 (third-friday year month)]
     (nordnet-millis friday-3))))

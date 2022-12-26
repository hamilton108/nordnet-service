(ns nordnetservice.ports
  (:gen-class))

(defprotocol Downloader
  (downloadAll [this ticker])
  (downloadForOption [this optionticker]))
(ns nordnetservice.ports
  (:gen-class))

(defprotocol Downloader
  (download [this ticker]))
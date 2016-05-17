(ns weareswat.meowallet-integration.handlers.index
  "Controller to handle the root/index request to the server"
  (:require [clanhr.reply.core :as reply]
            [result.core :as result]))

(defn handle
  "Runs a welcome route"
  [request]
  (-> {:name "WeAreSWAT Meo Wallet Integration"
       :version (System/getProperty "meowallet-integration.version")}
      result/success
      reply/ok))

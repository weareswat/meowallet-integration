(ns weareswat.meowallet-integration.handlers.generate-mb-ref
  "Controller to handle the root/payment-reference/create request to the server"
  (:require [clanhr.reply.core :as reply]
            [clanhr.reply.json :as json]
            [clojure.core.async :refer [<!]]
            [weareswat.meowallet-integration.core.generate-mb-ref :as generate-mb-ref]))

(defn handle
  "Runs a root/payment-reference/create route"
  [request]
  (reply/async-reply
    (let [data (json/build (slurp (:body request)))
          result (<! (generate-mb-ref/run (:context request) data))]
      (reply/result result))))

(ns weareswat.meowallet-integration.handlers.notify-about-payment
  "Controller to handle the root/payment-reference/event
   This handler should be called by meowallet when the state of some mb-reference
   changed, to paid, expired.. Here we just receive te request, convert the related
   data and sends it to the payment-service"
  (:require [clanhr.reply.core :as reply]
            [clanhr.reply.json :as json]
            [clojure.core.async :refer [<!]]
            [weareswat.meowallet-integration.core.notify-about-payment :as notify-about-payment]))

(defn handle
  "Runs a root/payment-reference/event route"
  [request]
  (reply/async-reply
    (let [data (json/build (slurp (:body request)))
          result (<! (notify-about-payment/run! (:context request) data))]
      (reply/result result))))

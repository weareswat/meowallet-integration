(ns weareswat.meowallet-integration.core.simulator
  (:refer-clojure :exclude [run!])
  (:require [result.core :as result]
            [clj-meowallet.core :as meowallet]
            [clojure.core.async :refer [go <!]]
            [environ.core :refer [env]]
            [weareswat.meowallet-integration.core.notify-about-payment :as notify-about-payment]))

(defn should-simulate?
  [data]
  (:simulator data))

(defn transform-data
  [data]
  (let [new-data {:currency (:currency data)
                  :amount (get-in data [:mb :amount])
                  :method (:payment-method data)
                  :event "COMPLETED"
                  :supplier-id "MeoWallet"
                  :operation-id (:transaction-id data)
                  :operation-status "COMPLETED"}]
    (prn "I'll do a request with this data: " new-data)
    (prn "Sync endpoint: " (str (notify-about-payment/host) notify-about-payment/path-to-sync-event))
    (prn "Verify endpoint: " (str (notify-about-payment/host) notify-about-payment/path-to-verify))
    (result/success new-data)))

(defn simulate-request
  [context data]
  (-> (assoc context :verify-cb (fn [auth-token url] (go {:success true :status 200})))
      (notify-about-payment/run! (transform-data data))))

(defn run!
  [context user-input-data output-data]
  (when (should-simulate? user-input-data)
    (simulate-request context output-data)))

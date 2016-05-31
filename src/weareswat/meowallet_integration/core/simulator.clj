(ns weareswat.meowallet-integration.core.simulator
  (:refer-clojure :exclude [run!])
  (:require [result.core :as result]
            [clj-meowallet.core :as meowallet]
            [clojure.core.async :refer [go <!]]
            [environ.core :refer [env]]
            [weareswat.meowallet-integration.core.notify-about-payment :as notify-about-payment]))

(defn not-production?
  []
  (not= :production (env :production)))

(defn should-simulate?
  [data]
  (and (not-production?)
       (:simulator data)))

(defn transform-data
  [data]
  (result/success
    {:currency (:currency data)
     :amount (get-in data [:mb :amount])
     :method (:payment-method data)
     :event "COMPLETED"
     :operation-id (:transaction-id data)
     :operation-status "COMPLETED"}))

(defn simulate-request
  [data]
  (with-redefs [meowallet/verify-callback (fn [auth-token url]
                                            (go {:success true
                                                 :status 200}))]
    (->> (transform-data data)
         (notify-about-payment/run! {}))))

(defn run!
  [user-input-data output-data]
  (when (should-simulate? user-input-data)
    (simulate-request output-data)))

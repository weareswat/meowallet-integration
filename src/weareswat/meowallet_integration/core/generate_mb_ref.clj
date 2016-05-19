(ns weareswat.meowallet-integration.core.generate-mb-ref
  (:require [result.core :as result]
            [clojure.core.async :refer [go <!]]
            [clj-meowallet.core :as meowallet]))

(defn transform-input-data
  [context]
  (result/success
    {:credentials {:meo-wallet-api-key (get-in context [:supplier :api-key])}
     :data {:amount (:amount context)
            :currency (:currency context)
            :expired-at (:expires-at context)}}))

(defn transform-output-data
  [result]
  (result/success
    {:fee (:fee result)
     :created-at (:date result)
     :payment-method (:method result)
     :currency (:currency result)
     :expires-at (:expired-at result)
     :transaction-id (:id result)
     :status (:status result)
     :mb {:ref (get-in result [:mb :ref])
          :entity (get-in result [:mb :entity])
          :amount (:amount result)}}))

(defn generate-mb-ref
  [credentials data]
  (meowallet/generate-mb-ref credentials data))

(defn run
  [context]
  (go
    (result/enforce-let [input-data (transform-input-data context)
                         result (<! (generate-mb-ref (:credentials input-data)
                                                     (:data input-data)))]
      (transform-output-data result))))

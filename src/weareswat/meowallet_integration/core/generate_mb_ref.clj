(ns weareswat.meowallet-integration.core.generate-mb-ref
  (:require [result.core :as result]
            [clj-meowallet.core :as meowallet]))

(defn transform-input-data
  [context]
  {:credentials {:meo-wallet-api-key (get-in context [:provider :api-key])}
   :data {:amount (:amount context)
          :currency (:currency context)
          :expires (:expires-at context)}})

(defn transform-output-data
  [result]
  {:fee (:fee result)
   :created-at (:date result)
   :payment-method (:method result)
   :currency (:currency result)
   :expires-at (:expires result)
   :transaction-id (:id result)
   :status (:status result)
   :mb {:ref (get-in result [:mb :ref])
        :entity (get-in result [:mb :entity])
        :amount (:amount result)}})

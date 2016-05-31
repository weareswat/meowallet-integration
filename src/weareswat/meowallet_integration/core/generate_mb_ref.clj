(ns weareswat.meowallet-integration.core.generate-mb-ref
  (:require [result.core :as result]
            [clojure.core.async :refer [go <!]]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [weareswat.meowallet-integration.core.simulator :as simulator]
            [weareswat.meowallet-integration.models.payment-reference-request :as prr]
            [clj-meowallet.core :as meowallet]))

(def custom-formatter (f/formatter "yyyy-MM-dd'T'HH:mm:ss+0000"))

(defn date->meowallet-date
  [date]
  (when date
    (f/unparse custom-formatter (c/from-string date))))

(defn transform-input-data
  [context]
  (result/success
    {:credentials {:meo-wallet-api-key (get-in context [:supplier :api-key])}
     :data {:amount (:amount context)
            :currency (:currency context)
            :expires (date->meowallet-date (:expires-at context))}}))

(defn transform-output-data
  [result]
  (result/success
    {:fee (:fee result)
     :created-at (:date result)
     :payment-method (:method result)
     :currency (:currency result)
     :expires-at (:expires result)
     :transaction-id (:id result)
     :status (:status result)
     :mb {:ref (get-in result [:mb :ref])
          :entity (get-in result [:mb :entity])
          :amount (:amount result)}}))

(defn generate-mb-ref
  [credentials data]
  (meowallet/generate-mb-ref credentials data))

(defn run
  [context data]
  (go
    (result/enforce-let [_ (prr/validate data)
                         input-data (transform-input-data data)
                         result (<! (generate-mb-ref (:credentials input-data)
                                                     (:data input-data)))
                         output-data (transform-output-data result)]
      (simulator/run! context data output-data)
      output-data)))

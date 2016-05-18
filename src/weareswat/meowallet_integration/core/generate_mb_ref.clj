(ns weareswat.meowallet-integration.core.generate-mb-ref
  (:require [result.core :as result]
            [clj-meowallet.core :as meowallet]))

(defn transform-input-data
  [context]
  {:credentials {:meo-wallet-api-key (:api-key context)}
   :data {:amount (:amount context)
          :currency (:currency context)
          :expires (:expires-at context)}})

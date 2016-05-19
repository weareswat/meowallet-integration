(ns weareswat.meowallet-integration.models.payment-reference-request
  "Payment reference request data representation"
  (:require [result.core :as result]
            [schema.core :as s]))

(def schema
  "Representation of this model"
  {(s/required-key :provider) s/Any
   (s/required-key :amount) s/Num
   (s/required-key :currency) s/Str
   :expires-at s/Any})

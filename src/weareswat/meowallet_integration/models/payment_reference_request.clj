(ns weareswat.meowallet-integration.models.payment-reference-request
  "Payment reference request data representation"
  (:require [result.core :as result]
            [schema.core :as s]))

(def schema
  "Representation of this model"
  {:supplier s/Any
   :amount s/Num
   :currency s/Str})

(defn validate
  "Validates a given payment reference request"
  [model]
  (if-let [errors (s/check schema model)]
    (result/failure errors)
    (result/success)))

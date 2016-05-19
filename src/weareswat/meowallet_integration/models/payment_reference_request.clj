(ns weareswat.meowallet-integration.models.payment-reference-request
  "Payment reference request data representation"
  (:require [result.core :as result]
            [schema.core :as s]))

(def schema
  "Representation of this model"
  {:supplier {:api-key s/Str}
   :amount s/Num
   (s/optional-key :currency) s/Str
   (s/optional-key :expires-at) s/Any})

(defn validate
  "Validates a given payment reference request"
  [model]
  (if-let [errors (s/check schema model)]
    (result/failure errors)
    (result/success)))

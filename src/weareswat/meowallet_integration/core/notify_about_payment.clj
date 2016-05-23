(ns weareswat.meowallet-integration.core.notify-about-payment
  (:require [result.core :as result]
            [environ.core :refer [env]]
            [request-utils.core :as request-utils]))

(defn host
  []
  (env :payment-gateway-host))

(defn transform-data
  [data]
  (result/success
    {:status (:operation-status data)
     :amount (:amount data)
     :currency (:currency data)
     :transaction-id (:operation-id data)
     :provider-key :meo-wallet
     :status-description (:user-error data)}))

(def path-to-callback "/payment-reference/event")

(defn prepare-data-to-request
  [data]
  {:host (host)
   :path path-to-callback
   :body data})

(defn run!
  [context data]
  (result/enforce-let [data (transform-data data)]
    (request-utils/http-post (prepare-data-to-request data))))

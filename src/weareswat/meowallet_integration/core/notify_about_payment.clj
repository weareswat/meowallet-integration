(ns weareswat.meowallet-integration.core.notify-about-payment
  (:require [result.core :as result]
            [clojure.core.async :refer [go <!]]
            [environ.core :refer [env]]
            [clj-meowallet.core :as meowallet]
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

(defn check-data-authenticity
  [data]
  (meowallet/verify-callback data))

(defn run!
  [context data]
  (go
    (result/enforce-let [_ (<! (check-data-authenticity data))
                         data (transform-data data)]
      (request-utils/http-post (prepare-data-to-request data)))))

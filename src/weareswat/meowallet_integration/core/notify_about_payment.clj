(ns weareswat.meowallet-integration.core.notify-about-payment
  (:refer-clojure :exclude [run!])
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
  {:status (:operation_status data)
   :amount (:amount data)
   :currency (:currency data)
   :transaction-id (:operation_id data)
   :supplier-id "MeoWallet"
   :status-description (:user_error data)})

(def path-to-sync-event "/payment-reference/event")
(def path-to-verify "/payment-reference/verify")

(defn prepare-data-to-request
  [data path]
  {:host (host)
   :path path
   :body data})

(defn sync-with-payment-gateway
  [data]
  (request-utils/http-post data))

(defn sync-with-payment-gateway-and-get-auth-token
  [data]
  (go
    (let [data (-> (assoc data :verified false)
                   (assoc :return-supplier true)
                   (prepare-data-to-request path-to-sync-event))
          result (<! (sync-with-payment-gateway data))]
      (prn "Request: " data)
      (prn "Response: " result)
      (if (result/succeeded? result)
        (result/success (:body result))
        result))))

(defn check-data-authenticity
  [context auth-token data]
  (if-let [verifies (:verify-cb context)]
    (verifies auth-token data)
    (-> {:meo-wallet-api-key (get-in auth-token [:supplier :token])}
        (meowallet/verify-callback data))))

(defn sync-verified-with-payment
  [data]
  (request-utils/http-post data))

(defn sync-verified
  [data]
  (-> (assoc data :verified true)
      (prepare-data-to-request path-to-verify)
      (sync-verified-with-payment)))

(defn run!
  [context data]
  (go
    (prn "MEO DATA: " data)
    (let [transformed-data (transform-data data)]
      (result/enforce-let [auth-token (<! (sync-with-payment-gateway-and-get-auth-token transformed-data))
                           _ (<! (check-data-authenticity context auth-token data))]
                          (<! (sync-verified transformed-data))))))

(ns weareswat.meowallet-integration.core.notify-about-payment
  (:refer-clojure :exclude [run!])
  (:require [result.core :as result]
            [clojure.core.async :refer [go <! <!!]]
            [clojure.string :as clj-str]
            [taoensso.timbre :as logger]
            [environ.core :refer [env]]
            [clj-meowallet.core :as meowallet]
            [request-utils.core :as request-utils]))

(defn host
  []
  (env :payment-gateway-host))

(defn transform-data
  [data]
  {:status (clj-str/lower-case (:operation_status data))
   :amount (:amount data)
   :currency (:currency data)
   :transaction-id (:operation_id data)
   :supplier-id "MeoWallet"
   :status-description (:user_error data)})

(def path-to-sync-event "/payment-reference/event")
(def path-to-verify "/payment-reference/verify-event")

(defn prepare-data-to-request
  [data path]
  {:host (host)
   :path path
   :body data})

(defn sync-with-payment-gateway
  [data]
  (request-utils/http-post data))

(defn sync-with-payment-gateway-and-get-auth-token
  ([data]
   (sync-with-payment-gateway-and-get-auth-token {} data))
  ([context data]
  (go
    (let [dataa (-> (assoc data :verified false)
                   (assoc :return-supplier true)
                   (prepare-data-to-request path-to-sync-event))
          result (<! (sync-with-payment-gateway dataa))]
      result))))

(defn check-data-authenticity
  [context auth-token data]
  (if-let [verifies (:verify-cb context)]
    (verifies auth-token data)
    (go (let [result (<! (-> {:meo-wallet-api-key (get-in auth-token [:body :supplier :api-key])}
                       (meowallet/verify-callback data)))]
      (logger/info (str "verify response: " result))
      result))))

(defn sync-verified-with-payment
  [data]
  (let [result (request-utils/http-post data)]
    result))

(defn sync-verified
  [data]
  (-> (assoc data :verified true)
      (prepare-data-to-request path-to-verify)
      (sync-verified-with-payment)))

(defn run!
  [context data]
  (go
    (let [transformed-data (transform-data data)]
      (result/enforce-let [sync-response (<! (sync-with-payment-gateway-and-get-auth-token context transformed-data))
                           _ (<! (check-data-authenticity context sync-response data))]
        (<! (sync-verified (assoc transformed-data :id (get-in sync-response [:body :id]))))))))

(ns weareswat.meowallet-integration.handlers.notify-about-payment-test
  (:use clojure.test)
  (:require [weareswat.meowallet-integration.handlers.test-request :as test-request]
            [environ.core :refer [env]]
            [clj-meowallet.core :as meowallet]
            [weareswat.meowallet-integration.core.notify-about-payment :as notify-about-payment]
            [clojure.core.async :refer [go]]
            [request-utils.core :as request-utils]
            [result.core :as result]))

(deftest basic-test
  (testing "root/payment-reference/event returns OK"
    (with-redefs [notify-about-payment/sync-with-payment-gateway (fn [url] (go {:success true
                                                                                :status 200
                                                                                :body {:supplier {:token "faketoken"}}}))
                meowallet/verify-callback (fn [auth-token url]
                                            (go {:success true
                                                 :status 200}))
                notify-about-payment/sync-verified (fn [data] (go {:success true
                                                                   :status 200
                                                                   :data data}))]
      (let [body {:currency "EUR"
                  :amount 10
                  :event "COMPLETED"
                  :ext-customerid "00001"
                  :ext-email "noreply@sapo.pt"
                  :ext-invoiceid "38440200100"
                  :method "WALLET"
                  :operation_id "qwkjehqkjwhe"
                  :operation_status "COMPLETED"
                  :user "237"}
            response (test-request/parsed-response :post
                                                   "/payment-reference/event"
                                                   body)]
        (is (= 200 (:status response))))))

  (testing "/payment-reference/create returns OK"
    (with-redefs [request-utils/http-post (fn [url] {:success true
                                                     :status 200})
                 notify-about-payment/check-data-authenticity (fn [data]
                                                                (go {:success false
                                                                     :status 400}))]
      (let [body {:currency "EUR"
                  :amount 10
                  :event "COMPLETED"
                  :ext-customerid "00001"
                  :ext-email "noreply@sapo.pt"
                  :ext-invoiceid "38440200100"
                  :method "WALLET"
                  :operation_id "qwkjehqkjwhe"
                  :operation_status "COMPLETED"
                  :user "237"}
            response (test-request/parsed-response :post
                                                   "/payment-reference/create"
                                                   body)]
        (is (= 400 (:status response)))))
    ))

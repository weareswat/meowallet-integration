(ns weareswat.meowallet-integration.core.notify-about-payment-test
  (:use clojure.test)
  (:require [weareswat.meowallet-integration.core.notify-about-payment :as notify-about-payment]
            [clj-meowallet.core :as meowallet]
            [clojure.string :as clj-str]
            [clojure.core.async :refer [<!! go]]
            [environ.core :refer [env]]
            [result.core :as result]
            [request-utils.core :as request-utils]))

(deftest transform-data-test
  (let [input-data {:operation_status "COMPLETED"
                    :amount 10
                    :currency "EUR"
                    :operation_id "qwkjehqkjwhe"}
        result (notify-about-payment/transform-data input-data)]

    (testing "status"
      (is (= (clj-str/lower-case (:operation_status input-data))
             (:status result))))

    (testing "amount"
      (is (= (:amount input-data)
             (:amount result))))

    (testing "currency"
      (is (= (:currency input-data)
             (:currency result))))

    (testing "transaction_id"
      (is (= (:operation_id input-data)
             (:transaction-id result))))

    (testing "supplier-id"
      (is (= "MeoWallet"
             (:supplier-id result))))))

(deftest sync-with-payment-gateway-and-get-auth-token-test
  (with-redefs [request-utils/http-post (fn [url] (go {:success true
                                                       :status 200
                                                       :supplier {:token "faketoken"}}))]
    (let [result (<!! (notify-about-payment/sync-with-payment-gateway-and-get-auth-token {}))]
      (is (result/succeeded? result))
      (is (= "faketoken"
             (get-in result [:supplier :token]))))))

(deftest fail-to-check-data-authenticity-flow
  (with-redefs [notify-about-payment/sync-with-payment-gateway (fn [url] (go {:success true
                                                                              :status 200
                                                                              :supplier {:token "faketoken"}}))
                meowallet/verify-callback (fn [auth-token url]
                                            (go {:success false
                                                 :status 400
                                                 :token auth-token}))]
    (let [input-data {:currency "EUR"
                      :amount 10
                      :event "COMPLETED"
                      :ext-customerid "00001"
                      :ext-email "noreply@sapo.pt"
                      :ext-invoiceid "38440200100"
                      :method "WALLET"
                      :operation_id "qwkjehqkjwhe"
                      :operation_status "COMPLETED"
                      :user "237"}
          result (<!! (notify-about-payment/run! {} input-data))]

      (is (result/failed? result))

      (testing "should return 400"
        (is (= 400
               (:status result))))

      (testing "should have a properly formatted token"
        (is (= "faketoken"
               (get-in result [:token :meo-wallet-api-key]))))
      )))

(deftest notify-about-payment-test
  (with-redefs [notify-about-payment/sync-with-payment-gateway (fn [url] (go {:success true
                                                                              :status 200
                                                                              :body {:supplier {:token "faketoken"}}}))
                meowallet/verify-callback (fn [auth-token url]
                                            (go {:success true
                                                 :status 200}))
                notify-about-payment/sync-verified (fn [data] (go {:success true
                                                                   :status 200
                                                                   :data data}))]
    (let [input-data {:currency "EUR"
                      :amount 10
                      :event "COMPLETED"
                      :ext-customerid "00001"
                      :ext-email "noreply@sapo.pt"
                      :ext-invoiceid "38440200100"
                      :method "WALLET"
                      :operation_id "qwkjehqkjwhe"
                      :operation_status "COMPLETED"
                      :user "237"}

          result (<!! (notify-about-payment/run! {} input-data))]

        (is (result/succeeded? result))
        (is (= 200 (:status result))))))

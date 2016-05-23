(ns weareswat.meowallet-integration.core.notify-about-payment-test
  (:use clojure.test)
  (:require [weareswat.meowallet-integration.core.notify-about-payment :as notify-about-payment]
            [clojure.core.async :refer [<!! go]]
            [environ.core :refer [env]]
            [result.core :as result]
            [request-utils.core :as request-utils]))

(deftest transform-data-test
  (let [input-data {:operation-status "COMPLETED"
                    :amount 10
                    :currency "EUR"
                    :operation-id "qwkjehqkjwhe"}
        result (notify-about-payment/transform-data input-data)]

    (is (result/succeeded? result))

    (testing "status"
      (is (= (:operation-status input-data)
             (:status result))))

    (testing "amount"
      (is (= (:amount input-data)
             (:amount result))))

    (testing "currency"
      (is (= (:currency input-data)
             (:currency result))))

    (testing "transaction-id"
      (is (= (:operation-id input-data)
             (:transaction-id result))))

    (testing "provider-key"
      (is (= :meo-wallet
             (:provider-key result))))))

(deftest transform-data-test
  (with-redefs [request-utils/http-post (fn [url] {:success true
                                                   :status 200})
                notify-about-payment/check-data-authenticity (fn [data]
                                                               (go {:success true
                                                                    :status 200}))]
    (let [input-data {:currency "EUR"
                      :amount 10
                      :event "COMPLETED"
                      :ext-customerid "00001"
                      :ext-email "noreply@sapo.pt"
                      :ext-invoiceid "38440200100"
                      :method "WALLET"
                      :operation-id "qwkjehqkjwhe"
                      :operation-status "COMPLETED"
                      :user "237"}

          result (<!! (notify-about-payment/run! {} input-data))]

        (is (result/succeeded? result))
        (is (= 200 (:status result))))))

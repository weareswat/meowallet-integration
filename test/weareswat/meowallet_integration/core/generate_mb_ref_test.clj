(ns weareswat.meowallet-integration.core.generate-mb-ref-test
  (:use clojure.test)
  (:require [weareswat.meowallet-integration.core.generate-mb-ref :as generate-mb-ref]
            [result.core :as result]))

(deftest transform-input-data-test
  (let [input-data {:api-key "qwe23"
                    :amount 10
                    :currency "EUR"
                    :expires-at "2016-05-18T15:59:58+0000"}
        result (generate-mb-ref/transform-input-data input-data)]

    (testing "credentials"
      (is (= (:api-key input-data)
             (get-in result [:credentials :meo-wallet-api-key]))))

    (testing "amount"
      (is (= (:amount input-data)
             (get-in result [:data :amount]))))

    (testing "currency"
      (is (= (:currency input-data)
             (get-in result [:data :currency]))))

    (testing "expires"
      (is (= (:expires-at input-data)
             (get-in result [:data :expires]))))))


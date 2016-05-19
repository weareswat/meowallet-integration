(ns weareswat.meowallet-integration.core.generate-mb-ref-test
  (:use clojure.test)
  (:require [weareswat.meowallet-integration.core.generate-mb-ref :as generate-mb-ref]
            [clojure.core.async :refer [<!!]]
            [environ.core :refer [env]]
            [result.core :as result]))

(deftest transform-input-data-test
  (let [input-data {:supplier {:api-key "qwe23"}
                    :amount 10
                    :currency "EUR"
                    :expires-at "2016-05-18T15:59:58+0000"}
        result (generate-mb-ref/transform-input-data input-data)]

    (is (result/succeeded? result))

    (testing "credentials"
      (is (= (get-in input-data [:supplier :api-key])
             (get-in result [:credentials :meo-wallet-api-key]))))

    (testing "amount"
      (is (= (:amount input-data)
             (get-in result [:data :amount]))))

    (testing "currency"
      (is (= (:currency input-data)
             (get-in result [:data :currency]))))

    (testing "expires"
      (is (= (:expires-at input-data)
             (get-in result [:data :expired-at]))))))

(deftest transform-output-data-test
  (let [meo-result-data {:amount 10
                         :fee -0.62
                         :date "2016-05-17T15:36:25+0000"
                         :method "MB"
                         :amount_net 9.38
                         :requests 1
                         :channel "WEBSITE"
                         :type "PAYMENT"
                         :mb {:ref 243323013
                              :entity 90426}
                         :expires "2016-05-18T15:59:58+0000"
                         :currency "EUR"
                         :refundable false
                         :ext_invoiceid "i00001232"
                         :status "PENDING"
                         :id "33de099a-49f1-42a7-913f-761f2e83b673"
                         :items []
                         :merchant {:id 688892900
                                    :name "merchant-name"
                                    :email "merchant-email"}}
        result (generate-mb-ref/transform-output-data meo-result-data)]

    (is (result/succeeded? result))

    (testing "currency"
      (is (= (:currency meo-result-data)
             (:currency result))))

    (testing "expires"
      (is (= (:expires-at meo-result-data)
             (:expires-at result))))

    (testing "fee"
      (is (= (:fee meo-result-data)
             (:fee result))))

    (testing "date"
      (is (= (:date meo-result-data)
             (:created-at result))))

    (testing "payment-method"
      (is (= (:method meo-result-data)
             (:payment-method result))))

    (testing "transaction-id"
      (is (= (:id meo-result-data)
             (:transaction-id result))))

    (testing "status"
      (is (= (:status meo-result-data)
             (:status result))))

    (testing "mb"
      (testing "amount"
        (is (= (:amount meo-result-data)
               (get-in result [:mb :amount]))))

      (testing "ref"
        (is (= (get-in meo-result-data [:mb :ref])
               (get-in result [:mb :ref]))))

      (testing "entity"
        (is (= (get-in meo-result-data [:mb :entity])
               (get-in result [:mb :entity])))))))

(deftest generate-mb-ref
  (if-not (env :meo-wallet-api-key)
    (println "Warning: No meo wallet api key on env (ignoring test)")

    (let [input-data {:supplier {:api-key (env :meo-wallet-api-key)}
                      :amount 10
                      :currency "EUR"}
          result (<!! (generate-mb-ref/run {} input-data))]

      (is (result/succeeded? result))

      (testing "validate format result"

        (testing "payment-method"
          (is (= "MB"
                 (:payment-method result))))

        (testing "currency"
          (is (= (:currency input-data)
                 (:currency result))))

        (testing "transaction-id"
          (is (:transaction-id result)))

        (testing "created-at"
          (is (:created-at result)))

        (testing "fee"
          (is (:fee result)))

        (testing "status"
          (is (= "PENDING"
                 (:status result))))

        (testing "mb"
          (is (:mb result))

          (testing "ref"
            (is (get-in result [:mb :ref])))

          (testing "entity"
            (is (get-in result [:mb :entity])))

          (testing "amount"
            (is (get-in result [:mb :amount]))))))))

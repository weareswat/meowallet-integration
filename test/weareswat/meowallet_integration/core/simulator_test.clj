(ns weareswat.meowallet-integration.core.simulator-test
  (:use clojure.test)
  (:require [weareswat.meowallet-integration.core.simulator :as simulator]
            [weareswat.meowallet-integration.core.notify-about-payment :as notify-about-payment]
            [clj-meowallet.core :as meowallet]
            [clojure.core.async :refer [<!! go]]
            [environ.core :refer [env]]
            [result.core :as result]
            [request-utils.core :as request-utils]))

(deftest transform-data-test
  (let [input-data {:currency "EUR"
                    :mb {:amount 10}
                    :payment-method "MB"
                    :transaction-id "qwebqwje"}
        result (simulator/transform-data input-data)]

    (is (result/succeeded? result))

    (testing "status"
      (is (= (:operation-status input-data)
             (:status result))))

    (testing "amount"
      (is (= (get-in input-data [:mb :amount])
             (:amount result))))

    (testing "currency"
      (is (= (:currency input-data)
             (:currency result))))

    (testing "transaction-id"
      (is (= (:operation-id input-data)
             (:transaction-id result))))))

(deftest simulate-test
  (with-redefs [notify-about-payment/sync-with-payment-gateway (fn [url] (go {:success true
                                                                              :status 200
                                                                              :body {:supplier {:token "faketoken"}}}))
                notify-about-payment/sync-verified (fn [data] (go {:success true
                                                                   :status 200
                                                                   :data data}))]
    (let [user-input-data {:simulator {:expected-status "paid"}}
          mb-ref-data {:currency "EUR"
                       :mb {:amount 10}
                       :payment-method "MB"
                       :transaction-id "qwebqwje"}
          result (<!! (simulator/run! {} user-input-data mb-ref-data))]

      (is (result/succeeded? result))

      (testing "status"
        (is (= 200 (:status result)))))))

(ns weareswat.meowallet-integration.core.simulator-test
  (:use clojure.test)
  (:require [weareswat.meowallet-integration.core.simulator :as simulator]
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

(ns weareswat.meowallet-integration.handlers.generate-mb-ref-test
  (:use clojure.test)
  (:require [weareswat.meowallet-integration.handlers.test-request :as test-request]
            [environ.core :refer [env]]
            [result.core :as result]))

(deftest basic-test
  (testing "/payment-reference/create returns OK"
    (let [body {:supplier {:api-key (env :meo-wallet-api-key)}
                :amount 10
                :currency "EUR"}
          response (test-request/parsed-response :post "/payment-reference/create" body)
          body (:body response)]
      (is (:mb body))
      (is (= 200 (:status response)))))

  (testing "/payment-reference/create returns OK"
    (let [body {:amount 10
                :currency "EUR"}
          response (test-request/parsed-response :post "/payment-reference/create" body)
          body (:body response)]
      (is (= "missing-required-key"
             (:supplier body)))
      (is (= 400 (:status response)))))
  )

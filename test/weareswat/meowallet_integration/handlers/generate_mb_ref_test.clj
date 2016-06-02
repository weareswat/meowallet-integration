(ns weareswat.meowallet-integration.handlers.generate-mb-ref-test
  (:use clojure.test)
  (:require [weareswat.meowallet-integration.handlers.test-request :as test-request]
            [environ.core :refer [env]]
            [result.core :as result]))

(deftest basic-test
  (if-not (env :meo-wallet-api-key)
    (println "Warning: No meo wallet api key on env (ignoring test)")

    (do
      (testing "/payment-reference/create returns OK"
        (let [body {:supplier {:api-key (env :meo-wallet-api-key)}
                    :amount 10
                    :currency "EUR"}
              response (test-request/parsed-response :post "/payment-reference/create" body)
              body (:body response)]
          (is (:mb body))
          (is (= 200 (:status response)))))

      (testing "/payment-reference/create returns OK"
        (let [body {:supplier {:api-key "wqkjejkqw"}
                    :currency "EUR"}
              response (test-request/parsed-response :post "/payment-reference/create" body)
              body (:body response)]
          (is (= "missing-required-key"
                 (get-in body [:errors :amount])))
          (is (= 400 (:status response))))))))

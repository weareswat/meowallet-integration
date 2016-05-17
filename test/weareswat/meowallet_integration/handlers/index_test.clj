(ns weareswat.meowallet-integration.handlers.index-test
  (:use clojure.test)
  (:require [weareswat.meowallet-integration.handlers.test-request :as test-request]
            [result.core :as result]))

(deftest basic-test
  (testing "Index returns OK"
    (let [response (test-request/parsed-response :get "/")
          body (:body response)]
      (is (= 200 (:status response))))))

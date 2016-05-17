(ns weareswat.meowallet-integration.handlers.test-request
  (:use clojure.test)
  (:require [weareswat.meowallet-integration.http-component :as http]
            [com.stuartsierra.component :as component]
            [weareswat.meowallet-integration.test-system :as system]
            [clojure.edn :as edn]
            [ring.mock.request :as mock]))

(defn parsed-response
  "Gets the response for a HTTP request"
  [method path & [body-data]]
  (let [system (component/start (system/create))
        body-data (if (nil? body-data) nil (str body-data))
        response ((http/app system) (mock/request method path body-data))]
    (component/stop system)
    response))

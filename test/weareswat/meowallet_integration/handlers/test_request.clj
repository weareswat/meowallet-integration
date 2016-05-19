(ns weareswat.meowallet-integration.handlers.test-request
  (:use clojure.test)
  (:require [weareswat.meowallet-integration.http-component :as http]
            [com.stuartsierra.component :as component]
            [weareswat.meowallet-integration.test-system :as system]
            [cheshire.core :as json]
            [clojure.edn :as edn]
            [ring.mock.request :as mock]))

(defn ->edn [raw]
  (cond
    (coll? raw) raw
    (string? raw) (json/parse-string raw true)
    :else (json/parse-string (slurp raw) true)))

(defn parsed-response
  "Gets the response for a HTTP request"
  [method path & [body-data]]
  (let [system (component/start (system/create))
        body-data (if (nil? body-data) nil (json/generate-string body-data))
        response ((http/app system) (-> (mock/request method path body-data)
                                        (mock/content-type "application/json")))]
    (component/stop system)
    (assoc response :body (->edn (:body response)))))

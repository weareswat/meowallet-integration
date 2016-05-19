(ns weareswat.meowallet-integration.routes
  "Specifices the routes"
  (:require
    [compojure.core :as compojure :refer [GET POST]]
    [compojure.route :as route]
    [compojure.api.sweet :as sweet]
    [schema.core :as s]
    [result.core :as result]
    [weareswat.meowallet-integration.models.payment-reference-request :as prr]
    [weareswat.meowallet-integration.handlers.generate-mb-ref :as generate-mb-ref]
    [weareswat.meowallet-integration.handlers.index :as index]))

(def sweet-public-routes
  (sweet/routes
    (sweet/GET "/" request
               :return {:name s/Str
                        :version s/Str
                        :success s/Bool}
               :no-doc true
               :summary "Just an Heelo World endpoint"
               (index/handle request))
    (sweet/POST "/payment-reference/create" request
                :body [input prr/schema]
                :return {:success s/Bool
                         :fee s/Num
                         :created-at s/Any
                         :expires-at s/Any
                         :payment-method s/Str
                         :mb {:amount s/Num
                              :ref s/Any
                              :entity s/Any}
                         :currency s/Str
                         :status s/Str
                         :transaction-id s/Any}
                :summary "Request MEOWallet for MB Reference"
                :description "Returns MB Reference related data"
                (generate-mb-ref/handle request)
      ))
  )

(defn routes
  []
  (sweet/api
    {:swagger {:ui "/api-docs"
               :spec "/swagger.json"
               :options {:ui {:jsonEditor true}}
               :data {:produces ["application/json"],
                      :consumes ["application/json"],
                      :info {:title "MEO Wallet Integration API"
                             :description "This API has the endpoints used to integrate with
                                          WeAreSwat Payment Gateway and MeoWallet API"
                             :contact {:name "WeAreSwat Team"
                                       :email "developers@rupeal.com"
                                       :url "http://www.github.com/weareswat"}}}}}
    (sweet/routes
      sweet-public-routes)))

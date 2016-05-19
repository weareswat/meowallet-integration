(ns weareswat.meowallet-integration.routes
  "Specifices the routes"
  (:require
    [compojure.core :as compojure :refer [GET POST]]
    [compojure.route :as route]
    [result.core :as result]
    [weareswat.meowallet-integration.handlers.generate-mb-ref :as generate-mb-ref]
    [weareswat.meowallet-integration.handlers.index :as index]))

(compojure/defroutes public-routes
  "The routes available to be served, that don't need auth"
  (GET "/" request (index/handle request))
  (POST "/payment-reference/create" request (generate-mb-ref/handle request))
  (route/not-found (result/failure {:error "not-found"})))

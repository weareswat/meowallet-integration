(ns weareswat.meowallet-integration.http-component
  "The aleph server as a component"
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [clojure.core.async :as async]
            [taoensso.timbre :as logger]
            [ring.middleware.cors :as cors]
            [ring.middleware.json :refer [wrap-json-response]]
            [aleph.http :as http]
            [compojure.core :as compojure]
            [weareswat.meowallet-integration.routes :as routes]))

(defn- setup-cors
  "Setup cors"
  [handler]
  (cors/wrap-cors handler
                  :access-control-allow-origin
                  [#"^http://localhost(.*)"
                   #"^http?://(.*)meo-wallet-integration-staging.herokuapp.com(.*)"
                   #"(.*)cloudapp.net(.*)"]
                  :access-control-allow-methods [:get :put :post :delete]))

(defn- set-system
  "Adds the system to the request data"
  [f system]
  (fn [request]
    (f (assoc request :system system))))

(defn content-type
  [f]
  (fn [request]
    (f (->> (:headers request)
            (merge {"content-type" "application/json"})
            (assoc request :headers)))))

(defn app
  "The main app handler"
  [system]
  (-> (routes/routes)
      (set-system system)
      (content-type)
      (wrap-json-response)
      (setup-cors)))

(defrecord HttpServerComponent [server port meta closed-ch web-app]

  component/Lifecycle

  (start [component]
    (logger/info (str "** WeAreSWAT Meo Wallet Integration Server " (or (get (System/getenv) "ENV" "development"))
                      " running on port " port))
    (assoc component :server (http/start-server (app web-app) {:port port})
           :closed-ch (async/chan)))

  (stop [component]
    (.close server)
    (async/>!! closed-ch {:http-server :closed})
    (dissoc component server)))

(defn get-port
  []
  (Integer/parseInt (or (env :port) (env :meowallet-integration-port) "5050")))

(defn create
  "Creates a new http component"
  []
  (component/using
    (map->HttpServerComponent {:port (get-port)})
    [:web-app]))

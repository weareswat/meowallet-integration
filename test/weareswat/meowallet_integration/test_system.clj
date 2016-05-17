(ns weareswat.meowallet-integration.test-system
  "A specific system just for tests"
  (:require [com.stuartsierra.component :as component]
            [weareswat.meowallet-integration.system :as system]))

(defn create
  "Creates a new system to execute the app"
  []
  (dissoc (system/create) :http-server))

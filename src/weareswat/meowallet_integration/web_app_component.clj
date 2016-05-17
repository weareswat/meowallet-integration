(ns weareswat.meowallet-integration.web-app-component
  "Gathers all deps required by the web app component"
  (:require [com.stuartsierra.component :as component]
            [clojure.core.async :as async]))

(defrecord WebAppComponent [])

(defn create
  "Creates the WebAppComponent"
  []
  (component/using (map->WebAppComponent {})
                   []))

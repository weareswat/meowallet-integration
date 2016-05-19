(defproject weareswat/meowallet-integration "0.1.0"
  :description "Meowallet integration"
  :url "https://github.com/weareswat/meowallet-integration"

  :license {:name         "MIT"
            :distribution :repo}

  :min-lein-version "2.6.0"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.2.374"]
                 [weareswat/clj-meowallet "0.3.0"]
                 [prismatic/schema "1.1.1"]
                 [com.taoensso/timbre "4.3.1"]
                 [prismatic/schema "1.1.1"]
                 [compojure "1.5.0"]
                 [ring-cors "0.1.7"]
                 [ring/ring-json "0.4.0"]
                 [aleph "0.4.1"]
                 [environ "1.0.3"]

                 [com.stuartsierra/component "0.3.1"]
                 [walmartlabs/system-viz "0.1.1"]
                 [clanhr/result "0.11.0"]
                 [clanhr/reply "1.0.0"]]

  :aliases {"server"  ["trampoline" "run" "-m" "weareswat.meowallet-integration.core/-main"]
            "system-viz" ["run" "-m" "weareswat.meowallet-integration.system/-main"]
            "autotest" ["trampoline" "with-profile" "+test" "test-refresh"]
            "test"  ["trampoline" "test"]}

  :scm {:name "git"
        :url "git@github.com:weareswat/meowallet-integration.git"}

  :profiles {:test {:dependencies [[ring/ring-mock "0.3.0"]
                                   [org.clojure/tools.namespace "0.2.11"]]

                    :plugins [[com.jakemccrary/lein-test-refresh "0.15.0"]
                              [lein-cloverage "1.0.2"]]}}
  
  :test-refresh {:quiet true
                 :changes-only true})

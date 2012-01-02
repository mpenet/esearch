(defproject clj-esearch "0.3.0"

  :description "Elastic Search REST client for Clojure"

  :dependencies [[org.clojure/clojure "1.2.1"]
                 [cheshire "2.0.4"]
                 [aleph "0.2.0"]]

  :dev-dependencies [[swank-clojure "1.3.4" :exclusions
                      [org.clojure/clojure org.clojure/clojure-contrib]]
                     [clojure-source "1.2.0"]])

(defproject clj-esearch "0.1.0"

  :description "Elastic Search REST client for Clojure"

  :repositories { "sonatype.org" "http://oss.sonatype.org/content/repositories/releases/" }

  :dependencies [[org.clojure/clojure "1.2.0"]
                 [aleph "0.2.0-beta2"]]

  :dev-dependencies [[swank-clojure "1.4.0-SNAPSHOT" :exclusions
                      [org.clojure/clojure org.clojure/clojure-contrib]]
                     [clojure-source "1.2.0"]
                     [com.fxtlabs/autodoc "0.8.0-SNAPSHOT"
                      :exclusions [org.clojure/clojure
                                   org.clojure/clojure-contrib]]])

(defproject elaticlj "1.0.0-SNAPSHOT"
  :description "FIXME: write description"

  :dependencies [[org.clojure/clojure "1.2.0"]
                 ;; [org.clojure/clojure-contrib "1.2.0"]

                 [record-utils "1.0.0"]
                 [aleph "0.1.5-SNAPSHOT"]
                 [clj-http "0.1.2"]]

  :dev-dependencies [[swank-clojure "1.4.0-SNAPSHOT" :exclusions
                      [org.clojure/clojure org.clojure/clojure-contrib]]
                     [clojure-source "1.2.0"]
                     [com.fxtlabs/autodoc "0.8.0-SNAPSHOT"
                      :exclusions [org.clojure/clojure
                                   org.clojure/clojure-contrib]]])

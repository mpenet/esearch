(ns qbits.esearch.utils
  (:require
   [clojure.string :as string]
   [aleph.http :as aleph]))

(defprotocol URLBuilder
  (encode [value]))

(extend-protocol URLBuilder

  clojure.lang.Sequential
  (encode [value]
    (string/join "," (map encode value)))

  clojure.lang.Keyword
  (encode [value]
    (name value))

  Object
  (encode [value] value))

(defn url
  [& parts]
  (->> parts
       (filter identity)
       (map encode)
       (string/join "/")))

(defn request
  [request-params]
  (aleph/http-request
   (merge
    {:headers {"content-type" "application/json"}
     :keep-alive false
     :auto-transform true}
    request-params)))
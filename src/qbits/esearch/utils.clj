(ns qbits.esearch.utils
  (:require
   [clojure.string :as string]
   [clojure.core.async :as async]
   [cheshire.core :as json]
   [qbits.jet.client.http :as http]))

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

(defonce client
  (delay
   (http/client {:request-buffer-size (* 4096 1024)
                 :response-buffer-size (* 4096 1024)})))

(defn request

  [request-params]
  (http/request @client
                (merge {:headers {"Content-Type" "application/json; charset=UTF-8"}
                        :as :json
                        :fold-chunked-response? true}
                       request-params)))

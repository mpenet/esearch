(ns qbits.esearch.utils
  (:require
   [clojure.string :as string]
   [clojure.core.async :as async]
   [cheshire.core :as json]
   [org.httpkit.client :as http]))

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
  (let [ch (async/chan)]
    (http/request
     (merge
      {:headers {"content-type" "application/json; charset=UTF-8"}
       :keep-alive -1}
      request-params)
     (fn [{:keys [status headers body error opts]
           :as response}]
       (async/put! ch (update-in response [:body] #(json/parse-string % true)))
       (async/close! ch)))
    ch))

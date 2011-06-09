(ns elaticlj.core
  (:use [aleph.http :only [sync-http-request http-request]]
        [lamina.core :only [closed-channel]]
        [clojure.contrib.json :only [json-str]]))

(defn url
  "Build urls from server index type instance params"
  [& parts]
  (apply str (interpose "/" parts)))

(defn query
  [params]
  (merge {:headers {"content-type" "application/json"}
          :auto-transform true}
         params))

(defn request
  [params & [async]]
  (apply (if async http-request sync-http-request)
         [(query params)]))

(defn add-doc
  [{:keys [server index type doc id query-string async debug]}]
  (let [params {:method :post
                :url (apply url (if id
                                  [server index type id]
                                  [server index type]))
                :query-string query-string
                :body (closed-channel doc)}]
    (request params async)))

(defn get-doc
  [{:keys [server index type doc id  query-string async debug]}]
  (let [params {:method :get
                :url (url server index type id)
                :query-string query-string}]
    (request params async)))

(defn delete-doc
  [{:keys [server index type doc id  query-string async debug]}]
  (let [params {:method :delete
                :url (url server index type id)
                :query-string query-string}]
    (request params async)))

(defn search-doc
  [{:keys [server index type search-query query-string async debug]}]
  (let [params {:method :get
                :url (url server index "_search")
                :query-string query-string
                :body (closed-channel search-query)}]
    (request params async)))

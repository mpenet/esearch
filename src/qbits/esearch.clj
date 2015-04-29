(ns qbits.esearch
  "Elastic-search HTTP client"
  (:require
   [cheshire.core :as json]
   [qbits.esearch.utils :as utils]))

(defn add-doc
  [client server index type doc & {:keys [id query-params]}]
  (utils/request client
                 {:method :post
                  :url (utils/url server index type id)
                  :body (json/generate-string doc)}))

(defn get-doc
  [client server index type id & {:keys [query-params]}]
  (utils/request client
                 {:method :get
                  :query-params query-params
                  :url (utils/url server index type id)}))

(defn mget-doc
  [client server query & {:keys [index type query-params]}]
  (utils/request client
                 {:method :get
                  :query-params query-params
                  :url (utils/url server index type "_mget")
                  :body (json/generate-string query)}))

(defn update-doc
  [client server query & {:keys [index type query-params]}]
  (utils/request client
                 {:method :delete
                  :url (utils/url server index type "_update")
                  :query-params query-params
                  :body (json/generate-string query)}))

(defn delete-doc
  [client server index type id & {:keys [query-params]}]
  (utils/request client
                 {:method :delete
                  :url (utils/url server index type id)
                  :query-params query-params}))

(defn delete-by-query
  [client server query & {:keys [index type query-params]}]
  (utils/request client
                 {:method :delete
                  :url (utils/url server index type "_query")
                  :query-params query-params
                  :body (json/generate-string query)}))

(defn search-doc
  [client server search-query & {:keys [index type query-params]}]
  (utils/request client
                 {:method :get
                  :url (utils/url server index type "_search")
                  :query-params query-params
                  :body (json/generate-string search-query)}))

(defn percolate
  [client server index name query & {:keys [query-params]}]
  (utils/request client
                 {:method :put
                  :url (utils/url server ".percolator" index name)
                  :query-params query-params
                  :body (json/generate-string query)}))

(defn count-docs
  [client server query & {:keys [index type query-params]}]
  (utils/request client
                 {:method :get
                  :url (utils/url server index type  "_count")
                  :query-params query-params
                  :body (json/generate-string query)}))

(defn bulk
  [client server bulk-lines & {:keys [query-params]}]
  (utils/request client
                 {:method :put
                  :url (utils/url server "_bulk")
                  :query-params query-params
                  :auto-transform false
                  :headers {"content-type" "text/plain"}
                  :body (->> bulk-lines
                             (map #(str (json/generate-string %) "\n"))
                             (apply str))}))

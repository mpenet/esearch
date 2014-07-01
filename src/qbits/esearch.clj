(ns qbits.esearch
  "Elastic-search HTTP client"
  (:require
   [cheshire.core :as json]
   [qbits.esearch.utils :as utils]))

(defn add-doc
  [server index type doc & {:keys [id query-params]}]
  (utils/request {:method :post
                  :url (utils/url server index type id)
                  :body (json/generate-string doc)}))

(defn get-doc
  [server index type id & {:keys [query-params]}]
  (utils/request {:method :get
                  :query-params query-params
                  :url (utils/url server index type id)}))

(defn mget-doc
  [server query & {:keys [index type query-params]}]
  (utils/request {:method :get
                  :query-params query-params
                  :url (utils/url server index type "_mget")
                  :body (json/generate-string query)}))

(defn update-doc
  [server query & {:keys [index type query-params]}]
  (utils/request {:method :delete
                  :url (utils/url server index type "_update")
                  :query-params query-params
                  :body (json/generate-string query)}))

(defn delete-doc
  [server index type id & {:keys [query-params]}]
  (utils/request {:method :delete
                  :url (utils/url server index type id)
                  :query-params query-params}))

(defn delete-by-query
  [server query & {:keys [index type query-params]}]
  (utils/request {:method :delete
                  :url (utils/url server index type "_query")
                  :query-params query-params
                  :body (json/generate-string query)}))

(defn search-doc
  [server search-query & {:keys [index type query-params]}]
  (utils/request {:method :get
                  :url (utils/url server index type "_search")
                  :query-params query-params
                  :body (json/generate-string search-query)}))

(defn percolate
  [server index name query & {:keys [query-params]}]
  (utils/request {:method :put
                  :url (utils/url server ".percolator" index name)
                  :query-params query-params
                  :body (json/generate-string query)}))

(defn count-docs
  [server query & {:keys [index type query-params]}]
  (utils/request {:method :get
                  :url (utils/url server index type  "_count")
                  :query-params query-params
                  :body (json/generate-string query)}))

(defn bulk
  [server bulk-lines & {:keys [query-params]}]
  (utils/request {:method :put
                  :url (utils/url server "_bulk")
                  :query-params query-params
                  :auto-transform false
                  :headers {"content-type" "text/plain"}
                  :body (->> bulk-lines
                             (map #(str (json/generate-string %) "\n"))
                             (apply str))}))

(ns clj-esearch.core
  "Elastic-search HTTP client"
  (:require [aleph.http :as aleph-h]
            [cheshire.core :as json]
            [clojure.string :as clj-str]))

(defn url
  "Build urls by joining params with /
   Treats vector args as multi params separated by ,"
  [& parts]
  (->> parts
       (filter identity)
       (map #(cond (sequential? %)
                   (clj-str/join "," %)
                   (keyword? %)
                   (name %)
                   :else %))
       (clj-str/join "/")))

(defn request
  [request-params]
  (aleph-h/http-request
   (merge
    {:headers {"content-type" "application/json"}
     :keep-alive false
     :auto-transform true}
    request-params)))

(defn add-doc
  [server index type doc & {:keys [id query-params]}]
  (request {:method :post
            :url (url server index type id)
            :body (json/generate-string doc)}))

(defn get-doc
  [server index type id & {:keys [query-params]}]
  (request {:method :get
            :url (url server index type id)}))

(defn update-doc
  [server query & {:keys [index type query-params]}]
  (request {:method :delete
            :url (url server index type "_update")
            :query-params query-params
            :body (json/generate-string query)}))

(defn delete-doc
  [server index type id & {:keys [query-params]}]
  (request {:method :delete
            :url (url server index type id)
            :query-params query-params}))

(defn delete-by-query
  [server query & {:keys [index type query-params]}]
  (request {:method :delete
            :url (url server index type "_query")
            :query-params query-params
            :body (json/generate-string query)}))

(defn search-doc
  [server search-query & {:keys [index type query-params]}]
  (request {:method :get
            :url (url server index type  "_search")
            :query-params query-params
            :body (json/generate-string search-query)}))

(defn percolate
  [server index name query & {:keys [query-params]}]
  (request {:method :put
            :url (url server "_percolator" index name)
            :query-params query-params
            :body (json/generate-string query)}))

(defn count-docs
  [server query & {:keys [index type query-params]}]
  (request {:method :get
            :url (url server index type  "_count")
            :query-params query-params
            :body (json/generate-string query)}))

(defn bulk
  [server bulk-lines & {:keys [query-params]}]
  (request {:method :put
            :url (url server "_bulk")
            :query-params query-params
            :auto-transform false
            :headers {"content-type" "text/plain"}
            :body (->> bulk-lines
                       (map #(str (json/generate-string %) "\n"))
                       (apply str))}))

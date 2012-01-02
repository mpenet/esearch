(ns clj-esearch.core
  "Elastic-search HTTP client"
  (:use [aleph.http :only [sync-http-request http-request]])
  (:require [cheshire.core :as json]))

(defn str-join [value separator]
  (apply str (interpose separator value)))

(defn url
  "Build urls by joining params with /
   Treats vector args as multi params separated by ,"
  [& parts]
  (str-join (->> parts
                 (remove nil?)
                 (map #(cond (sequential? %)
                             (str-join % ",")
                             (keyword? %)
                             (name %)
                             :else %)))
            "/"))

(defn query
  [params]
  (merge
   {:headers {"content-type" "application/json"}
    :keep-alive false
    :auto-transform true}
   params))

(defn request
  "Forwards the ring requests settings to the appropriate request client"
  [params async]
  (let [query-params (query params)]
    (if async
      (http-request query-params)
      (sync-http-request query-params))))

(defn add-doc
  [server index type doc & {:keys [id query-string async]}]
  (request {:method :post
            :url (url server index type id)
            :query-string query-string
            :body (json/generate-string doc)}
           async))

(defn get-doc
  [server index type id & {:keys [query-string async]}]
  (request {:method :get
            :url (url server index type id)
            :query-string query-string}
           async))

(defn delete-doc
  [server index type id & {:keys [query-string async]}]
  (request {:method :delete
            :url (url server index type id)
            :query-string query-string}
           async))

(defn delete-by-query
  [server delete-query & {:keys [index type query-string async]}]
  (request {:method :delete
            :url (url server index type "_query")
            :query-string query-string
            :body (json/generate-string delete-query)}
           async))

(defn search-doc
  [server search-query & {:keys [index type query-string async]}]
  (request {:method :get
            :url (url server index type  "_search")
            :query-string query-string
            :body (json/generate-string search-query)}
           async))

(defn percolate
  [server index name percolator-query & {:keys [query-string async]}]
  (request {:method :put
            :url (url server "_percolator" index name)
            :query-string query-string
            :body (json/generate-string percolator-query)}
           async))

(defn count-docs
  [server count-query & {:keys [index type query-string async]}]
  (request {:method :get
            :url (url server index type  "_count")
            :query-string query-string
            :body (json/generate-string count-query)}
           async))

(defn bulk
  [server bulk-lines & {:keys [query-string async]}]
  (request {:method :put
            :url (url server "_bulk")
            :query-string query-string
            :auto-transform false
            :headers {"content-type" "text/plain"}
            :body (->> bulk-lines
                       (map #(str (json/generate-string %) "\n"))
                       (apply str))}
           async))

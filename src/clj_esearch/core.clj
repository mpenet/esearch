(ns clj-esearch.core
  (:use [aleph.http :only [sync-http-request http-request]]
        [lamina.core :only [closed-channel]]
        [clojure.contrib.json :only [json-str]]))

(defn str-join [value separator]
  (apply str (interpose separator value)))

(defn url
  "Build urls by joining params with /
   Treats vector args as multi params separated by ,
   :all as _all"
  [& parts]
  (str-join (->> parts
                 (remove nil?)
                 (map #(cond (sequential? %)
                             (str-join % ",")
                             (= :all %)
                             "_all"
                             :else %)))
            "/"))

(defn query
  [params]
  (assoc params
    :headers {"content-type" "application/json"}
;    :keep-alive? true
    :auto-transform true))

(defn request
  "Forwards the ring requests settings to the appropriate request client"
  [params async]
  ((if async http-request sync-http-request) (query params)))

(defn add-doc
  [server index type doc & {:keys [id query-string async]}]
  (request {:method :post
            :url (url server index type id)
            :query-string query-string
            :body (closed-channel doc)}
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
            :body (closed-channel delete-query)}
           async))

(defn search-doc
  [server search-query & {:keys [index type query-string async]}]
  (request {:method :get
            :url (url server index type  "_search")
            :query-string query-string
            :body (closed-channel search-query)}
           async))

(defn percolate
  [server index name percolator-query & {:keys [query-string async]}]
  (request {:method :put
            :url (url server "_percolator" index name)
            :query-string query-string
            :body (closed-channel percolator-query)}
           async))

(defn count-docs
  [server count-query & {:keys [index type query-string async]}]
  (request {:method :get
            :url (url server index type  "_count")
            :query-string query-string
            :body (closed-channel count-query)}
           async))

(defn bulk
  [server item-coll & {:keys [query-string async]}]
  (request {:method :put
            :url (url server "_bulk")
            :query-string query-string
            :body (str-join (map #(json-str %) item-coll) "\n")}
           async))



(ns elaticlj.direct
  (:use [clojure.contrib.json :only [json-str read-json]])
  (:import [org.elasticsearch.node NodeBuilder]
           [org.elasticsearch.common.xcontent XContentFactory]
           [org.elasticsearch.node.internal InternalNode]
           [org.elasticsearch.client.node NodeClient]
           [org.elasticsearch.client.transport TransportClient]
           [org.elasticsearch.common.transport InetSocketTransportAddress]
           [org.elasticsearch.common.settings ImmutableSettings]
           [org.elasticsearch.common.xcontent XContentFactory]
           [org.elasticsearch.action.index IndexResponse]))

(defrecord AddResponse [version type matches index id])
(defrecord GetResponse [index type id version exists source fields])

(defn response->get-response
  [response]
  (GetResponse.
   (. response index)
   (. response type)
   (. response id)
   (. response version)
   (. response exists)
   (-> response (.sourceAsString) read-json)
   (. response fields)))

(defn response->add-response
  [response]
  (AddResponse.
   (. response version)
   (. response type)
   (. response matches)
   (. response index)
   (. response id)))

(defn ^InternalNode make-node
  [& [{:keys [opt-client opt-data opt-local]}]]
  (let [node-builder (NodeBuilder.)]
    (when opt-client
      (. node-builder client opt-client))
    (when opt-client
      (. node-builder data opt-data))
    (when opt-local
      (. node-builder local opt-local))
    (. node-builder node)))

(defn ^NodeClient make-client
  [node]
  (.client node))

(defn ^TransportClient make-transport-client
  [& [{:keys [hosts sniff]}]]
  (cond sniff
        (TransportClient. (. (ImmutableSettings/settingsBuilder) put "client.transport.sniff" true))
        hosts
        (let [client (TransportClient.)]
          (doseq [[host port] hosts]
            (.addTransportAddress client (InetSocketTransportAddress. host port)))
          client)))

(defn add-doc
  [client index type & xs]
  (let [doc (last xs)
        doc-id (when (= 2 (count xs)) (first xs))]
    (-> (.setSource
          (if doc-id
            (.prepareIndex client index type doc-id)
            (.prepareIndex client index type))
          (-> doc json-str (.getBytes)))
         (.execute)
         (.actionGet)
         response->add-response)))

(defn get-doc
  [client index type id]

  (println   (->  client
       (.prepareGet index type id)
       (.execute)))

  (->  client
       (.prepareGet index type id)
       (.execute)
       (.actionGet)
       response->get-response))









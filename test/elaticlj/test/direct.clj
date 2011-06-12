(ns elaticlj.test.direct
  (:use [elaticlj.direct]
        [record-utils.core :only [def-record]]
        [lamina.core :only [enqueue enqueue-and-close named-channel
                            receive run-pipeline wait-for-result
                            read-channel]]
        [aleph.formats :only [byte-buffer->string]]
        [clojure.test])
  (:import [elaticlj.direct AddResponse GetResponse]
           [org.elasticsearch.node.internal InternalNode]
           [org.elasticsearch.client.node NodeClient]
           [org.elasticsearch.client.transport TransportClient]
           [org.elasticsearch.action.index IndexResponse]))

(def test-server "http://localhost:9200")
(def test-index "test-index")
(def test-type "test-type")
;; (def test-node (make-node))
;; (def test-client (make-client test-node))
(def test-transport-client (make-transport-client {:hosts [["localhost" 9300]]}))

(def-record Doc [title posted content])
(def test-doc (make-doc {:title "foo" :posted 12345 :content "bar"}))

(use-fixtures :each (fn [atest]
                      (atest)))

;; (deftest create-node-test
;;   (is (= InternalNode (type test-node))))

;; (deftest create-client-test
;;   (is (= NodeClient (type test-client))))

(deftest create-transport-client
  (is (= TransportClient (type test-transport-client))))

(deftest add-doc-test
  (is (= AddResponse (type (add-doc test-transport-client  test-index test-type test-doc))))
  (is (= AddResponse (type (add-doc test-transport-client test-index test-type "1" test-doc)))))

(deftest test-get-doc
  (let [doc (add-doc test-transport-client test-index test-type "1" test-doc)]
    (is (= "1" (-> (get-doc test-transport-client test-index test-type "1") :id)))))











(ns elaticlj.test.core
  (:use [elaticlj.core]
        [record-utils.core :only [def-record]]
        [lamina.core :only [enqueue enqueue-and-close named-channel
                            receive run-pipeline wait-for-result
                            read-channel]]
        [aleph.formats :only [byte-buffer->string]]
        [clojure.test]))

(def test-server "http://localhost:9200")
(def test-index "test-index")
(def test-type "test-type")

(def-record Doc [title posted content])
(def test-doc (make-doc {:title "foo" :posted 12345 :content "bar"}))

(use-fixtures :each (fn [atest]
                     (atest)
                     (request {:method :post
                               :url (url test-server test-index "_flush")})))
(deftest add-doc-test
  (let [response (add-doc {:server test-server
                           :index test-index
                           :type test-type
                           :doc test-doc})]
    (is (= 201 (:status response)))))

(deftest get-doc-test
  (let [doc (add-doc {:server test-server
                      :index test-index
                      :type test-type
                      :doc test-doc
                      :id 1})
        response (get-doc {:server test-server :index test-index
                           :type test-type :id 1})]
    (is (= 200 (:status response)))))

(deftest delete-test
    (let [doc (add-doc {:server test-server
                        :index test-index
                        :type test-type
                        :doc (assoc test-doc :content "prout")
                        :id 3})]
      (is (=  200 (:status (delete-doc {:server test-server :index test-index :type test-type :id 3}))))))

(deftest search-doc-test
  (let [doc (add-doc {:server test-server
                      :index test-index
                      :type test-type
                      :doc (assoc test-doc :content "prout")
                      :id 2})
        response (search-doc {:server test-server :index test-index :type test-type
                              :query-string {:pretty true}
                              :search-query {:query {:term {:content "prout"}}}})]
      (is (= 200 (:status response)))
      (is (= 1 (-> response :body :hits :hits count)))))


(deftest search-async-test
  (let [doc (add-doc {:server test-server
                      :index test-index
                      :type test-type
                      :doc (assoc test-doc :content "prout")
                      :id 2})
        response (wait-for-result (search-doc {:server test-server
                                               :index test-index :type test-type
                                               :query-string {:pretty true}
                                               :async true
                                               :search-query
                                               {:query {:term {:content "prout"}}}}))]
    (is (= 200 (:status response)))
    (is (= 1 (-> response :body :hits :hits count)))))


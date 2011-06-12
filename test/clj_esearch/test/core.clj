(ns clj-esearch.test.core
  (:use [clj-esearch.core]
        [lamina.core :only [wait-for-result]]
        [clojure.test]))

(def test-server "http://127.0.0.1:9200")
(def test-index "test-index")
(def test-type "test-type")

(defrecord Doc [title posted content])
(def test-doc (Doc. "foo" 12345 "bar"))
(def test-doc2 (Doc. "foo bar"  123456  "bar baz"))

(use-fixtures :each (fn [atest]
                     (atest)
                     (request {:method :post
                               :url (url test-server test-index "_flush")} nil)))
(deftest add-doc-test
  (let [response (add-doc test-server test-index test-type test-doc)]
  (is (= 201 (:status response)))))

(deftest get-doc-test
  (let [doc (add-doc test-server
                     test-index
                     test-type
                     test-doc
                     :id 1)
        response (get-doc test-server test-index test-type 1)]
    (is (= 200 (:status response)))))

(deftest delete-test
  (let [doc (add-doc test-server
                     test-index
                     test-type
                     (assoc test-doc :content "prout")
                     :id 3)]
      (is (=  200 (:status (delete-doc test-server test-index test-type 3))))))

(deftest search-doc-test
  (let [doc (add-doc test-server
                      test-index
                      test-type
                      (assoc test-doc :content "prout")
                      :id 2)
        doc2 (add-doc test-server
                      "test-index2"
                      test-type
                      (assoc test-doc :content "prout")
                      :id 2)
        response (search-doc test-server
                             {:query {:term {:content "prout"}}}
                             :index [test-index "test-index2"]
                             :type test-type
                             :query-string {:pretty true})]
    (is (= 200 (:status response)))
    (is (= 2 (-> response :body :hits :hits count)))))

(deftest search-async-test
  (let [doc (add-doc test-server
                     test-index
                     test-type
                     (assoc test-doc :content "prout")
                      :id 2)
        response (wait-for-result (search-doc test-server
                                              {:query {:term {:content "prout"}}}
                                              :index test-index
                                              :type test-type
                                              :async true))]
    (is (= 200 (:status response)))
    (is (= 1 (-> response :body :hits :hits count)))))

(deftest percolate-test
  (is (= 200 (:status (percolate test-server
                                 test-index
                                 "perc-test"
                                 {:query {:term {:field1 "value1" }}})))))
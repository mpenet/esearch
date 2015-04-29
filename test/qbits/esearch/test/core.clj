(ns qbits.esearch.test.core
  (:use [qbits.esearch]
        [clojure.test])
  (:require [qbits.jet.client.http :as http]
            [clojure.core.async :refer [<!!]]
            [qbits.esearch.utils :as utils]))

(def test-server "http://127.0.0.1:9200")
(def test-index "titems")
(def test-type "titem")

(def client (http/client))

(defrecord Doc [title posted content])
(def test-doc (Doc. "foo" 12345 "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))

(use-fixtures :each (fn [atest]
                      (<!! (utils/request client {:method :delete
                                                  :url (utils/url test-server test-index test-type)}))
                      (<!! (utils/request client {:method :post
                                                  :url (utils/url test-server test-index "_flush")}))
                      (<!! (utils/request client {:method :post
                                                  :url (utils/url test-server "_cache" "clear")}))
                      (atest)))

(deftest url-generation-test
  (is (= "http://127.0.0.1:9200/a/b/c") (utils/url test-server "a" "b" "c"))
  (is (= "http://127.0.0.1:9200/a/b/c") (utils/url test-server "a" :b  :c))
  (is (= "http://127.0.0.1:9200/a/b,c") (utils/url test-server "a" ["b" "c"]))
  (is (= "http://127.0.0.1:9200/a/b,c") (utils/url test-server "a" [:b :c]))
  (is (= "http://127.0.0.1:9200/a,b/c") (utils/url test-server ["a" "b"] "c"))
  (is (= "http://127.0.0.1:9200/_all/b/c") (utils/url test-server :_all "b" "c")))

(deftest add-doc-test
  (let [response (<!! (add-doc client test-server test-index test-type test-doc))]
    (is (= 201 (:status response)))))

(deftest get-doc-test
  (add-doc client
           test-server
           test-index
           test-type
           test-doc
           :id 1)
  (Thread/sleep 1000)
  (let [response (<!! (get-doc client test-server test-index test-type 1))]
    (is (= 200 (:status response)))))

(deftest delete-test
  (let [doc (<!! (add-doc client
                          test-server
                          test-index
                          test-type
                          test-doc
                          :id 3))]
    (is (=  200 (:status (<!! (delete-doc client test-server test-index test-type 3)))))))

(deftest search-doc-test
  (dotimes [i 3]
    (<!! (add-doc client
                  test-server
                  test-index
                  test-type
                  test-doc)))
  (Thread/sleep 1000)
  (let [response (<!! (search-doc client
                                  test-server
                                  {:query {:term {:title "foo"}}}
                                  :index test-index))]
    (is (= 200 (:status response)))
    (is (= 3 (-> response :body <!! :hits :hits count)))))

(deftest percolate-test
  (is (>= 201 (:status (<!! (percolate client
                                       test-server
                                       test-index
                                       "perc-test"
                                       {:query {:term {:field1 "value1" }}}))))))

(deftest bulk-test
  (is (>= 201 (:status (<!! (bulk client
                                  test-server
                                  [{:index {:_index "test-index" :_type "test-type" :_id "foo"}}
                                   {:foo "bar" :lorem "ipsum"}]))))))

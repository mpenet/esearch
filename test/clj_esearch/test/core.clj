(ns clj-esearch.test.core
  (:use [clj-esearch.core]
        [lamina.core :only [wait-for-result]]
        [clojure.test]))

(def test-server "http://127.0.0.1:9200")
(def test-index "titems")
(def test-type "titem")

(defrecord Doc [title posted content])
(def test-doc (Doc. "foo" 12345 "bar"))

(use-fixtures :each (fn [atest]
                      (request {:method :delete
                                :url (url test-server test-index test-type)} nil)
                      (request {:method :post
                                :url (url test-server test-index "_flush")} nil)
                      (request {:method :post
                                :url (url test-server "_cache" "clear")} nil)
                      (atest)))

(deftest url-generation-test
  (is (= "http://127.0.0.1:9200/a/b/c") (url test-server "a" "b" "c"))
  (is (= "http://127.0.0.1:9200/a/b/c") (url test-server "a" :b  :c))
  (is (= "http://127.0.0.1:9200/a/b,c") (url test-server "a" ["b" "c"]))
  (is (= "http://127.0.0.1:9200/a/b,c") (url test-server "a" [:b :c]))
  (is (= "http://127.0.0.1:9200/a,b/c") (url test-server ["a" "b"] "c"))
  (is (= "http://127.0.0.1:9200/_all/b/c") (url test-server :_all "b" "c")))

(deftest add-doc-test
  (let [response (add-doc test-server test-index test-type test-doc)]
  (is (= 201 (:status response)))))

(deftest get-doc-test
  (add-doc test-server
           test-index
           test-type
           test-doc
           :id 1)
  (Thread/sleep 1000)
  (let [response (get-doc test-server test-index test-type 1)]
    (is (= 200 (:status response)))))

(deftest delete-test
  (let [doc (add-doc test-server
                     test-index
                     test-type
                     test-doc
                     :id 3)]
      (is (=  200 (:status (delete-doc test-server test-index test-type 3))))))

(deftest search-doc-test
  (dotimes [i 3]
    (add-doc test-server
             test-index
             test-type
             test-doc))
  (Thread/sleep 1000)
  (let [response (search-doc test-server
                             {:query {:term {:title "foo"}}}
                             :index :_all)]
    (is (= 200 (:status response)))
    (is (= 3 (-> response :body :hits :hits count)))))

(deftest search-async-test
  (add-doc test-server
           test-index
           test-type
           test-doc)
  (Thread/sleep 1000)
  (let [response (chunked-json-response->hash-map
                  (wait-for-result (search-doc test-server
                                               {:query {:term {:content "bar"}}}
                                               :index test-index
                                               :type test-type
                                               :async true)))]
    (is (= 200 (:status response)))
    (is (= 1 (-> response :body :hits :hits count)))))

(deftest percolate-test
  (is (= 200 (:status (percolate test-server
                                 test-index
                                 "perc-test"
                                 {:query {:term {:field1 "value1" }}})))))


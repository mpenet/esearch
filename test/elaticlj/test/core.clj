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
        response (search-doc test-server test-index test-type
                             {:query {:term {:content "prout"}}}
                              :query-string {:pretty true})]
      (is (= 200 (:status response)))
      (is (= 1 (-> response :body :hits :hits count)))))


(deftest search-async-test
  (let [doc (add-doc test-server
                     test-index
                     test-type
                     (assoc test-doc :content "prout")
                      :id 2)
        response (wait-for-result (search-doc test-server
                                              test-index
                                              test-type
                                              {:query {:term {:content "prout"}}}
                                               :async true))]
    (is (= 200 (:status response)))
    (is (= 1 (-> response :body :hits :hits count)))))


(deftest performance-test
  (time
   (dotimes [i 100]
     (add-doc test-server
              test-index
              test-type
              test-doc
              :async true)))

   (is true))


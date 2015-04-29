# esearch [![Build Status](https://secure.travis-ci.org/mpenet/esearch.png?branch=master)](http://travis-ci.org/mpenet/esearch)

Clojure REST client for [Elastic Search](http://www.elasticsearch.org/)

The query map corresponds to the JSON query DSL, allowing you to
leverage the full features of elastic search.

[Elastic Search Query reference](http://www.elasticsearch.org/guide/reference/query-dsl/)

## Installation

esearch is available as a Maven artifact from [Clojars](http://clojars.org/esearch):

    :dependencies
      [[cc.qbits/esearch "0.8.2"] ...]

## Usage


```clojure
(use 'qbits.esearch)
(use 'qbits.jet.client.http)

;; All queries are asynchronous by default, they return a
   clojure.core.async channel

(def client (qbits.jet.client.http/client))

(add-doc client
         "http://127.0.0.1:9200"
         "tweets"
         "tweet"
         {:text "foo bar" :author {:name "john"} :posted 123450000000})


(add-doc client
         "http://127.0.0.1:9200"
         :tweets ;; index and types can be keywords, strings, numbers and sequences
         :tweet
         {:text "foo bar" :author {:name "john"} :posted 123450000000}
         :id 1) ;; optional id
```

### Supported operations

`add-doc` `get-doc` `mget-doc` `update-doc` `delete-doc` `delete-by-query`
`search-doc` `percolate` `count-docs` `bulk`

### Connections

Since we are making the request through HTTP there is not connection
kept with the server and I am not very fond of bindings for this, imho
partial, comp & co fill this need just fine.


```clojure

(def search (partial es/search-doc client "http://example.com:9200"))
(def get-item (partial es/get-doc clietn "http://example.com:9200" :items :item))

(get-item 12)

```

See [tests](https://github.com/mpenet/esearch/blob/master/test/qbits/esearch/test/core.clj) for more details.

## License

Distributed under the Eclipse Public License, the same as Clojure.

# clj-esearch [![Build Status](https://secure.travis-ci.org/mpenet/clj-esearch.png?branch=master)](http://travis-ci.org/mpenet/clj-esearch)

Clojure REST client for [Elastic Search](http://www.elasticsearch.org/)

Uses [Aleph](https://github.com/ztellman/aleph) HTTP client under the hood.

The query map corresponds to the JSON query DSL, allowing you to
leverage the full features of elastic search.

[Elastic Search Query reference](http://www.elasticsearch.org/guide/reference/query-dsl/)

## Usage


```clojure
(use 'clj-esearch.core)

;; All queries are asynchronous by default, they return a Lamina result channel

(add-doc "http://127.0.0.1:9200"
         "tweets"
         "tweet"
         {:text "foo bar" :author {:name "john"} :posted 123450000000})


(add-doc "http://127.0.0.1:9200"
         :tweets ;; index and types can be keywords, strings, numbers and sequences
         :tweet
         {:text "foo bar" :author {:name "john"} :posted 123450000000}
         :id 1) ;; optional id
```

If you need the query to block/wait for the response you can
dereference it, use an on-success callback, a lamina pipeline or the async macro
see: [Lamina Result Channel](https://github.com/ztellman/lamina/wiki/Result-Channels).

```clojure
@(add-doc ...)

;; Error handling  can be done using lamina utilities

(lamina.core/run-pipeline
  (add-doc "http://127.0.0.1:9200"
           "tweets"
           "tweet"
           {:text "foo bar" :author {:name "john"} :posted 123450000000})
  :error-handler (fn [e] ...)
  #(when (> (:status %) 201) (throw (Exception. "Not good"))))

```

### Supported operations

`add-doc` `get-doc` `mget-doc` `update-doc` `delete-doc` `delete-by-query`
`search-doc` `percolate` `count-docs` `bulk`

### Connections

Since we are making the request through HTTP there is not connection
kept with the server and I am not very fond of bindings for this, imho
partial, comp & co fill this need just fine.


```clojure

(def search (partial es/search-doc "http://example.com:9200"))
(def get-item (partial es/get-doc "http://example.com:9200" :items :item))

(get-item 12)

```


See [tests](https://github.com/mpenet/clj-esearch/blob/master/test/clj_esearch/test/core.clj) for more details.

[Lamina](https://github.com/ztellman/lamina) [Result Channel](https://github.com/ztellman/lamina/wiki/Result-Channels)
[Aleph](https://github.com/ztellman/aleph)


## Installation

clj-esearch is available as a Maven artifact from [Clojars](http://clojars.org/clj-esearch):

    :dependencies
      [[cc.qbits/clj-esearch "0.5.3"] ...]

## License

Distributed under the Eclipse Public License, the same as Clojure.

(ns hypermq.test.handler
  (:require [ring.mock.request :refer :all]
            [midje.sweet       :refer :all]
            [hypermq.handler   :refer :all]
            [hypermq.message   :as msg]))

(defn- json-request
  [method uri & [content]]
  (let [base-request (content-type (request method uri) "application/json")]
    (case method
      :get base-request
      :post (body base-request content))))

(fact "create message should fail if body is not valid json"
      (app (json-request :post "/q/myqueue" "}\"invalid-json{")) => (contains {:status 400}))

(fact "Should parse json body to create message on queue"
      (app (json-request :post "/q/myqueue" "{\"producer\":\"myproducer\",\"body\":{\"msg\":1}}")) => (contains {:status 201})
      (provided
       (msg/create "myqueue" {:producer "myproducer" :body {:msg 1}}) => anything))

(fact "Should list messages on a queue from the beginning"
      (app (json-request :get "/q/myqueue")) => (contains {:status 200 :body (contains "uuid1")})
      (provided
       (msg/fetch "myqueue" nil) => [{:id "uuid1"}]))

(fact "Should list messages on a queue from a known message id"
      (app (json-request :get "/q/fooqueue/uuid1")) => (contains {:status 200 :body (contains "uuid2")})
      (provided
       (msg/fetch "fooqueue" "uuid1") => [{:id "uuid2"}]))

(fact "Should return etag same as id of last message on page"
      (app (json-request :get "/q/fooqueue")) => (contains {:headers (contains {"ETag" "\"id-of-last-msg\""})})
      (provided
       (msg/fetch "fooqueue" nil) => [{:id "id-of-1st-msg"} {:id "id-of-last-msg"}]))

(fact "Should return last modified same as creation date of last message on page"
      (app (json-request :get "/q/fooqueue/uuid1")) => (contains {:headers (contains {"Last-Modified" "Sat, 03 May 2014 10:25:41 GMT"})})
      (provided
       (msg/fetch "fooqueue" "uuid1") => [{:id "uuid2" :created 1000} {:id "uuid3" :created 1399112741000}]))

(ns hypermq.test.handler
  (:require [ring.mock.request :refer :all]
            [midje.sweet       :refer :all]
            [hypermq.handler   :refer :all]
            [hypermq.message   :as msg]
            [hypermq.ack       :as ack]))

(defn- json-request
  [method uri & {:keys [content etag]}]
  (let [base-request (-> (request method uri)
                         (content-type "application/json"))]
    (case method
      :get  (header base-request "If-None-Match" (format "\"%s\"" etag))
      :post (body base-request content))))

(fact "create message should fail if body is not valid json"
      (app (json-request :post "/q/myqueue" :content "}\"invalid-json{")) => (contains {:status 400}))

(fact "Should parse json body to create message on queue"
      (app (json-request :post "/q/myqueue" :content "{\"producer\":\"myproducer\",\"body\":{\"msg\":1}}")) => (contains {:status 201}))

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

(fact "Should return 304 when requested etag matches page"
      (app (json-request :get "/q/fooqueue/uuid1" :etag "uuid2")) => (contains {:status 304})
      (provided
       (msg/fetch "fooqueue" "uuid1") => [{:id "uuid2"}]))

(fact "Should create acknowledgement for a client"
      (app (json-request :post "/ack/fooqueue/client1" :content "{\"id\":\"uuid2\"}")) => (contains {:status 201})
      (provided
       (ack/create "fooqueue" "client1" "uuid2") => anything))

(fact "Should not fail on duplicate acknowledgements"
      (app (json-request :post "/ack/fooqueue/client1" :content "{\"id\":\"uuid2\"}")) => (contains {:status 201})
      (provided
       (ack/acknowledged? "fooqueue" "client1" "uuid2") => anything))

(fact "Should retrieve last seen message-id for a queue & client"
      (app (json-request :get "/ack/fooqueue/client1")) => (contains {:body (contains "uuid2")})
      (provided
       (ack/latest "fooqueue" "client1") => {:message "uuid2"}))

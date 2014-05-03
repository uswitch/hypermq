(ns hypermq.test.handler
  (:require [clojure.test      :refer :all]
            [ring.mock.request :refer :all]
            [midje.sweet       :refer :all]
            [hypermq.handler   :refer :all]
            [hypermq.message   :as msg]))

(deftest test-app
  (testing "main route"
    (let [response (app (request :get "/"))]
      (is (= (:status response) 200))
      (is (= (:body response) "Home Page"))))

  (testing "not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= (:status response) 404)))))

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
      (app (json-request :get "/q/myqueue")) => (contains {:status 200})
      (provided
       (msg/fetch "myqueue" nil) => [{:id "uuid1" :queue "myqueue" :created 123}]))

(ns hypermq.json
  (:require [clojure.java.io     :as io]
            [clojure.data.json   :as js]))

(defn body-as-string [ctx]
  (if-let [body (get-in ctx [:request :body])]
    (condp instance? body
      java.lang.String body
      java.io.ByteArrayInputStream (slurp body)
      (slurp (io/reader body)))))

(defn parse-body [context]
  (when (#{:put :post} (get-in context [:request :request-method]))
    (try
      (if-let [body (body-as-string context)]
        (let [data (js/read-str body :key-fn keyword)]
          [false {:data data}])
        {:message "No body"})
      (catch Exception e
        {:message (format "IOException: " (.getMessage e))}))))

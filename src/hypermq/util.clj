(ns hypermq.util
  (:require [clj-time.core        :as t]
            [clj-time.coerce      :as c]
            [clj-time.format      :as f]
            [clojure.tools.reader :as edn]))

(defn timestamp->str
  [timestamp]
  (f/unparse (f/formatters :mysql) (c/from-long timestamp)))

(defn timestamp [] (c/to-long (t/now)))

(defn serialize [x]
  (pr-str x))

(defn de-serialize [x]
  (edn/read-string x))

(defn mutate-row [key mutate-fn]
  (fn [{value key :as row}]
    (if value
      (assoc row key (mutate-fn value))
      row)))

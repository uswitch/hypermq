(ns hypermq.uuid
  (:require [clj-time.core   :refer [now]]
            [clj-time.coerce :refer [to-long]])
  (:import [com.eaio.uuid UUID UUIDGen]))

(def ^:private timestamp (comp to-long now))
(def ^:private clock-seq-and-node (UUIDGen/getClockSeqAndNode))

(defn generate
  []
  (-> (timestamp)
      (UUIDGen/createTime)
      (UUID. clock-seq-and-node)
      (str)))

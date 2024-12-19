(ns duct.handler.resource
  (:require [duct.handler.util :as util]
            [integrant.core :as ig]
            [ring.util.response :as resp]))

(defmethod ig/init-key :duct.handler/resource [_ {:keys [paths not-found]}]
  (util/path-handler paths resp/resource-response not-found))

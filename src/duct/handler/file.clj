(ns duct.handler.file
  (:require [duct.handler.util :as util]
            [integrant.core :as ig]
            [ring.util.response :as resp]))

(defmethod ig/init-key :duct.handler/file [_ {:keys [paths not-found]}]
  (util/path-handler paths resp/file-response not-found))

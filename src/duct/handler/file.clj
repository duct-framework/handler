(ns duct.handler.file
  (:require [clojure.string :as str]
            [integrant.core :as ig]
            [ring.middleware.content-type :as type]
            [ring.util.async :as async]
            [ring.util.response :as resp]))

(defn- match-path [uri paths]
  (first (filter #(str/starts-with? uri (key %)) paths)))

(defn- path-handler [paths make-response not-found-response]
  (let [paths (sort-by (comp count key) paths)]
    (fn handler
      ([{:keys [uri] :as req}]
       (if-some [[path opts] (match-path uri paths)]
         (or (make-response req (subs uri (count path)) opts)
             not-found-response)
         not-found-response))
      ([request respond raise]
       (async/raising raise (respond (handler request)))))))

(defmethod ig/init-key :duct.handler/file [_ {:keys [paths not-found]}]
  (path-handler paths
                (fn [req path opts]
                  (some-> (resp/file-response path opts)
                          (type/content-type-response req opts)))
                not-found))

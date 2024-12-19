(ns duct.handler.file
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [duct.handler.static :as static]
            [integrant.core :as ig]
            [ring.util.async :as async]))

(defn- match-path [uri paths]
  (first (filter #(str/starts-with? uri (key %)) paths)))

(defn- path-handler [paths make-response not-found-response]
  (let [paths (sort-by (comp count key) paths)]
    (fn handler
      ([{:keys [uri]}]
       (if-some [[path opts] (match-path uri paths)]
         (or (make-response (subs uri (count path)) opts)
             not-found-response)
         not-found-response))
      ([request respond raise]
       (async/raising raise (respond (handler request)))))))

(defmethod ig/init-key :duct.handler/file [_ {:keys [paths not-found]}]
  (path-handler paths
                (fn [path {:keys [root]}]
                  (let [body (io/file root path)]
                    (when (.exists body)
                      (static/static-response {:status 200, :body body}))))
                not-found))

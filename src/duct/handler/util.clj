(ns duct.handler.util
  (:require [clojure.string :as str]
            [ring.middleware.content-type :as type]
            [ring.util.async :as async]))

(defn- match-path [uri paths]
  (first (filter #(str/starts-with? uri (key %)) paths)))

(defn path-handler [paths make-response not-found-response]
  (let [paths (sort-by (comp count key) paths)]
    (fn handler
      ([{:keys [uri] :as req}]
       (if-some [[path opts] (match-path uri paths)]
         (or (some-> (make-response (subs uri (count path)) opts)
                     (type/content-type-response req opts))
             not-found-response)
         not-found-response))
      ([request respond raise]
       (async/raising raise (respond (handler request)))))))

(ns duct.handler.util
  (:require [clojure.string :as str]
            [ring.middleware.content-type :as type]
            [ring.util.async :as async]))

(defn path-handler [paths make-response not-found-response]
  (let [paths (reverse (sort-by (comp count key) paths))]
    (fn handler
      ([{:keys [uri] :as req}]
       (or (some (fn [[path opts]]
                   (when (str/starts-with? uri path)
                     (some-> (make-response (subs uri (count path)) opts)
                             (type/content-type-response req opts))))
                 paths)
           not-found-response))
      ([request respond raise]
       (async/raising raise (respond (handler request)))))))

(ns duct.handler.static
  (:require [integrant.core :as ig]
            [ring.util.mime-type :as mime]
            [ring.util.response :as resp]))

(defn- merge-response [a b]
  (-> a (assoc :body (:body b))
        (update :headers (partial merge (:headers b)))))

(defn- guess-content-type [response name]
  (if-let [mime-type (mime/ext-mime-type (str name))]
    (resp/content-type response mime-type)
    response))

(defprotocol ResponseBody
  (update-response [body response]))

(extend-protocol ResponseBody
  nil
  (update-response [_ response] response)
  Object
  (update-response [_ response] response)
  java.io.File
  (update-response [f response]
    (if-let [r (resp/file-response (str f))]
      (-> response (merge-response r) (guess-content-type (str f)))
      (assoc response :body nil)))
  java.net.URL
  (update-response [url response]
    (if-let [r (resp/url-response url)]
      (-> response (merge-response r) (guess-content-type (str url)))
      (assoc response :body nil))))

(defn- static-response [response]
  (update-response (:body response) response))

(defn- static-handler [response]
  (fn
    ([_] (static-response response))
    ([_ respond _] (respond (static-response response)))))

(defmethod ig/init-key :duct.handler/static [_ response]
  (static-handler response))

(defmethod ig/init-key ::ok [_ response]
  (static-handler (assoc response :status 200)))

(defmethod ig/init-key ::bad-request [_ response]
  (static-handler (assoc response :status 400)))

(defmethod ig/init-key ::not-found [_ response]
  (static-handler (assoc response :status 404)))

(defmethod ig/init-key ::method-not-allowed [_ response]
  (static-handler (assoc response :status 405)))

(defmethod ig/init-key ::not-acceptable [_ response]
  (static-handler (assoc response :status 406)))

(defmethod ig/init-key ::internal-server-error [_ response]
  (static-handler (assoc response :status 500)))

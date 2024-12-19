(ns duct.handler.file-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer [deftest is]]
            [duct.handler.file :as file]
            [integrant.core :as ig]))

(deftest test-file-handler
  (let [root    "test/duct/handler/files"
        handler (ig/init-key :duct.handler/file
                             {:paths {"/static/" {:root root}}})]
    (is (= {:status 200
            :headers {"Content-Type" "text/plain"
                      "Content-Length" "4"}
            :body    (io/file root "foo.txt")}
           (-> (handler {:request-method :get, :uri "/static/foo.txt"})
               (update :headers dissoc "Last-Modified"))))
    (is (nil? (handler {:request-method :get, :uri "/static/bar.txt"})))
    (is (nil? (handler {:request-method :get, :uri "/foo.txt"})))))

(deftest test-not-found
  (let [root    "test/duct/handler/files"
        handler (ig/init-key :duct.handler/file
                             {:paths {"/static/" {:root root}}
                              :not-found {:status 404, :body "Not Found"}})]
    (is (= {:status 200
            :headers {"Content-Type" "text/plain"
                      "Content-Length" "4"}
            :body    (io/file root "foo.txt")}
           (-> (handler {:request-method :get, :uri "/static/foo.txt"})
               (update :headers dissoc "Last-Modified"))))
    (is (= {:status 404, :body "Not Found"}
           (handler {:request-method :get, :uri "/static/bar.txt"})))))

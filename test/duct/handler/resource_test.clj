(ns duct.handler.resource-test
  (:require [clojure.test :refer [deftest is]]
            [duct.handler.resource]
            [integrant.core :as ig]))

(deftest test-resource-handler
  (let [root    "duct/handler/files"
        handler (ig/init-key :duct.handler/resource
                             {:paths {"/static/" {:root root}}})]
    (is (= {:status 200
            :headers {"Content-Type" "text/plain"
                      "Content-Length" "4"}
            :body    "foo\n"}
           (-> (handler {:request-method :get, :uri "/static/foo.txt"})
               (update :headers dissoc "Last-Modified")
               (update :body slurp))))
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
            :body    "foo\n"}
           (-> (handler {:request-method :get, :uri "/static/foo.txt"})
               (update :headers dissoc "Last-Modified")
               (update :body slurp))))
    (is (= {:status 404, :body "Not Found"}
           (handler {:request-method :get, :uri "/static/bar.txt"})))))

(deftest test-security
  (let [root    "test/duct/handler/files"
        handler (ig/init-key :duct.handler/file
                             {:paths {"/static/" {:root root}}})]
    (is (nil? (handler {:request-method :get, :uri "/../file_test.clj"})))))

(deftest test-async-handler
  (let [root    "test/duct/handler/files"
        handler (ig/init-key :duct.handler/file
                             {:paths {"/static/" {:root root}}})
        respond (promise)
        raise   (promise)]
    (handler {:request-method :get, :uri "/static/foo.txt"}
             respond raise)
    (is (realized? respond))
    (is (not (realized? raise)))
    (is (= {:status 200
            :headers {"Content-Type" "text/plain"
                      "Content-Length" "4"}
            :body    "foo\n"}
           (-> (deref respond 100 nil)
               (update :headers dissoc "Last-Modified")
               (update :body slurp))))))

(deftest test-multiple-paths
  (let [paths   {"/"    {:root "duct/handler/files"}
                 "/sub" {:root "duct/handler/files/subdir"}}
        handler (ig/init-key :duct.handler/resource {:paths paths})]
    (is (= {:status 200
            :headers {"Content-Type" "text/plain"
                      "Content-Length" "4"}
            :body    "foo\n"}
           (-> (handler {:request-method :get, :uri "/foo.txt"})
               (update :headers dissoc "Last-Modified")
               (update :body slurp))))
    (is (= {:status 200
            :headers {"Content-Type" "text/plain"
                      "Content-Length" "4"}
            :body    "bar\n"}
           (-> (handler {:request-method :get, :uri "/sub/bar.txt"})
               (update :headers dissoc "Last-Modified")
               (update :body slurp))))
    (is (nil? (handler {:request-method :get, :uri "/bar.txt"})))
    (is (nil? (handler {:request-method :get, :uri "/sub/foo.txt"})))))

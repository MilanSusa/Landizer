(ns landizer.test.dao.user-dao
  (:require
    [java-time.pre-java8]
    [luminus-migrations.core :as migrations]
    [clojure.test :refer :all]
    [landizer.config :refer [env]]
    [mount.core :as mount]
    [landizer.dao.user-dao :as user-dao]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'landizer.config/env
      #'landizer.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

(deftest test-user-dao
  (testing "user dao creation"
    (let [prev-user (user-dao/get-user-by-email "user.creation.dao@example.com")]
      (user-dao/delete-user! (:id prev-user)))
    (is (= 1 (user-dao/create-user! "Test" "Test" "user.creation.dao@example.com" "test"))))

  (testing "user retrieval by email"
    (let [prev-user (user-dao/get-user-by-email "user.retrieval.dao@example.com")]
      (user-dao/delete-user! (:id prev-user)))
    (user-dao/create-user! "Test" "Test" "user.retrieval.dao@example.com" "test")
    (is (= false
           (empty? (user-dao/get-user-by-email "user.retrieval.dao@example.com")))))

  (testing "user dao deletion by id"
    (let [prev-user (user-dao/get-user-by-email "user.deletion.dao@example.com")]
      (user-dao/delete-user! (:id prev-user)))
    (user-dao/create-user! "Test" "Test" "user.deletion.dao@example.com" "test")
    (let [user (user-dao/get-user-by-email "user.deletion.dao@example.com")
          user-id (:id user)]
      (user-dao/delete-user! user-id)
      (is (= 0 (user-dao/delete-user! user-id)))))

  (testing "user dao login"
    (let [prev-user (user-dao/get-user-by-email "user.login.dao@example.com")]
      (user-dao/delete-user! (:id prev-user)))
    (user-dao/create-user! "Test" "Test" "user.login.dao@example.com" "test")
    (let [user (user-dao/get-user-by-email "user.login.dao@example.com")]
      (is (= (dissoc user :password)
             (user-dao/login-user! (:email user) "test"))))))

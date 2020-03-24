(ns landizer.test.db.core
  (:require
    [landizer.db.core :refer [*db*] :as db]
    [java-time.pre-java8]
    [luminus-migrations.core :as migrations]
    [clojure.test :refer :all]
    [clojure.java.jdbc :as jdbc]
    [landizer.config :refer [env]]
    [mount.core :as mount]
    [buddy.hashers :as hashers]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'landizer.config/env
      #'landizer.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

(deftest test-users
  (testing "user creation"
    (jdbc/with-db-transaction [t-conn *db*]
                              (let [prev-user (db/get-user-by-email
                                                t-conn
                                                {:email "user.creation@example.com"})]
                                (db/delete-user!
                                  t-conn
                                  {:id (:id prev-user)}))
                              (is (= 1 (db/create-user!
                                         t-conn
                                         {:first_name "Test"
                                          :last_name  "Test"
                                          :email      "user.creation@example.com"
                                          :password   (hashers/derive "test")})))))

  (testing "user retrieval by email"
    (jdbc/with-db-transaction [t-conn *db*]
                              (let [prev-user (db/get-user-by-email
                                                t-conn
                                                {:email "retrieve.by.email@example.com"})]
                                (db/delete-user!
                                  t-conn
                                  {:id (:id prev-user)}))
                              (db/create-user!
                                t-conn
                                {:first_name "Test"
                                 :last_name  "Test"
                                 :email      "retrieve.by.email@example.com"
                                 :password   (hashers/derive "test")})
                              (let [user (db/get-user-by-email
                                           t-conn
                                           {:email "retrieve.by.email@example.com"})]
                                (is (= true
                                       (and (= "Test" (:first_name user))
                                            (= "Test" (:last_name user))
                                            (= "retrieve.by.email@example.com" (:email user))
                                            (hashers/check "test" (:password user))))))))

  (testing "user retrieval by id"
    (jdbc/with-db-transaction [t-conn *db*]
                              (let [prev-user (db/get-user-by-email
                                                t-conn
                                                {:email "retrieve.by.id@example.com"})]
                                (db/delete-user!
                                  t-conn
                                  {:id (:id prev-user)}))
                              (db/create-user!
                                t-conn
                                {:first_name "Test"
                                 :last_name  "Test"
                                 :email      "retrieve.by.id@example.com"
                                 :password   (hashers/derive "test")})
                              (let [user (db/get-user-by-email
                                           t-conn
                                           {:email "retrieve.by.id@example.com"})
                                    user-id (:id user)]
                                (is (= {:id         user-id
                                        :first_name "Test"
                                        :last_name  "Test"
                                        :email      "retrieve.by.id@example.com"}
                                       (dissoc (db/get-user
                                                 t-conn
                                                 {:id user-id})
                                               :password)))))))

(deftest test-predictions
  (testing "prediction creation"
    (jdbc/with-db-transaction [t-conn *db*]
                              (let [prev-user (db/get-user-by-email
                                                t-conn
                                                {:email "pred.creation@example.com"})]
                                (db/delete-user!
                                  t-conn
                                  {:id (:id prev-user)}))
                              (db/create-user!
                                t-conn
                                {:first_name "Test"
                                 :last_name  "Test"
                                 :email      "pred.creation@example.com"
                                 :password   (hashers/derive "test")})
                              (let [user (db/get-user-by-email
                                           t-conn
                                           {:email "pred.creation@example.com"})]
                                (is (= 1 (db/create-prediction!
                                           t-conn
                                           {:landmark    "Test"
                                            :probability 100
                                            :image       "https://test-prediciton-creation.com"
                                            :user_id     (:id user)}))))))

  (testing "prediction retrieval by user id"
    (jdbc/with-db-transaction [t-conn *db*]
                              (let [prev-user (db/get-user-by-email
                                                t-conn
                                                {:email "pred.retrieval@example.com"})]
                                (db/delete-user!
                                  t-conn
                                  {:id (:id prev-user)}))
                              (db/create-user!
                                t-conn
                                {:first_name "Test"
                                 :last_name  "Test"
                                 :email      "pred.retrieval@example.com"
                                 :password   (hashers/derive "test")})
                              (let [user (db/get-user-by-email
                                           t-conn
                                           {:email "pred.retrieval@example.com"})
                                    user-id (:id user)]
                                (db/create-prediction!
                                  t-conn
                                  {:landmark    "Test"
                                   :probability 100
                                   :image       "https://test-prediction-retrieval.com"
                                   :user_id     user-id})
                                (is (= false
                                       (empty? (db/get-predictions-for-user
                                                 t-conn
                                                 {:user_id user-id}))))))))

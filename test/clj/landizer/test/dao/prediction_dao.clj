(ns landizer.test.dao.prediction-dao
  (:require
    [java-time.pre-java8]
    [luminus-migrations.core :as migrations]
    [clojure.test :refer :all]
    [landizer.config :refer [env]]
    [mount.core :as mount]
    [landizer.dao.user-dao :as user-dao]
    [landizer.dao.prediction-dao :as prediction-dao]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'landizer.config/env
      #'landizer.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

(deftest test-prediction-dao
  (testing "prediction dao creation"
    (let [prev-user (user-dao/get-user-by-email "pred.creation.dao@example.com")]
      (user-dao/delete-user! (:id prev-user)))
    (user-dao/create-user! "Test" "Test" "pred.creation.dao@example.com" "test")
    (let [user (user-dao/get-user-by-email "pred.creation.dao@example.com")]
      (is (= 1 (prediction-dao/create-prediction! "Test" 100 "https://test-pred-dao-creation.com" (:id user))))))

  (testing "prediction dao retrieval by user id"
    (let [prev-user (user-dao/get-user-by-email "pred.retrieval.dao@example.com")]
      (user-dao/delete-user! (:id prev-user)))
    (user-dao/create-user! "Test" "Test" "pred.retrieval.dao@example.com" "test")
    (let [user (user-dao/get-user-by-email "pred.retrieval.dao@example.com")
          user-id (:id user)]
      (prediction-dao/create-prediction! "Test" 100 "https://test-pred-dao-retrieval.com" user-id)
      (is (= false
             (empty? (prediction-dao/get-predictions-for-user user-id)))))))

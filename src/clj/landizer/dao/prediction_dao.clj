(ns landizer.dao.prediction-dao
  (:require
    [clojure.java.jdbc :as jdbc]
    [landizer.db.core :as db]))

(defn create-prediction! [landmark probability image user_id]
  (jdbc/with-db-transaction [t-conn db/*db*]
                            (try
                              (db/create-prediction! t-conn
                                                     {:landmark    landmark
                                                      :probability probability
                                                      :image       image
                                                      :user_id     user_id})
                              (catch Exception e
                                "Error occurred while creating prediction"))))

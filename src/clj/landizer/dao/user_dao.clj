(ns landizer.dao.user-dao
  (:require
    [buddy.hashers :as hashers]
    [clojure.java.jdbc :as jdbc]
    [landizer.db.core :as db]))

(defn create-user! [first_name last_name email password]
  (jdbc/with-db-transaction [t-conn db/*db*]
                            (if-not (empty? (db/get-user-by-email t-conn {:email email}))
                              (throw (ex-info "User already exists!"
                                              {:error "User already exists!"}))
                              (db/create-user! t-conn
                                               {:first_name first_name
                                                :last_name  last_name
                                                :email      email
                                                :password   (hashers/derive password)}))))

(defn get-user-by-email [email]
  (jdbc/with-db-connection [t-conn db/*db*]
                           (db/get-user-by-email t-conn {:email email})))

(defn delete-user! [id]
  (jdbc/with-db-transaction [t-conn db/*db*]
                            (db/delete-user! t-conn {:id id})))

(defn login-user! [email password]
  (let [{hashed-password :password :as user}
        (db/get-user-by-email {:email email})]
    (when (hashers/check password hashed-password)
      (dissoc user :password))))
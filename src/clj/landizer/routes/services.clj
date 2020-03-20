(ns landizer.routes.services
  (:require
    [reitit.swagger :as swagger]
    [reitit.swagger-ui :as swagger-ui]
    [reitit.ring.coercion :as coercion]
    [reitit.coercion.spec :as spec-coercion]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.multipart :as multipart]
    [reitit.ring.middleware.parameters :as parameters]
    [landizer.middleware.formats :as formats]
    [landizer.middleware.exception :as exception]
    [ring.util.http-response :refer :all]
    [clojure.java.io :as io]
    [landizer.dao.user-dao :as user-dao]
    [landizer.dao.prediction-dao :as prediction-dao]))

(defn service-routes []
  ["/api"
   {:coercion spec-coercion/coercion
    :muuntaja formats/instance
    :swagger {:id ::api}
    :middleware [;; query-params & form-params
                 parameters/parameters-middleware
                 ;; content-negotiation
                 muuntaja/format-negotiate-middleware
                 ;; encoding response body
                 muuntaja/format-response-middleware
                 ;; exception handling
                 exception/exception-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodys
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware
                 ;; multipart
                 multipart/multipart-middleware]}

   ;; swagger documentation
   ["" {:no-doc true
        :swagger {:info {:title "my-api"
                         :description "https://cljdoc.org/d/metosin/reitit"}}}

    ["/swagger.json"
     {:get (swagger/create-swagger-handler)}]

    ["/api-docs/*"
     {:get (swagger-ui/create-swagger-ui-handler
             {:url "/api/swagger.json"
              :config {:validator-url nil}})}]]

   ["/register"
    {:post {:summary    "registers a user"
            :parameters {:body {:first_name string?
                                :last_name  string?
                                :email      string?
                                :password   string?}}
            :responses  {201 {:body {:message string?}}
                         400 {:body {:message string?}}}
            :handler    (fn [{{{:keys [first_name last_name email password]} :body} :parameters}]
                          (try
                            (user-dao/create-user! first_name last_name email password)
                            (ok {:message "Registration successful!"})
                            (catch Exception e
                              "An error occurred while registering a user!")))}}]

   ["/login"
    {:post {:summary    "logs the user in"
            :parameters {:body {:email    string?
                                :password string?}}
            :responses  {200 {:body {:identity {:email string?}}}
                         401 {:body {:message string?}}}
            :handler    (fn [{{{:keys [email password]} :body} :parameters
                              session                          :session}]
                          (if-some [user (user-dao/login-user! email password)]
                            (-> (ok {:identity user})
                                (assoc :session (assoc session :identity user)))
                            (unauthorized
                              {:message "Invalid email and/or password!"})))}}]

   ["/logout"
    {:post {:summary "logs the user out"
            :handler (fn [_]
                       (-> (ok)
                           (assoc :session nil)))}}]

   ["/predictions"
    {:post {:summary    "creates prediction for user with given id"
            :parameters {:body {:image       string?
                                :probability double?
                                :landmark    string?
                                :user_id     int?}}
            :responses  {201 {:body {:message string?}}
                         400 {:body {:message string?}}}
            :handler    (fn [{{{:keys [image probability landmark user_id]} :body} :parameters}]
                          (try
                            (prediction-dao/create-prediction! landmark probability image user_id)
                            (ok {:message "Prediction created"})
                            (catch Exception e
                              (.getMessage e))))}}]

   ["/ping"
    {:get (constantly (ok {:message "pong"}))}]
   

   ["/math"
    {:swagger {:tags ["math"]}}

    ["/plus"
     {:get {:summary "plus with spec query parameters"
            :parameters {:query {:x int?, :y int?}}
            :responses {200 {:body {:total pos-int?}}}
            :handler (fn [{{{:keys [x y]} :query} :parameters}]
                       {:status 200
                        :body {:total (+ x y)}})}
      :post {:summary "plus with spec body parameters"
             :parameters {:body {:x int?, :y int?}}
             :responses {200 {:body {:total pos-int?}}}
             :handler (fn [{{{:keys [x y]} :body} :parameters}]
                        {:status 200
                         :body {:total (+ x y)}})}}]]

   ["/files"
    {:swagger {:tags ["files"]}}

    ["/upload"
     {:post {:summary "upload a file"
             :parameters {:multipart {:file multipart/temp-file-part}}
             :responses {200 {:body {:name string?, :size int?}}}
             :handler (fn [{{{:keys [file]} :multipart} :parameters}]
                        {:status 200
                         :body {:name (:filename file)
                                :size (:size file)}})}}]

    ["/download"
     {:get {:summary "downloads a file"
            :swagger {:produces ["image/png"]}
            :handler (fn [_]
                       {:status 200
                        :headers {"Content-Type" "image/png"}
                        :body (-> "public/img/warning_clojure.png"
                                  (io/resource)
                                  (io/input-stream))})}}]]])

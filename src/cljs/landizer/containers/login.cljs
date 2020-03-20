(ns landizer.containers.login
  (:require
    [reagent.core :as r]
    [clojure.string :as string]
    [landizer.store.session :refer [session]]
    [ajax.core :refer [POST]]))

(defn login-page []
  (r/with-let [inputs (r/atom {})
               error (r/atom nil)]
              [:section.section>div.container>div.content
               [:div.has-text-centered
                [:img {:src "img/landizer-with-name.PNG"}]]
               [:div
                (when-not (string/blank? @error)
                  [:div.notification.is-danger
                   @error])
                [:div.field
                 [:div.label "Email"]
                 [:div.control
                  [:input.input
                   {:type      "email"
                    :value     (:email @inputs)
                    :on-change #(swap! inputs assoc :email (-> %
                                                               .-target
                                                               .-value))}]]]
                [:div.field
                 [:div.label "Password"]
                 [:div.control
                  [:input.input
                   {:type      "password"
                    :value     (:password @inputs)
                    :on-change #(swap! inputs assoc :password (-> %
                                                                  .-target
                                                                  .-value))}]]]]
               [:div {:style {:paddingTop 20}}
                [:button.button.is-black
                 {:on-click (fn [_]
                              (reset! error nil)
                              (POST "/api/login"
                                    {:params        @inputs
                                     :handler       (fn [response]
                                                      (reset! inputs {})
                                                      (swap! session assoc :user-id (-> response
                                                                                        :identity
                                                                                        :id))
                                                      (set! (.-location js/window) "/#/recognize"))
                                     :error-handler #(reset! error (or (-> %
                                                                           :response
                                                                           :message)
                                                                       (:status-text %)
                                                                       "Unknown error!"))}))
                  :disabled (or (string/blank? (:email @inputs))
                                (string/blank? (:password @inputs)))}
                 "Login"]]]))
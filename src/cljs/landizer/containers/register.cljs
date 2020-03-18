(ns landizer.containers.register
  (:require
    [reagent.core :as r]
    [clojure.string :as string]
    [landizer.store.session :refer [session]]
    [ajax.core :refer [POST]]))

(defn register-page []
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
                 [:div.label "First name"]
                 [:div.control
                  [:input.input
                   {:type      "text"
                    :value     (:first_name @inputs)
                    :on-change #(swap! inputs assoc :first_name (-> %
                                                                    .-target
                                                                    .-value))}]]]
                [:div.field
                 [:div.label "Last name"]
                 [:div.control
                  [:input.input
                   {:type      "text"
                    :value     (:last_name @inputs)
                    :on-change #(swap! inputs assoc :last_name (-> %
                                                                   .-target
                                                                   .-value))}]]]
                [:div.field
                 [:div.label "E-mail"]
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
                              (POST "/api/register"
                                    {:params        @inputs
                                     :handler       (fn [_]
                                                      (reset! inputs {})
                                                      (set! (.-location js/window) "/#/login"))
                                     :error-handler #(reset! error (or (-> %
                                                                           :response
                                                                           :message)
                                                                       (:status-text %)
                                                                       "Unknown error!"))}))
                  :disabled (or (string/blank? (:first_name @inputs))
                                (string/blank? (:last_name @inputs))
                                (string/blank? (:email @inputs))
                                (string/blank? (:password @inputs)))}
                 "Register"]]]))

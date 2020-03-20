(ns landizer.containers.history
  (:require
    [reagent.core :as r]
    [landizer.store.session :refer [session]]
    [ajax.core :refer [GET]]))

(defn history-page []
  (let [predictions (r/atom nil)]
    (r/create-class
      {:component-did-mount (fn [this]
                              (GET "/api/predictions"
                                   {:params  {:user_id (:user-id @session)}
                                    :handler #(reset! predictions (:data %))}))
       :reagent-render      (fn []
                              [:section.section>div.container>div.content
                               [:div.has-text-centered
                                [:img {:src "img/landizer-with-name.PNG"}]]
                               (for [prediction @predictions]
                                 ^{:key (:id prediction)} [:div.field.box.has-text-centered
                                                           [:img {:src (:image prediction)}]
                                                           [:div (:landmark prediction)]])])})))

(ns landizer.components.logout
  (:require
    [ajax.core :refer [POST]]))

(defn logout-page []
  [:section.section>div.container>div.content.has-text-centered
   [:div.has-text-centered
    [:img {:src "img/landizer-with-name.PNG"}]]
   [:div.field
    [:div.label "Are you sure you want to logout?"]]
   [:button.button.is-black
    {:on-click #(POST "/api/logout"
                      {:handler (fn [_]
                                  (set! (.-location js/window) "/"))})}
    "Logout"]])

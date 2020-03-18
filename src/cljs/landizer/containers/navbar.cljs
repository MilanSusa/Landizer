(ns landizer.containers.navbar
  (:require
    [reagent.core :as r]
    [landizer.store.session :refer [session]]))

(defn- nav-link [uri title page expanded?]
  [:a.navbar-item
   {:href  uri
    :on-click #(swap! expanded? not)
    :class (when (= page (:page @session)) "is-active")}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)]
              [:nav.navbar.is-dark.has-background-black>div.container
               [:div.navbar-brand
                [:a.navbar-item {:href "/" :style {:font-weight :bold}} "Landizer"]
                [:span.navbar-burger.burger
                 {:data-target :nav-menu
                  :on-click    #(swap! expanded? not)
                  :class       (when @expanded? :is-active)}
                 [:span] [:span] [:span]]]
               [:div#nav-menu.navbar-menu
                {:class (when @expanded? :is-active)}
                [:div.navbar-start
                 [nav-link "#/" "Home" :home expanded?]
                 (when (= (:user-id @session) nil)
                   [nav-link "#/register" "Register" :reg expanded?])
                 (when (= (:user-id @session) nil)
                   [nav-link "#/login" "Login" :login expanded?])]]]))

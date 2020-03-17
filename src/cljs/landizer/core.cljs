(ns landizer.core
  (:require
    [reagent.core :as r]
    [goog.events :as events]
    [goog.history.EventType :as HistoryEventType]
    [markdown.core :refer [md->html]]
    [landizer.ajax :as ajax]
    [ajax.core :refer [GET POST]]
    [reitit.core :as reitit]
    [clojure.string :as string]
    [landizer.sw.registration :as registration]
    [landizer.store.session :refer [session]]
    [landizer.containers.navbar :refer [navbar]])
  (:import goog.History))

(defn home-page []
  [:section.section>div.container>div.content
   (when-let [docs (:docs @session)]
     [:div {:dangerouslySetInnerHTML {:__html (md->html docs)}}])])

(def pages
  {:home  #'home-page})

(defn page []
  [(pages (:page @session))])

;; -------------------------
;; Routes

(def router
  (reitit/router
    [["/" :home]
     ["/about" :about]]))

(defn match-route [uri]
  (->> (or (not-empty (string/replace uri #"^.*#" "")) "/")
       (reitit/match-by-path router)
       :data
       :name))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (swap! session assoc :page (match-route (.-token event)))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(swap! session assoc :docs %)}))

(defn mount-components []
  (r/render [#'navbar] (.getElementById js/document "navbar"))
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (ajax/load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components)
  (registration/register-installation-listener!)
  (registration/register-service-worker!))

(ns landizer.sw.registration)

(defn register-service-worker! []
  (when js/navigator.serviceWorker
    (.register js/navigator.serviceWorker "/service-worker.js")))

(defn- show-install-prompt! [event]
  (.prompt event))

(defn register-installation-listener! []
  (.addEventListener js/window "beforeinstallprompt" #(show-install-prompt! %)))
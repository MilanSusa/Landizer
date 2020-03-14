(ns landizer.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[landizer started successfully]=-"))
   :stop       (fn []
                 (log/info "\n-=[landizer has shut down successfully]=-"))
   :middleware identity})

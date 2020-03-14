(ns landizer.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [landizer.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init       (fn []
                 (parser/cache-off!)
                 (log/info "\n-=[landizer started successfully using the development profile]=-"))
   :stop       (fn []
                 (log/info "\n-=[landizer has shut down successfully]=-"))
   :middleware wrap-dev})

(ns landizer.store.session
  (:require [reagent.core :as r]))

(defonce session (r/atom {:page :home}))

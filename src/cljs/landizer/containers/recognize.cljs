(ns landizer.containers.recognize
  (:require
    [reagent.core :as r]
    [landizer.store.session :refer [session]]
    [ajax.core :refer [GET POST]]
    [goog.string :as gstring]
    [goog.string.format]))

(defonce image (r/atom nil))
(defonce prediction (r/atom nil))
(defonce content (r/atom nil))
(defonce button-classes (r/atom #{"button" "is-black"}))
(defonce prediction-announcement (r/atom nil))

(defonce firebase-project-id "{your-firebase-project-id}")
(defonce landmark-recognition-inference-api-base-url "http://localhost:8000")

(defn- extract-content-from-wiki-response! [response]
  (if (empty? @prediction)
    (reset! prediction-announcement [:div
                                     [:p "Landizer couldn't recognize this landmark."]])
    (reset! prediction-announcement [:div
                                     [:p "Landizer thinks this landmark is:"]
                                     [:strong @prediction]]))
  (reset! content (-> response
                      :query
                      :pages
                      first
                      :extract)))

(defn- wiki-content-handler! [response]
  (extract-content-from-wiki-response! response))

(defn- get-wiki-content! [title]
  (GET (gstring/format "https://en.wikipedia.org/w/api.php?action=query&prop=extracts&rvprop=content&format=json&origin=*&titles=%s" title)
       {:handler wiki-content-handler!}))

(defn- wiki-search-handler! [response]
  (get-wiki-content! (-> response
                         second
                         first)))

(defn- perform-wiki-search! [response]
  (reset! prediction (:landmark response))
  (GET (gstring/format "https://en.wikipedia.org/w/api.php?action=opensearch&format=json&origin=*&search=%s" @prediction)
       {:handler wiki-search-handler!}))

(defn- build-firebase-upload-url []
  (gstring/format
     "https://firebasestorage.clients6.google.com/v0/b/%s.appspot.com/o?uploadType=multipart&name=%s.jpg"
     firebase-project-id
     (.getTime (js/Date.))))

(defn- build-firebase-download-url [name token]
  (gstring/format
     "https://firebasestorage.googleapis.com/v0/b/%s.appspot.com/o/%s?alt=media&token=%s"
     firebase-project-id
     name
     token))

(defn- prediction-creation-handler! [res landmark probability]
  (let [name (:name res)
        token (:token res)
        image (build-firebase-download-url name token)]
    (POST "/api/predictions"
          {:headers {"Content-Type" "application/json"}
           :params  {:image       image
                     :landmark    landmark
                     :probability probability
                     :user_id     (:user-id @session)}})))

(defn- create-prediction! [response]
  (let [landmark (:landmark response)
        probability (:probability response)
        form-data (doto
                    (js/FormData.)
                    (.append "file" @image))]
    (POST build-firebase-upload-url
          {:body            form-data
           :response-format :json
           :handler         (fn [res]
                              (prediction-creation-handler! res landmark probability))})))

(defn- inference-handler! [response]
  (swap! button-classes disj "is-loading")
  (perform-wiki-search! response)
  (create-prediction! response))

(defn- reset-prediction-state! []
  (swap! button-classes conj "is-loading")
  (reset! prediction-announcement nil)
  (reset! prediction nil)
  (reset! content nil))

(defn- perform-inference! []
  (reset-prediction-state!)
  (let [form-data (doto
                    (js/FormData.)
                    (.append "image" @image))]
    (POST (gstring/format "%s/inference/" landmark-recognition-inference-api-base-url)
          {:body            form-data
           :response-format :json
           :handler         inference-handler!})))

(defn recognize-page []
  [:section.section>div.container>div.content
   [:div.has-text-centered
    [:img {:src "img/landizer-with-name.PNG"}]]
   [:div.file.is-boxed.is-black.is-centered {:id "file-chooser" :style {:paddingBottom 20}}
    [:label.file-label
     [:input.file-input {:type      "file"
                         :name      "resume"
                         :accept    "image/*"
                         :capture   "camera"
                         :on-change #(reset! image (-> %
                                                       .-target
                                                       .-files
                                                       array-seq
                                                       first))}]
     [:span.file-cta
      [:span.file-label "Take a picture..."]]]]
   [:div.is-boxed.has-text-centered
    [:button.button.is-black
     {:class    @button-classes
      :type     "button"
      :on-click perform-inference!}
     "Recognize landmark"
     [:span.spinner-border.spinner-border-sm {:role        "status"
                                              :aria-hidden true}]]]
   [:div.is-boxed.has-text-centered {:style {:paddingTop 20}}
    @prediction-announcement]
   [:div.is-boxed.has-text-justified {:dangerouslySetInnerHTML {:__html @content}}]])
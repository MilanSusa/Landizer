(ns landizer.components.home)

(defn home-page []
  [:section.section>div.container>div.content
   [:div.has-text-centered
    [:img {:src "img/landizer-with-name.PNG"}]]
   [:div.label.has-text-centered "Welcome to Landizer - Landmark Recognizer"]
   [:div.field.has-text-justified
    [:p "Have you ever been on a trip and walked by an amazing landmark but wondered what it is? You probably had to
     ask people around and hope that someone knows what the landmark is. Some time passed and you came across a person
     who could identify the landmark. After that, you searched the internet to find more information about the landmark,
     hoping that the person was correct."]]
   [:div.field.has-text-justified
    [:p "Forget about this tedious process and find out more about the landmark"
     [:strong " by just taking a picture of it!"]]]
   [:div.field.has-text-justified
    [:p "To get your personal recognizer in your pocket, follow these"
     [:strong " 5 fast and easy steps:"]]]
   [:div.label.box "1) Register an account."]
   [:div.label.box "2) Log in to your account."]
   [:div.label.box "3) Take a picture of the landmark."]
   [:div.label.box "4) Press the recognize landmark button."]
   [:div.label.box "5) View history of pictures you've taken."]])

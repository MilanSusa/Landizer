# Landizer - Landmark Recognizer

Landizer is a Clojure/ClojureScript based Luminus progressive web application (PWA) that uses [Landmark Recognition Inference API][1]
in order to provide user the ability to identify famous and infamous landmarks by taking a picture of them.

## Prerequisites

1) You will need [Leiningen][2] 2.0 or above installed.
2) Create a Firebase project which will provide storage for images.
3) Follow the steps for running [Landmark Recognition Inference API][1].

[1]: https://github.com/MilanSusa/Landmark-Recognition-Inference-API
[2]: https://github.com/technomancy/leiningen

## Running locally

Create `dev-config.edn` file in you root directory that contains the following content:

    {:dev true
     :port 3000
     :nrepl-port 7000
     :database-url "postgresql://localhost/{db-name}?user={username}&password={password}"}
     
and replace `{db-name}`, `{username}` and `{password}` with your configuration.

Located in /src/cljs/containers/recognize.cljs, find the following line of code:

    (defonce firebase-project-id "{your-firebase-project-id}")

and replace `{your-firebase-project-id}` with id of your Firebase project.

Execute the following code to run init migrations:

    lein run migrate

To start a web server for the application, run:

    lein run 
    
In another terminal, run the following line:

    lein figwheel

## License

Licensed under the [MIT License](LICENSE).
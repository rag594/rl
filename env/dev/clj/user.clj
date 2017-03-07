(ns user
  (:require [mount.core :as mount]
            [play.figwheel :refer [start-fw stop-fw cljs]]
            play.core))

(defn start []
  (mount/start-without #'play.core/http-server
                       #'play.core/repl-server))

(defn stop []
  (mount/stop-except #'play.core/http-server
                     #'play.core/repl-server))

(defn restart []
  (stop)
  (start))



(ns play.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [play.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[play started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[play has shut down successfully]=-"))
   :middleware wrap-dev})

(ns play.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [play.core-test]))

(doo-tests 'play.core-test)


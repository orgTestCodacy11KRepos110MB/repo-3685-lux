;; This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
;; If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.

(ns leiningen.lux
  (:require [leiningen.pom :as pom]
            [leiningen.core.classpath :as classpath]
            (leiningen.lux [builder :as &builder]
                           [test :as &test]
                           [repl :as &repl]
                           [watch :as &watch])))

;; [Exports]
(defn lux [project & args]
  (case args
    ["build"] (&builder/build project)
    ["test"] (&test/test project)
    ["repl"] (&repl/repl project)
    ["auto" "build"] (&watch/watch #(&builder/build project) project)
    ["auto" "test"] (&watch/watch #(&test/test project) project)
    ;; default...
    (println "Commands available: (auto) build, (auto) test, repl")))

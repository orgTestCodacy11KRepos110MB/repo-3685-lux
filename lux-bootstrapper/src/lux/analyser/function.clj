;; This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
;; If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.

(ns lux.analyser.function
  (:require clojure.core.match
            clojure.core.match.array
            (lux [base :as & :refer [|let |do return |case]]
                 [host :as &host])
            (lux.analyser [base :as &&]
                          [env :as &env])))

;; [Resource]
(defn with-function [self self-type arg arg-type body]
  (&/with-closure
    (|do [scope-name &/get-scope-name]
      (&env/with-local self self-type
        (&env/with-local arg arg-type
          (|do [=return body
                =captured &env/captured-vars]
            (return (&/T [scope-name =captured =return]))))))))

(defn close-over [scope name register frame]
  (|let [[[register-type register-location] _] register
         register* (&&/|meta register-type register-location
                             (&&/$captured (&/T [scope
                                                 (->> frame (&/get$ &/$captured) (&/get$ &/$counter))
                                                 register])))]
    (&/T [register* (&/update$ &/$captured #(->> %
                                                 (&/update$ &/$counter inc)
                                                 (&/update$ &/$mappings (fn [mps] (&/|put name (&/T [register-type register*]) mps))))
                               frame)])))

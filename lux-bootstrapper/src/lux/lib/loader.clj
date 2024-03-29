;; This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
;; If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.

(ns lux.lib.loader
  (:refer-clojure :exclude [load])
  (:require (lux [base :as & :refer [|let |do return return* |case]]))
  (:import (java.io InputStream
                    File
                    FileInputStream
                    ByteArrayInputStream
                    ByteArrayOutputStream)
           java.util.jar.JarInputStream))

;; [Utils]
(let [init-capacity (* 100 1024)
      buffer-size 1024]
  (defn ^:private ^"[B" read-stream [^InputStream is]
    (let [buffer (byte-array buffer-size)]
      (with-open [os (new ByteArrayOutputStream init-capacity)]
        (loop [bytes-read (.read is buffer 0 buffer-size)]
          (when (not= -1 bytes-read)
            (do (.write os buffer 0 bytes-read)
              (recur (.read is buffer 0 buffer-size)))))
        (.toByteArray os)))))

(defn ^:private unpackage [^File lib-file]
  (let [is (->> lib-file
                (new FileInputStream)
                (new JarInputStream))]
    (loop [lib-data {}
           entry (.getNextJarEntry is)]
      (if entry
        (if (.endsWith (.getName entry) ".lux")
          (recur (assoc lib-data (.getName entry) (new String (read-stream is)))
                 (.getNextJarEntry is))
          (recur lib-data
                 (.getNextJarEntry is)))
        lib-data))))

;; [Exports]
(defn load [dependencies]
  (->> dependencies
       &/->seq
       (map #(->> ^String % (new File) unpackage))
       (reduce merge {})))

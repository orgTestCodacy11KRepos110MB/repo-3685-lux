;; This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
;; If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.

(def version "0.6.0-SNAPSHOT")
(def repo "https://github.com/LuxLang/lux")
(def sonatype "https://oss.sonatype.org")
(def sonatype-releases (str sonatype "/service/local/staging/deploy/maven2/"))
(def sonatype-snapshots (str sonatype "/content/repositories/snapshots/"))

(defproject com.github.luxlang/lux-r #=(identity version)
  :description "An R compiler for Lux."
  :url ~repo
  :license {:name "Lux License v0.1.1"
            :url ~(str repo "/blob/master/license.txt")}
  :plugins [[com.github.luxlang/lein-luxc ~version]]
  :deploy-repositories [["releases" {:url ~sonatype-releases :creds :gpg}]
                        ["snapshots" {:url ~sonatype-snapshots :creds :gpg}]]
  :pom-addition [:developers [:developer
                              [:name "Eduardo Julian"]
                              [:url "https://github.com/eduardoejp"]]]
  :repositories [["releases" ~sonatype-releases]
                 ["snapshots" ~sonatype-snapshots]
                 ["bedatadriven" "https://nexus.bedatadriven.com/content/groups/public/"]
                 ["jitpack" "https://jitpack.io"]]
  :scm {:name "git"
        :url ~(str repo ".git")}

  :dependencies [[com.github.luxlang/luxc-jvm ~version]
                 [com.github.luxlang/stdlib ~version]
                 [org.renjin/renjin-script-engine "3.5-beta43"]]
  
  :manifest {"lux" ~version}
  :source-paths ["source"]
  :lux {:program "program"
        :test "test/program"}
  )

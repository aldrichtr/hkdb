;;; build.clj --- Build runner for hkdb -*- mode: clojure -*-


(ns build
  "Build library for hkdb."
  (:require [clojure.tools.build.api :as b]
            [clojure.tools.deps :as t]
            [clojure.tools.gitlibs :as git]
            [clojure.pprint :refer [pprint]]))

(def project
  "Project configuration to support build tasks."
  {:lib 'io.github.aldrichtr/hkdb
   :name "hkdb"
   :version "0.1.0"
   :repo "https://github.com/aldrichtr/hkdb"
   :main 'hkdb.core})

;;; Jar file
(defn- pom-template [version]
  [[:description "Manage Hotkeys across all your applications."]
   [:url "https://github.com/aldrichtr/hkdb"]
   [:licenses
    [:license
     [:name "Eclipse Public License"]
     [:url "http://www.eclipse.org/legal/epl-v10.html"]]]
   [:developers [:developer [:name "Timothy Aldrich"]]]
   [:scm
    [:url "https://github.com/aldrichtr/hkdb"]
    [:connection "scm:git:https://github.com/aldrichtr/hkdb.git"]
    [:developerConnection "scm:git:ssh:git@github.com:aldrichtr/hkdb.git"]
    [:tag (str "v" version)]]])

(defn- jar-opts
  "Options that are used by the `jar' function."
  [opts]
  ;;(let [version (or (:version opts) (:version project))])
  (let [opts (merge opts project)]
    (assoc opts
           :jar-file (format "target/%s-%s.jar" (:name opts) (:version opts))
           :uber-file (format "target/%s-%s-standalone.jar" (:name opts) (:version opts))
           :basis (b/create-basis opts)
           :class-dir "target/classes"
           :target "target"
           :target-dir "target/classes"
           :path "target"
           :src-dirs ["src" "resources"]
           :pom-data (pom-template (:version opts)))))


(defn print-jar-opts [opts]
  "A means of validating the jar options."
  (pprint (jar-opts opts)))


(defn jar
  "Build a jar file for the current project."
  [opts]
  (let [opts (jar-opts opts)]
    (println "Cleaning target dir")
    (b/delete {:path (:class-dir opts)})
    (println "\nWriting pom.xml")
    (b/write-pom opts)
    (println "\nCopying source...")
    (b/copy-dir {:src-dirs (:src-dirs opts) :target-dir (:class-dir opts)})
    (println "\nWriting" (:jar-file opts))
    (b/jar opts))
  ;; return original opts for chaining:
  opts)

(defn uberjar
  "Create an archive containing Clojure and the build of the project
  Merge command line configuration to the default project config"
  [opts]
  (let [opts (jar-opts opts)]
    (println "Cleaning target dir")
    (b/delete {:path (:class-dir opts)})

    (println "\nCopying source...")
    (b/copy-dir opts)

    (println "\nCompiling sources")
    (b/compile-clj opts)

    (println "\nCreating uberjar")
    (b/uber opts)))

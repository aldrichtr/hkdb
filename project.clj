(defproject hkdb "0.1.0"
  ;; project details
  :description "A keybinding database application"
  :url "https://github.com/aldrichtr/hkdb"
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [cli-matic "0.5.4"]
                 [org.clojure/tools.cli "1.1.230"]
                 [com.datomic/local "1.0.291"]]

  ;; skip-aot is ahead-of-time compile
  :main ^:skip-aot hkdb.core
  :target-path "out/%s"

  :profiles {:dev {:debug true}
             :dependencies [[org.clojure/test.check "1.1.0"]]}
             

  :jvm-opts ["-Djdk.attach.allowAttachSelf"
             "-Dclojure.spec.skip-macros=true"
             "--sun-misc-unsafe-memory-access=allow"]

  :release-tasks []
  :prep-tasks [])



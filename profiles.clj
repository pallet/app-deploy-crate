{:dev
 {:dependencies [[com.palletops/pallet "0.8.0-beta.10" :classifier "tests"]
                 [com.palletops/crates "0.1.0"]
                 [com.palletops/java-crate "0.8.0-beta.4"]
                 [com.palletops/runit-crate "0.8.0-alpha.1"]
                 [ch.qos.logback/logback-classic "1.0.9"]
                 [org.slf4j/jcl-over-slf4j "1.7.3"]]
  :exclusions [commons.logging]
  :plugins [[com.palletops/pallet-lein "0.6.0-beta.9"]
            [com.palletops/lein-pallet-crate "0.1.0"]
            [lein-resource "0.3.2"]
            [lein-set-version "0.3.0"]]
  :aliases {"live-test-up"
            ["pallet" "up"
             "--phases" "install,configure,test"
             "--selector" "live-test"]
            "live-test-down" ["pallet" "down" "--selector" "live-test"]
            "live-test" ["do" "live-test-up," "live-test-down"]}}
 :doc {:dependencies [[com.palletops/pallet-codox "0.1.0"]]
       :plugins [[codox/codox.leiningen "0.6.4"]
                 [lein-marginalia "0.7.1"]]
       :codox {:writer codox-md.writer/write-docs
               :output-dir "doc/0.8/api"
               :src-dir-uri "https://github.com/pallet/app-deploy-crate/blob/develop"
               :src-linenum-anchor-prefix "L"}
       :aliases {"marg" ["marg" "-d" "doc/0.8/annotated"]
                 "codox" ["doc"]
                 "doc" ["do" "codox," "marg"]}
       }
 :pallet {:jvm-opts ["-Djna.nosys=true"]}
 :release
 {:set-version
  {:updates [{:path "README.md" :no-snapshot true}
             {:path "resources/pallet_crate/app_deploy_crate/meta.edn"
              :no-snapshot true}]}}}

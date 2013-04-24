(defproject com.palletops/app-deploy-crate "0.8.0-alpha.1"
  :description "A crate to help deploy applications"
  :url "http://palletops.com/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.palletops/maven-resolver "0.1.0"]
                 [com.palletops/pallet "0.8.0-beta.9"]]
  :resource {:resource-paths ["doc-src"]
             :target-path "target/classes/pallet_crate/app_deploy_crate/"
             :includes [#"doc-src/USAGE.*"]}
  :prep-tasks ["resource" "crate-doc"])

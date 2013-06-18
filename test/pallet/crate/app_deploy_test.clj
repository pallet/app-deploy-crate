(ns pallet.crate.app-deploy-test
  (:require
   [clojure.test :refer :all]
   [pallet.api :refer [group-spec plan-fn]]
   [pallet.actions :refer [exec-script*]]
   [pallet.crate.app-deploy :as app-deploy]
   [pallet.crate.app-deploy.resolve-artifacts :refer [resolve-artifacts]]
   [pallet.crate.java :as java]
   [pallet.crate.runit :as runit]
   [pallet.script-test :refer [is-true testing-script]]))


(def test-spec
  (group-spec "appdeploy"
    :extends [(java/server-spec {})
              (runit/server-spec {})
              (app-deploy/server-spec
               {:app-root "/opt/myapp"
                :artifacts
                {:from-lein
                 [{:project-path
                   "target/app-deploy-crate-%s.jar"
                   :path "example.jar"}]}
                :run-command "java -jar /opt/myapp/example.jar"})
              (app-deploy/server-spec
               {:artifacts
                {:from-maven-repo
                 [{:coord '[com.palletops/maven-resolver "0.1.0"]
                   :path "resolver.jar"}]}
                :run-command "java -jar /opt/second/resolver.jar"}
               :instance-id :second)]
    :phases {:test (plan-fn
                     (exec-script*
                      (testing-script "App deploy"
                        (is-true
                         (file-exists? "/opt/myapp/example.jar")
                         "Verify that lein resolved jar is deployed")
                        (is-true
                         (file-exists? "/etc/sv/default-app/run")
                         "Verify default-app service")
                        (is-true
                         (file-exists? "/opt/second/resolver.jar")
                         "Verify that maven-repo resolved jar is deployed")
                        (is-true
                         (file-exists? "/etc/sv/second/run")
                         "Verify named app service"))))}
    :roles #{:live-test :deploy}))

(deftest resolve-artifacts-test
  (is (every? (comp string? :local-file)
              (resolve-artifacts
                :from-maven-repo
                [{:coord '[com.palletops/maven-resolver "0.1.0"]
                  :path "resolver.jar"}]
                {:repositories {}})))
  (is (every? (comp string? :local-file)
              (resolve-artifacts
                :from-lein
                [{:project-path "target/app-deploy-crate-%s.jar"
                  :path "example.jar"}]
                {:project {}}))))

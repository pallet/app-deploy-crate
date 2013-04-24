(ns pallet.crate.app-deploy.lein
  "Resolve artifacts for deploying via lein command line.

Each artifact map should provide at least a `:project-path` entry. The
`:project-path` entry may contain a \"%s\", which will be replaced with the
project's version string.

  {:project-path \"target/app-%s-standalone.jar\"
   :path  \"/opt/myapp/simple-app.jar\" ; Path to install to
                                        ; (relative or absolute)
   :unpack false}                       ; flag to unpack the artifact"
  (:require
   [clojure.tools.logging :refer [debugf]]
   [pallet.crate.app-deploy.resolve-artifacts :refer [resolve-artifacts]]))

;; Takes a sequence of artifact maps and resolves them.  Returns the artifact
;; map sequence, with `:file` entries for the resolved files.
(defmethod resolve-artifacts :from-lein
  [_ artifact-maps {:keys [project]}]
  (when-not project
    (throw (ex-info
            "No :project passed in the environment, impossible to deploy")))
  (debugf "Deploy from lein")
  (let [version (:version project)]
    (mapv
     #(assoc % :local-file (format (:project-path %) version))
     artifact-maps)))

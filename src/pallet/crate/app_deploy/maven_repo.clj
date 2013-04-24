(ns pallet.crate.app-deploy.maven-repo
  "Resolve artifacts from maven repositories, using lein style coordinate
vectors.  Each artifact map should provide at least a :coord entry.

  {:coord [simple-app 0.1.0-SNAPSHOT :classifier standalone]
   :path  \"/opt/myapp/simple-app.jar\" ; Path to install to
                                        ; (relative or absolute)
   :unpack false}                       ; flag to unpack the artifact"
  (:require
   [clojure.tools.logging :refer [debugf]]
   [com.palletops.maven-resolver :refer [resolve-coordinates]]
   [pallet.crate.app-deploy.resolve-artifacts :refer [resolve-artifacts]]))

;; Takes a sequence of artifact maps and resolves them.  Returns the artifact
;; map sequence, with `:file` entries for the resolved files.
(defmethod resolve-artifacts :from-maven-repo
  [_ artifact-maps {:keys [repositories local-repo] :as options}]
  (debugf "Deploy from maven-repo repositories : %s" (vec repositories))
  (let [coords (vec (map :coord artifact-maps))
        resolved (resolve-coordinates coords options)]
    (debugf "Deploy from maven-repo resolve-config coords : %s  resolved : %s"
            coords resolved)
    (mapv #(assoc %1 :local-file %2) artifact-maps resolved)))

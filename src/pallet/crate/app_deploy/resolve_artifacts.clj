(ns pallet.crate.app-deploy.resolve-artifacts
  "Resolve artifacts for deploying")

(defmulti resolve-artifacts (fn [method artifact-maps options] method))

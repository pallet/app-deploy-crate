(ns pallet.crate.app-deploy
  "A pallet crate to deploy applications"
  (:require
   [clojure.java.io :refer [file]]
   [clojure.string :as string]
   [clojure.tools.logging :refer [debugf]]
   [pallet.actions :refer [content-options directory package package-manager
                           package-source remote-directory remote-file]]
   [pallet.api :refer [cluster-spec group-spec node-spec plan-fn] :as api]
   [pallet.crate :refer [admin-user assoc-settings defmethod-plan defplan
                         get-settings]]
   [pallet.crate.app-deploy.lein]
   [pallet.crate.app-deploy.maven-repo]
   [pallet.crate.app-deploy.resolve-artifacts :refer [resolve-artifacts]]
   [pallet.crate.automated-admin-user :refer [automated-admin-user]]
   [pallet.crate.service
    :refer [service-supervisor-config supervisor-config supervisor-config-map]
    :as service]
   [pallet.environment :refer [get-environment]]
   [pallet.utils :refer [apply-map]]))

(defn default-settings []
  {:supervisor :runit
   :app-root "/opt"})

(defmethod supervisor-config-map [:app-deploy :runit]
  [_ {:keys [run-command service-name user service-log-dir] :as settings} options]
  {:pre [service-name]}
  (debugf "supervisor-config-map %s" settings)
  {:service-name service-name
   :run-file {:content (str "#!/bin/sh\nexec chpst -u " user " " run-command)}
   :log-run-file {:content (str "#!/bin/sh\nexec chpst -u " user " svlogd -tt " service-log-dir)}})

(defmethod supervisor-config-map [:app-deploy :nohup]
  [_ {:keys [run-command service-name user] :as settings} options]
  {:pre [service-name]}
  {:service-name service-name
   :run-file {:content run-command}
   :user user})

(defplan settings
    "Settings for an application.

`:app-root`
: the root of the install location (default /opt)

`:artifacts`
: a sequence of artifact maps.  An artifact map is keyed on resolver,
  with :from-lein and :from-maven-repo resolvers being supported.  All resolvers
  accept a `:path`, which is the path relative to `:app-root` or an absolute
  path, and optionally an `:unpack` flag, which can be set to unpack an archive.
  The `:from-lein` resolver expects a `:project-path`, a path to a project
  artifact, which may contain a \"%s\" which will be substituted with the
  project version.  The `:from-maven-repo` resolver expects a `:coord`, which is
  a leiningen style coordinate vector.

`:repositories`
: a map of leiningen style repository definitions, used by the
  `:from-maven-repo` resolver.

`:supervision`
: a keyword selecting the supervision service to be used.  Defaults to
  `:runit`.

`:run-command`
: a string that can be used as a command to start the application.

`:user`
: the user to use for running the application."
  [{:keys [app-root runit run-command supervisor user artifacts]
    :as settings}
   {:keys [instance-id] :as options}]
  (let [{:keys [app-root] :as settings} (merge (default-settings) settings)
        settings (-> settings
                     (update-in [:user] #(or % (:username (admin-user))))
                     (update-in [:service-name]
                                #(or % (name (or instance-id :default-app))))
                     (update-in [:service-log-dir]
                                #(str "/var/log/"
                                      (or % (name (or instance-id
                                                      :default-app))))))]
    (assoc-settings :app-deploy settings options)
    (supervisor-config
     :app-deploy settings
     (assoc options :instance-id (or instance-id :default-app)))))

(defn project-repositories
  [project]
  (let [repos (apply merge
                     (mapv #(into {} (second %))
                           (select-keys project
                                        [:repositories :deploy-repositories])))]
    (zipmap
     (map first repos)
     (->> (map second repos)
          (map (fn [repo]
                 (reduce
                  (fn [result k]
                    (debugf "project-repositories %s %s" k (get result k))
                    (if (string? (get result k))
                      result
                      (dissoc result k)))
                  repo
                  [:username :password :passphrase :creds])))))))

(defplan deploy
  "Deploy an application"
  [resolve-method {:keys [instance-id] :as options}]
  (let [{:keys [app-root artifacts user] :as settings} (get-settings
                                                        :app-deploy options)
        resolve-method (or resolve-method (key (first artifacts)))
        project (get-environment [:project] {})
        artifacts (resolve-artifacts
                   resolve-method (get artifacts resolve-method)
                   (merge
                    {:repositories (project-repositories project)}
                    (assoc settings :project project)))]
    (doseq [{:keys [unpack path] :as artifact} artifacts
            :let [app-path (let [f (file path)]
                             (.getAbsolutePath
                              (if (.isAbsolute f)
                                f
                                (file app-root f))))]]
      (debugf "deploy %s : %s to %s" instance-id (pr-str artifact) app-path)
      (directory (.getParent (file app-path)) :mode "755")
      (if unpack
        (apply-map remote-directory app-path :owner user
                   (select-keys artifact content-options))
        (apply-map remote-file app-path :owner user
                   (select-keys artifact content-options))))))

(defplan service
  "Run an application under service management."
  [& {:keys [action if-flag if-stopped instance-id]
      :or {action :manage}
      :as options}]
  (let [{:keys [supervision-options] :as settings}
        (get-settings :app-deploy {:instance-id instance-id})]
    (debugf "service %s settings %s" instance-id settings)
    (service/service
     settings
     (merge supervision-options (dissoc options :instance-id)))))

(defn- kw-for [prefix app-kw]
  (keyword (str prefix "-" (name app-kw))))

(defn- from-key [from]
  (keyword (string/replace (name from) #"^:" "")))

(defn server-spec
  "Define a server spec for an application.

The deploy phase is defined to deploy the application, either from a lein
project, or from artifacts in a maven repository.

To control the application, the server-spec has phases for start, stop and
restart, as well as application specific start-<app>, stop-<app> and
restart-<app>.

Settings are as described in the `settings` function."
  [{:as settings} & {:keys [instance-id] :as options}]
  (let [service-fn (fn [kw]
                     [[kw
                       (plan-fn (apply-map service :action kw options))]
                      [(kw-for (name kw) (or instance-id :default-app))
                       (plan-fn (apply-map service :action kw options))]])]
    (api/server-spec
     :phases (merge
              {:settings (plan-fn
                           (pallet.crate.app-deploy/settings settings options))

               :configure (plan-fn

                           (let [{:keys [service-log-dir user]}
                                 (get-settings :app-deploy
                                               {:instance-id
                                                (or instance-id :default-app)})]
                             (directory service-log-dir :owner user))

                           (apply-map service :action :enable options))
               :deploy (fn
                         ([] (deploy nil options))
                         ([from] (deploy (from-key from) options)))}
              (into {} (mapcat service-fn [:start :stop :restart])))
     :roles #{:app :clojure-app (or instance-id :default-app)})))

## Usage

The `server-spec` function provides an easy way to deploy and control an
application.  It takes a map of options specifying the application artifact
sources and destinations, etc.  The name of the service for the application, and
the install directory under `:app-root` are both taken from the `:instance-id`
keyword.

The options are as described in the
[`settings`](http://palletops.com/app-deploy-crate/0.8/api/pallet.crate.app-deploy.html#var-settings)
function.

The `deploy` phase deploys using the first defined deploy method by default, but
can be passed an argument to specify the artifact source, as either `:from-lein`
or `:from-maven-repo`.

To control the application, `start`, `stop` and `restart` phases are defined, as
well as instacnce specific `start-<instance-id>`, `stop-<instance-id>` and
`restart-<instance-id>`.

The default supervision is with `runit`.

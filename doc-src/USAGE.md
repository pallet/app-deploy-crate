## Usage

The `server-spec` function provides an easy way to deploy and control an
application.  It takes a map of options specifying the application artifact
sources and destinations, etc.

The options are as described in the 
[`settings`](http://palletops.com/app-deploy-crate/0.8/api/pallet.crate.app-deploy.html#var-settings)
function.

The `deploy` phase deploys using the first defined deploy method by default, but
can be passed an argument to specify the artifact source, as either `:from-lein`
or `:from-maven-repo`.

The default supervision is with `runit`.

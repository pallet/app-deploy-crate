[Repository](https://github.com/pallet/app-deploy-crate) &#xb7;
[Issues](https://github.com/pallet/app-deploy-crate/issues) &#xb7;
[API docs](http://palletops.com/app-deploy-crate/0.8/api) &#xb7;
[Annotated source](http://palletops.com/app-deploy-crate/0.8/annotated/uberdoc.html) &#xb7;
[Release Notes](https://github.com/pallet/app-deploy-crate/blob/develop/ReleaseNotes.md)

A [pallet](http://palletops.com/) crate to install and control applications.

### Dependency Information

```clj
:dependencies [[com.palletops/app-deploy-crate "0.8.0-alpha.3"]]
```

### Releases

<table>
<thead>
  <tr><th>Pallet</th><th>Crate Version</th><th>Repo</th><th>GroupId</th></tr>
</thead>
<tbody>
  <tr>
    <th>0.8.0-beta.10</th>
    <td>0.8.0-alpha.3</td>
    <td>clojars</td>
    <td>com.palletops</td>
    <td><a href='https://github.com/pallet/app-deploy-crate/blob/0.8.0-alpha.3/ReleaseNotes.md'>Release Notes</a></td>
    <td><a href='https://github.com/pallet/app-deploy-crate/blob/0.8.0-alpha.3/'>Source</a></td>
  </tr>
</tbody>
</table>

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

## Acknowledgments

This crate is based on work for 313 Ventures, that was released under the EPL
license.  Many thanks to 313 Ventures.

## License

Copyright © 2013 Hugo Duncan

Distributed under the Eclipse Public License.

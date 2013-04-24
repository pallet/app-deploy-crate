[Repository](https://github.com/pallet/app-deploy-crate) &#xb7;
[Issues](https://github.com/pallet/app-deploy-crate/issues) &#xb7;
[API docs](http://palletops.com/app-deploy-crate/0.8/api) &#xb7;
[Annotated source](http://palletops.com/app-deploy-crate/0.8/annotated/uberdoc.html) &#xb7;
[Release Notes](https://github.com/pallet/app-deploy-crate/blob/develop/ReleaseNotes.md)

A [pallet](http://palletops.com/) crate to install and control applications.

### Dependency Information

```clj
:dependencies [[com.palletops/app-deploy-crate "0.8.0-alpha.1"]]
```

### Releases

<table>
<thead>
  <tr><th>Pallet</th><th>Crate Version</th><th>Repo</th><th>GroupId</th></tr>
</thead>
<tbody>
  <tr>
    <th>0.8.0-beta.9</th>
    <td>0.8.0-alpha.1</td>
    <td>clojars</td>
    <td>com.palletops</td>
    <td><a href='https://github.com/pallet/app-deploy-crate/blob/0.8.0-alpha.1/ReleaseNotes.md'>Release Notes</a></td>
    <td><a href='https://github.com/pallet/app-deploy-crate/blob/0.8.0-alpha.1/'>Source</a></td>
  </tr>
</tbody>
</table>

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

## Acknowledgments

This crate is based on work for 313 Ventures, that was released under the EPL
license.  Many thanks to 313 Ventures.

## License

Copyright © 2013 Hugo Duncan

Distributed under the Eclipse Public License.

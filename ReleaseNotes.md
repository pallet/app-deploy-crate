# 0.8.0-alpha.3

- Normalize output of 'resolve-artifacts
  Calling (resolve-artifacts :from-maven-repo ...) was returning artifacts
  such as:

  [{:local-file #<java.io.File> ...} ...].

  'pallet.crate.app-deploy/deploy passes these maps to 'remote-file, however
  'remote-file has a schema, 'remote-file-arguments, that stipulates
  :local-file string?

# 0.8.0-alpha.2

- Add runit service startup logging using svlogd

- Don't blow up if there's no :project (i.e.: run outside of pallet-lein)

- Update to pallet-0.8.0-beta.10

# 0.8.0-alpha.1

- Initial Version

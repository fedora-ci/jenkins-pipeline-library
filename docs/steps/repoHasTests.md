# repoHasTests() step

This step clones the given repository and checks if there are any supported (STI/tmt) tests inside.

If the `useCloneCredentials` option is used, then the credentials stored in the `GIT_CLONE_AUTH_STRING`
environment variable will be used to clone the repository.

## Parameters

* **repoUrl**: string; repository URL
* **ref**: string; git reference
* **useCloneCredentials**: boolean; (optional) use clone credentials (default: false)

## Example Usage

```groovy
def repoTests = repoHasTests(repoUrl: 'https://src.fedoraproject.org/rpms/tmt.git', ref: 'e2d36dbb871c9faf9dae3d85c3ec8ed993a37f5c')
echo "${repoTests['type']}"  // prints "tmt"
echo "${repoTests['files']}" // prints ".fmf/version"
```

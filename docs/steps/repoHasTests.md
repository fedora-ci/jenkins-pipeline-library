# repoHasTests() step

This step clones the given repository and checks if there are any supported (STI/TMT) tests inside.

## Parameters

* **repoUrl**: string; repository URL
* **ref**: string; git reference

## Example Usage

```groovy
def repoTests = repoHasTests(repoUrl: 'https://src.fedoraproject.org/rpms/tmt.git', ref: 'e2d36dbb871c9faf9dae3d85c3ec8ed993a37f5c')
echo "${repoTests['type']}"  // prints "tmt"
echo "${repoTests['files']}" // prints ".fmf/version"
```

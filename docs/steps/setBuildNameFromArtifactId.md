# setBuildNameFromArtifactId() step

This step takes an *artifact Id* (e.g.: "koji-build:4328752") and uses it to set the name of the current build to some sensible value.

The step returns the name of the artifact.

## Parameters

* **artifactId**: string; artifact Id
* **profile**: string; (optional) test profile name

## Example Usage

```groovy
setBuildNameFromArtifactId(artifactId: 'koji-build:4328752', profile: 'f36')
```

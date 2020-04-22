# setBuildNameFromArtifactId() step

This step takes an *artifact Id* (e.g.: "koji-build:4328752") and uses it to set the name of the current build to some sensible value.

## Parameters

* **artifactId**: string; artifact Id

## Example Usage

```groovy
setBuildNameFromArtifactId(artifactId: 'koji-build:4328752')
```

# isScratchBuild() step

This step checks whether given artifactId is a scratch build or not. This only works for koji-build and brew-build artifact types. All other artifact types always return "false".

## Parameters

* **artifactId**: string; artifactId

## Example Usage

```groovy
def isScratch = isScratchBuild(artifactId: 'koji-build:62674132')
```

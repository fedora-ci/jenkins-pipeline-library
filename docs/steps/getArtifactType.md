# getArtifactType() step

This step inspects given artifactId and returns its type.


## Parameters

* **artifactId**: string; artifact Id


## Example Usage

```groovy
def artifactType = getArtifactType('koji-build:123456')
echo "${artifactType}"  // prints "koji-build"
```

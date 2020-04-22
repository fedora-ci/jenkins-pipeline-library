# abort() step

Abort the build. This step sets build result to "ABORTED" (gray icon in Jenkins),
and immediately stops execution of the pipeline.

## Parameters

* **message**: string; A message for users explaining why the build was aborted

## Example Usage

```groovy
abort('ARTIFACT_ID is missing')
```

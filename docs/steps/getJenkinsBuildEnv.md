# getJenkinsBuildEnv() step

This step reads the environment variables from given Jenkins build.

## Parameters

* **jobName**: string; Jenkins job name
* **buildNumber**: int; Build number

## Example Usage

```groovy
def buildEnv = getJenkinsBuildEnv(jobName: 'test-compose/build-test-compose', buildNumber: 101)
```

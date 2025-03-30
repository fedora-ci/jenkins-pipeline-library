# findJenkinsBuildNumber() step

This step tries to find a build that matches given critria. It such build is found, the build number is returned. Otherwise this step returns null.

## Parameters

* **jobName**: string; Jenkins job name
* **buildParams**: map; (optional) Build parameters
* **isRunning**: boolean; (optional) Look for running builds
* **result**: string; (optional) Build result; default: 'SUCCESS'

## Example Usage

```groovy
def buildNumber = findJenkinsBuildNumber(jobName: 'test-compose/build-test-compose', buildParams: ['TASK_ID': '123456'])
```

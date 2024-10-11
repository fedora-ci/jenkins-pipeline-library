# waitForTestingFarm() step

This steps causes the running pipeline to pause and wait for a webhook from Testing Farm. The pipeline will resume once the Testing Farm will report the status of the request to be either complete or error.

## Parameters

* **requestId**: string; Testing Farm API request ID
* **hook**: (optional) hook object previously created by `registerWebhook()` step
* **suppressSslErrors**: boolean; (optional) ignore ssl errors (default: false)

## Example Usage

```groovy
def response = waitForTestingFarm(requestId: testingFarmRequestId, hook: hook)
def testingFarmResult = response.apiResponse
def xunit = response.xunit
```

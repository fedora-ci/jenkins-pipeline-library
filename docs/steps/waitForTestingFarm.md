# waitForTestingFarm() step

This steps causes the running pipeline to pause and wait for a webhook from Testing Farm. The pipeline will resume once the Testing Farm will report the status of the request to be either complete or error.

## Parameters

* **requestId**: string; Testing Farm API request ID
* **hook**: hook object previously created by `registerWebhook()` step

## Example Usage

```groovy
def response = waitForTestingFarm(requestId: testingFarmRequestId, hook: hook)
def testingFarmResult = response.apiResponse
def xunit = response.xunit
```

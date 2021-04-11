# waitForTestingFarmResults() step

**NOTE:**
This step is **deprecated**. Please use [waitForTestingFarm()](./waitForTestingFarm.md) instead.

This step polls Testing Farm API and checks the status of the given request. The pipeline will resume once the Testing Farm will report the status to be either "complete" or "error".

## Parameters

* **requestId**: string; Testing Farm API request ID

## Example Usage

```groovy
def apiResponse = waitForTestingFarmResults(requestId: testingFarmRequestId)
```

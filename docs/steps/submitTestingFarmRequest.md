# submitTestingFarmRequest() step

This steps submits request to the Testing Farm API.

## Parameters

* **payload**: payloadMap; Testing Farm request itself
* **suppressSslErrors**: boolean; (optional) ignore ssl errors (default: false)

## Example Usage

```groovy
def initial_response = submitTestingFarmRequest(payloadMap: requestPayload)
echo "Request ID is: ${initial_response['id']}"
```

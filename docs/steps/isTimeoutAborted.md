# isTimeoutAborted() step

This step checks if the currently running build has been timeout-aborted or not.


## Parameters

* **timeout**: int; timeout for the build
* **unit**: string, time unit, default: "SECONDS", other supported values are: "MINUTES", "SECONDS"

## Example Usage

```groovy
isTimeoutAborted(timeout: '60')
```

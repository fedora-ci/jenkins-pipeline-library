# buildId2taskId() step

This step translates given Koji build Id into the corresponding task Id.

## Parameters

* **type**: integer; Koji build Id

## Example Usage

```groovy
def taskId = buildId2taskId(1536155)
```

# getGatingRequirements() step

This step queries Greenwave and returns a list of required testcase names for given context and product-version.

It is possible to filter the requirements by testcase name, scenario, and/or scenario prefix.

## Parameters

* **artifactId**: string; artifact Id
* **decisionContext**: string; Greenwave decision context
* **productVersion**: string; Greenwave product version
* **testcasePrefix**: string; (optional) test case name prefix
* **testcase**: string; (optional) test case name
* **scenarioPrefix**: string; (optional) test scenario prefix
* **scenario**: string; (optional) test scenario

## Example Usage

```groovy
def requirements = getGatingRequirements(
    artifactId: 'koji-build:4328752',
    decisionContext: 'bodhi_update_push_testing',
    productVersion: 'f36'
)
```

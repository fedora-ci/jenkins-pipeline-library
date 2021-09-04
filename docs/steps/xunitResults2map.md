# xunitResults2map() step

This step extracts results from XUnit and returns them as a map.

Note the XUnit must be from Testing Farm, i.e. standard XUnit format is not supported.


## Parameters

* **xunit**: string; xunit from Testing Farm

## Example Usage

```groovy
def xunitResults = xunitResults2map(xunit: '<xunit-here>')
```

# getReleaseIdFromBranch() step

This step returns a release Id (e.g. "f31", or "rawhide") from the current branch. For non-production builds (i.e. for pull requests), it uses the target branch.

## Parameters

No parameters.

## Example Usage

```groovy
def releaseId = getReleaseIdFromBranch()
```

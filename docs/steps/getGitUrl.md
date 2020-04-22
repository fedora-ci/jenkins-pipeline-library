# getGitUrl() step

This step returns URL of the Git repository for the current build. For non-production builds (i.e. for pull requests), it returns the URL of the fork.

## Parameters

No parameters.

## Example Usage

```groovy
def gitUrl = getGitUrl()
```

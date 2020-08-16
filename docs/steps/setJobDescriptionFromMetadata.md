# setJobDescriptionFromMetadata() step

This step sets the description of a job in Jenkins.

## Parameters

* **pipelineMetadata**: map; pipeline metadata

## Example Usage

```groovy
def pipelineMetadata = [
    pipelineName: 'rpmdeplint',
    pipelineDescription: 'Finding errors in RPM packages in the context of their dependency graph',
    testCategory: 'functional',
    testType: 'rpmdeplint',
    maintainer: 'Fedora CI',
    docs: 'https://github.com/fedora-ci/rpmdeplint-pipeline',
    contact: [
        irc: '#fedora-ci',
        email: 'ci@lists.fedoraproject.org'
    ],
]

setJobDescriptionFromMetadata(pipelineMetadata: pipelineMetadata)
```

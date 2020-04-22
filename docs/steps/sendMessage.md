# sendMessage() step

This step sends a CI message to the message bus. For more information, check the [CI Messages](https://pagure.io/fedora-ci/messages) project.

**Warning** The API of this step will likely change in the near future.

## Prerequisites

This step requires the [JMS Messaging plugin](https://wiki.jenkins.io/display/JENKINS/JMS+Messaging+Plugin) to be installed and configured in Jenkins. This includes configuring at least one messaging provider in the global configuration of the plugin. This step takes the name of the messaging provider from the `MSG_PROVIDER` environment variable.

## Parameters

* **type**: string; Type of the message to send, valid types are: "queued", "running", "complete", "error"
* **artifactId**: string; artifact Id
* **pipelineMetadata**: map; metadata about the pipeline
* **dryRun**: boolean; if true, do not actually send the message, just log it

## Example Usage

```groovy
sendMessage(type: 'queued', artifactId: artifactId, pipelineMetadata: pipelineMetadata, dryRun: isPullRequest())
```

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
* **topic**: string; (optional) name of the topic where to send the message
* **messageProvider**: string; (optional) name of the topic the message provider to use
* **xunit**: string; (optional) xunit with results
* **xunitUrls**: list; (optional) a list of URLs pointing to xunit files
* **runUrl**: string; (optional) URL that will appear in the message instead of the URL of the Jenkins build
* **runLog**: string; (optional) Log URL that will appear in the message instead of the URL of the Jenkins console
* **isSkipped**: boolean; [**DEPRECATED**: please use "isInfo" instead] (optional) indicator whether this test was skipped or not; a "note" param can be used to provide an explanation on why the test was skipped
* **isInfo**: boolean; (optional) indicator whether this result is just informational or not; note: this field is only applicable for "complete" messages
* **note**: string; (optional) arbitrary note about the test result
* **testScenario**: string; (optional) name of the test scenario
* **errorReason**: string; (optional) a reason why the testing failed; note: this field is only applicable for "error" messages
* **testType**: string; (optional) test type (e.g.: "tier0")
* **testResult**: string; (optional) test result (e.g.: "passed", "needs_inspection")
* **testProfile**: string: (optional) name of the test profile
* **nvr**: string: (optional) Report results for this NVR, instead of the artifact Id


## Example Usage

```groovy
sendMessage(type: 'queued', artifactId: artifactId, pipelineMetadata: pipelineMetadata, dryRun: isPullRequest())
```

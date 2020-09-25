# Fedora CI pipeline library

This git repository contains a library of reusable [Jenkins Pipeline](https://jenkins.io/doc/book/pipeline/) steps and functions that can be used in your Jenkinsfile to make your Fedora CI pipelines simple.

## How to use this library

This library is intended to be used as a [global shared library](https://jenkins.io/doc/book/pipeline/shared-libraries/#global-shared-libraries) configured in your Jenkins instance.

It is recommended to pin down a specific version of the library and update it cautiously.

To use the steps in this library just add the following to the top of your Jenkinsfile:

```jenkinsfile
@Library('fedora-pipeline-library') _
```

The snippet above assumes that you decided to use the name `fedora-pipeline-library` when configuring the library in the global settings.

## Steps

This is a list of all steps implemented in the library. Click on a particular step to find out more about it.

* [abort()](./docs/steps/abort.md)
* [buildId2taskId()](./docs/steps/buildId2taskId.md)
* [buildImageAndPushToRegistry()](./docs/steps/buildImageAndPushToRegistry.md)
* [getGitUrl()](./docs/steps/getGitUrl.md)
* [getReleaseIdFromBranch()](./docs/steps/getReleaseIdFromBranch.md)
* [isProduction()](./docs/steps/isProduction.md)
* [isPullRequest()](./docs/steps/isPullRequest.md)
* [sendMessage()](./docs/steps/sendMessage.md)
* [setBuildNameFromArtifactId()](./docs/steps/setBuildNameFromArtifactId.md)
* [setJobDescriptionFromMetadata()](./docs/steps/setJobDescriptionFromMetadata.md)

## Limitations

Fedora CI is "artifact-centric", i.e. everything more or less evolves around artifacts. This library currently supports only "koji-build" and "fedora-dist-git" artifacts; modules and other artifact types are not supported yet.

## Development

If you'd want to implement more steps, see the [official documentation](https://jenkins.io/doc/book/pipeline/shared-libraries/) first.

One thing not covered by the official documentation is unit testing. This library has some tests in the _test/groovy/_ directory. In order to run them, you will need [Apache Maven](http://maven.apache.org/) (packaged in Fedora!).

To run the tests, type:

```shell
mvn clean test
```

### Coding style

Jenkinsfiles are almost Groovy and Groovy is almost Java. And Java is camelCase ;)

### What belongs in here

Re-implementing the world in Groovy is not what we want here. The goal is to have simple and maintainable CI pipelines.

Sometimes it may be better (and more future-proof) to implement what you need in Python and just run in CI in a container. In the future, when we switch to [OpenShift/Tekton Pipelines](https://www.openshift.com/learn/topics/pipelines) we will just take such Python implementation with us.

However, if what you're trying to do involves Jenkins APIs and/or internals (i.e. it is tied to Jenkins), and it is relatively simple to do in Groovy, then putting it in here may be a good idea.

Also, if your pipeline step is a function and you need to use the return value from it later in the pipeline, then implementing this step as an external command in your favorite language means that you will likely need to parse standard output of that command in the pipeline. Which doesn't exactly scream simple and maintainable :)

Use your own judgment!

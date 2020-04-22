# buildImageAndPushToRegistry() step

This pipeline step builds a container image from a given Git repository and pushes it to an external registry.

## Prerequisites

This step requires the [OpenShift Client plugin](https://plugins.jenkins.io/openshift-client/) to be installed and configured in Jenkins.

## Parameters

* **imageName**: string; Name of the resulting image, e.g.: "quay.io/fedoraci/rpmdeplint"
* **imageTag**: string; Tag for the image, e.g.: "latest", or "d57f6a4"
* **pushSecret**: string; Name of the OpenShift secret that will be used to push the image to the registry (this is NOT a Jenkins secret)
* **gitUrl**: string; URL pointing to a Git repository, e.g.: "https://github.com/fedora-ci/rpmdeplint-image.git"
* **gitRef**: string; Git reference, e.g.: "master", or "ca8eca526ababc4ffae29dfac8e6d222687e368b" (default is "master")
* **noCache**: boolean; Flag whether caching should be enabled during the build (default is true)
* **forcePull**: boolean; Flag whether builder should force-pull base image before the build (default is true).
* **dockerfilePath**: string; Path to the Dockerfile in the repository (default is "Dockerfile")
* **openshiftProject**: string; Name of the OpenShift project where to build the image
* **buildName**: string; Arbitrary name that will be used as a name for the OpenShift build config

## Example Usage

```groovy
 buildImageAndPushToRegistry(
    imageName: 'quay.io/fedoraci/rpmdeplint',
    imageTag: 'd57f6a4',
    pushSecret: 'quay',
    gitUrl: 'https://github.com/fedora-ci/rpmdeplint-image.git',
    gitRef: 'master',
    buildName: 'rpmdeplint-image',
    openshiftProject: 'fedora-ci'
)
```

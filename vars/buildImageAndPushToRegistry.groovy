#!/usr/bin/groovy


/**
 * buildImageAndPushToRegistry() step.
 *
 * The long name says it all — this pipeline step builds a container image
 * from the given Git repository and pushes it to a registry.
 *
 * @param imageName Name of the resulting image, e.g.: "quay.io/fedoraci/rpmdeplint".
 * @param imageTag Tag for the image, e.g.: "latest", or "d57f6a4".
 * @param pushSecret Name of the OpenShift secret that will be used to push the image to the registry (this is NOT a Jenkins secret).
 * @param gitUrl URL pointing to a Git repository, e.g.: "https://github.com/fedora-ci/rpmdeplint-image.git".
 * @param gitRef Git reference, e.g.: "master", or "ca8eca526ababc4ffae29dfac8e6d222687e368b"; default is "master".
 * @param noCache Flag whether caching should be enabled during the build; default is true.
 * @param forcePull Flag whether builder should force-pull base image before the build; default is true.
 * @param dockerfilePath Path to the Dockerfile in the repository; default is "Dockerfile".
 * @param openshiftProject Name of the OpenShift project where to build the image.
 * @param buildName Arbitrary name that will be used as a name for the OpenShift build config.
 *
 * Example usage:
 *
 * buildImageAndPushToRegistry(
 *    imageName: 'quay.io/fedoraci/rpmdeplint',
 *    imageTag: 'd57f6a4',
 *    pushSecret: 'quay',
 *    gitUrl: 'https://github.com/fedora-ci/rpmdeplint-image.git',
 *    gitRef: 'master',
 *    buildName: 'rpmdeplint-image',
 *    openshiftProject: 'fedora-ci'
 * )
 */
def call(Map params = [:]) {
    // TODO:
    // * buildName needs to be more unique
    // * try/catch and delete bc in "finally"

    // Explicit is better than implicit... they say.
    def openshiftProject = params.get('openshiftProject', env.OPENSHIFT_PROJECT)
    def imageName = params.get('imageName')
    def imageTag = params.get('imageTag')
    def pushSecret = params.get('pushSecret', env.REGISTRY_PUSH_SECRET_NAME)
    def gitUrl = params.get('gitUrl')
    def gitRef = params.get('gitRef', 'master')
    def noCache = params.get('noCache', true)
    def forcePull = params.get('forcePull', true)
    def dockerfilePath = params.get('dockerfilePath', 'Dockerfile')
    // Make buildName somewhat unique
    def buildName = "${params.get('buildName')}-${env.BUILD_NUMBER}"

    // Build config template; without any triggers — we will start the build manually
    def buildConfig = """
apiVersion: build.openshift.io/v1
kind: BuildConfig
metadata:
  labels:
    build: ${buildName}
  name: ${buildName}
spec:
  output:
    to:
      kind: DockerImage
      name: ${imageName}:${imageTag}
    pushSecret:
      name: ${pushSecret}
  postCommit: {}
  runPolicy: Serial
  source:
    git:
      uri: ${gitUrl}
      ref: ${gitRef}
    type: Git
  strategy:
    type: Docker
    dockerStrategy:
      dockerfilePath: ${dockerfilePath}
      noCache: ${noCache}
      forcePull: ${forcePull}
      env:
      - name: GIT_SSL_NO_VERIFY
        value: "true"
  successfulBuildsHistoryLimit: 2
  failedBuildsHistoryLimit: 2
"""

    openshift.withCluster() {  // "openshift" variable is provided by "Jenkins OpenShift Client" plugin
        openshift.withProject(openshiftProject) {
            // Create the build config in OpenShift
            openshift.apply("${buildConfig}")
            // Start the build and wait for it to finish
            // TODO: we should capture stdout/stderr and print it
            openshift.startBuild("${buildName}", "--follow=true")
            // Delete the build config
            openshift.selector( 'bc', [ build: "${buildName}" ] ).delete()

            return imageName + ':' + imageTag
        }
    }
}

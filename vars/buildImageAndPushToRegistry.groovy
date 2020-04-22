#!/usr/bin/groovy


/**
 * buildImageAndPushToRegistry() step.
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

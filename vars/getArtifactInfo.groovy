#!/usr/bin/groovy

import org.fedoraproject.jenkins.Utils
import org.fedoraproject.jenkins.koji.Koji


/**
 * getArtifactInfo() step.
 */
def call(Map params = [:]) {

    def artifactId = params.get('artifactId', '')
    def additionalArtifactIds = params.get('additionalArtifactIds', '')

    if (Utils.isCompositeArtifact(artifactId)) {
        artifactId = artifactId.split('->')[0] - '(' - ')'
    }

    def artifactIds = []

    if (artifactId) {
        artifactId.split(',').each { a ->
            artifactIds.add(a)
        }
    }

    if (additionalArtifactIds) {
        additionalArtifactIds.split(',').each { a ->
            artifactIds.add(a)
        }
    }

    def koji = new Koji(env.KOJI_API_URL)
    def taskId
    def taskInfo
    def artifactsInfo = [:]
    artifactIds.each { a ->
        taskId = a.split(':')[1]
        taskInfo = koji.getTaskInfo(taskId.toInteger())
        artifactsInfo["${a}"] = [
            name: "${taskInfo.name}".toString(),
            nvr: "${taskInfo.nvr}".toString(),
            id: "${taskInfo.id}"
        ]
    }

    return artifactsInfo
}

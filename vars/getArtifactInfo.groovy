#!/usr/bin/groovy

import org.fedoraproject.jenkins.Utils
import org.fedoraproject.jenkins.koji.Koji
import org.fedoraproject.jenkins.mbs.Mbs


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
    def mbs = new Mbs(env.FEDORA_CI_MBS_URL)
    def taskId
    def taskInfo
    def moduleInfo
    def artifactType
    def artifactsInfo = [:]
    artifactIds.each { a ->
        artifactType = a.split(':')[0]
        taskId = a.split(':')[1]
        if (artifactType in ['koji-build', 'brew-build']) {
            taskInfo = koji.getTaskInfo(taskId.toInteger())
            artifactsInfo[a.toString()] = [
                name: "${taskInfo.name}".toString(),
                nvr: "${taskInfo.nvr}".toString(),
                id: "${taskInfo.id}"
            ]
        } else {
            moduleInfo = mbs.getModuleBuildInfo(taskId)
            artifactsInfo[a.toString()] = [
                name: "${moduleInfo.name}".toString(),
                nvr: "${mbs.getModuleNVR(moduleInfo)}".toString(),
                id: "${moduleInfo.id}"
            ]
        }
    }

    return artifactsInfo
}

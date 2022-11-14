#!/usr/bin/groovy

import org.fedoraproject.jenkins.Utils
import org.fedoraproject.jenkins.koji.Koji


/**
 * isScratchBuild() step.
 */
def call(Map params = [:]) {

    def artifactId = params.get('artifactId', false)

    def targetArtifactId
    if (Utils.isCompositeArtifact(artifactId)) {
        targetArtifactId = Utils.getTargetArtifactId(artifactId)
    } else {
        targetArtifactId = artifactId
    }

    def artifactType = targetArtifactId.split(':')[0]
    def taskId = targetArtifactId.split(':')[1]

    if (artifactType in ['koji-build', 'brew-build']) {
        def koji = new Koji(env.KOJI_API_URL)
        def taskInfo = koji.getTaskInfo(taskId.toInteger())

        if (taskInfo.scratch) {
            return true
        }
    }

    return false
}

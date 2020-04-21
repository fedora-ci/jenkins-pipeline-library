#!/usr/bin/groovy

import org.fedoraproject.jenkins.koji.Koji

/**
 * setBuildNameFromArtifactId() step.
 *
 * Set name of the build in Jenkins.
 *
 * @return Nothing
 */
def call(Map params = [:]) {
    def artifactId = params.get('artifactId')

    def artifactType = artifactId.split(':')[0]
    def taskId = artifactId.split(':')[1]

    def displayName

    if (artifactType == 'koji-build') {
        def koji = new Koji()
        def taskInfo = koji.getTaskInfo(taskId.toInteger())
        displayName = "[${artifactType}] ${taskInfo.nvr}"
        if (taskInfo.scratch) {
            displayName = "[scratch] ${displayName}"
        }
    }

    currentBuild.displayName = displayName
}

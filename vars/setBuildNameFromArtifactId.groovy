#!/usr/bin/groovy

import org.fedoraproject.jenkins.koji.Koji

/**
 * setBuildNameFromArtifactId() step.
 */
def call(Map params = [:]) {
    def artifactId = params.get('artifactId')
    def displayName

    if (!artifactId) {
        currentBuild.displayName = '[pipeline update]'
        return
    }

    try {
        def artifactType = artifactId.split(':')[0]
        def taskId = artifactId.split(':')[1]

        if (artifactType == 'koji-build') {
            def koji = new Koji()
            def taskInfo = koji.getTaskInfo(taskId.toInteger())
            displayName = "[${artifactType}] ${taskInfo.nvr}"
            if (taskInfo.scratch) {
                displayName = "[scratch] ${displayName}"
            }
        } else {
            displayName = "UNKNOWN ARTIFACT TYPE: '${artifactType}'"
        }
    } catch (Exception ex) {
        error("${ex}")
        displayName = "INVALID ARTIFACT ID: '${artifactId}'"
    }

    currentBuild.displayName = displayName
}

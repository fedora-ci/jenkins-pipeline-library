#!/usr/bin/groovy

import org.fedoraproject.jenkins.koji.Koji
import org.fedoraproject.jenkins.pagure.Pagure
import org.fedoraproject.jenkins.Utils


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

        if (Utils.isCompositeArtifact(artifactId)) {
            artifactId = Utils.getTargetArtifactId(artifactId)
        }

        def artifactType = artifactId.split(':')[0]
        def taskId = artifactId.split(':')[1]

        if (artifactType == 'koji-build') {
            def koji = new Koji()
            def taskInfo = koji.getTaskInfo(taskId.toInteger())
            displayName = "[${artifactType}] ${taskInfo.nvr}"
            if (taskInfo.scratch) {
                displayName = "[scratch] ${displayName}"
            }
        } else if (artifactType == 'fedora-update') {
            displayName = "[${artifactType}] ${taskId}"
        } else if (artifactType == 'fedora-dist-git') {
            // handle pull-requests
            def pagure = new Pagure()
            def pullRequestInfo = pagure.getPullRequestInfo(taskId)
            def fullname = pullRequestInfo.get('project', [:])?.get('fullname') ?: 'unknown'
            def pullRequestId = pullRequestInfo.get('id', 0)
            def shortCommit = pagure.splitPullRequestId(taskId)['commitId'][0..6]
            displayName = "[${artifactType}] ${fullname}#${pullRequestId}@${shortCommit}"
        } else {
            displayName = "UNKNOWN ARTIFACT TYPE: '${artifactType}'"
        }
    } catch (Exception ex) {
        error("${ex}")
        displayName = "INVALID ARTIFACT ID: '${artifactId}'"
    }

    currentBuild.displayName = displayName
}

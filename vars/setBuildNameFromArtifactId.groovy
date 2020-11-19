#!/usr/bin/groovy

import org.fedoraproject.jenkins.koji.Koji
import org.fedoraproject.jenkins.pagure.Pagure
import org.fedoraproject.jenkins.Utils


/**
 * setBuildNameFromArtifactId() step.
 */
def call(Map params = [:]) {
    def artifactId = params.get('artifactId')
    def profileName = params.get('profile')
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

        if (artifactType in ['koji-build', 'brew-build']) {
            def koji = new Koji(env.KOJI_API_URL)
            def taskInfo = koji.getTaskInfo(taskId.toInteger())
            displayName = "[${artifactType}] ${taskInfo.nvr}"
            if (taskInfo.scratch) {
                displayName = "[scratch] ${displayName}"
            }
        } else if (artifactType == 'fedora-update') {
            displayName = "[${artifactType}] ${taskId}"
        } else if (artifactType in ['fedora-dist-git', 'dist-git-pr']) {
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
    if (profileName) {
        currentBuild.description = "test profile: ${profileName}"
    }
}

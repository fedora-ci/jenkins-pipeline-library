#!/usr/bin/groovy

import org.fedoraproject.jenkins.koji.Koji
import org.fedoraproject.jenkins.pagure.Pagure
import org.fedoraproject.jenkins.mbs.Mbs
import org.fedoraproject.jenkins.Utils


/**
 * setBuildNameFromArtifactId() step.
 */
def call(Map params = [:]) {
    def artifactId = params.get('artifactId')
    def profileName = params.get('profile')
    def scenarioName = params.get('scenario')
    def displayName
    def packageName = ''

    if (!artifactId) {
        currentBuild.displayName = '[pipeline update]'
        return packageName.toString()
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
            packageName = "${taskInfo.name}"
            if (taskInfo.scratch) {
                displayName = "[scratch] ${displayName}"
            }
        } else if (artifactType == 'fedora-update') {
            displayName = "[${artifactType}] ${taskId}"
        } else if (artifactType == 'redhat-module') {
            def mbs = new Mbs(env.FEDORA_CI_MBS_URL)
            def moduleInfo = mbs.getModuleBuildInfo(taskId)
            displayName = "[${artifactType}] ${mbs.getModuleName(moduleInfo)}"
        } else if (artifactType in ['fedora-dist-git', 'dist-git-pr']) {
            // handle pull-requests
            def pagure = new Pagure(env.FEDORA_CI_PAGURE_DIST_GIT_URL)
            def pullRequestInfo = pagure.getPullRequestInfo(taskId)
            def fullname = pullRequestInfo.get('project', [:])?.get('fullname') ?: 'unknown'
            def pullRequestId = pullRequestInfo.get('id', 0)
            def commitId = pagure.splitPullRequestId(taskId)['commitId']
            def shortCommit = commitId
            if (commitId.length() >= 7) {
                shortCommit = pagure.splitPullRequestId(taskId)['commitId'][0..6]
            }
            displayName = "[${artifactType}] ${fullname}#${pullRequestId}@${shortCommit}"
            packageName = "${pullRequestInfo.get('project', [:])?.get('name') ?: 'unknown'}"
        } else {
            displayName = "UNKNOWN ARTIFACT TYPE: '${artifactType}'"
        }
    } catch (Exception ex) {
        error("${ex}")
        displayName = "INVALID ARTIFACT ID: '${artifactId}'"
    }

    currentBuild.displayName = displayName
    def description = ""
    if (profileName) {
        description += "test profile: ${profileName}\n"
    }
    if (scenarioName) {
        description += "scenario: ${scenarioName}\n"
    }
    if (description) {
        currentBuild.description = description
    }

    return packageName.toString()
}

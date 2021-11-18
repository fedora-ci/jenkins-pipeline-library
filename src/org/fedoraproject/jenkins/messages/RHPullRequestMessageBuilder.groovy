package org.fedoraproject.jenkins.messages

import groovy.json.JsonOutput
import groovy.json.JsonSlurperClassic

import org.fedoraproject.jenkins.pagure.Pagure
import org.fedoraproject.jenkins.Utils


def buildMessageQueued(
    String artifactType,
    String taskId,
    Map pipelineMetadata,
    String scenario,
    String testType,
    String testProfile
) {
    // OSCI is not sending queued messages for pull requests (?)
    return [:]
}


def buildMessageRunning(
    String artifactType,
    String taskId,
    Map pipelineMetadata,
    String scenario,
    String testType,
    String testProfile
) {
    def msgTemplate

    def msgTemplateString = libraryResource 'rh-pull-request.test.running-template.json'
    msgTemplate = new groovy.json.JsonSlurperClassic().parseText(msgTemplateString)

    // CI/contact section
    msgTemplate['ci']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['ci']['team'] = pipelineMetadata['maintainer']
    msgTemplate['ci']['url'] = pipelineMetadata['docs']
    msgTemplate['ci']['docs'] = pipelineMetadata['docs']
    msgTemplate['ci']['irc'] = pipelineMetadata['contact']['irc']
    msgTemplate['ci']['email'] = pipelineMetadata['contact']['email']

    // artifact section
    def pagure = new Pagure(env.FEDORA_CI_PAGURE_DIST_GIT_URL)
    def pullRequestInfo = pagure.getPullRequestInfo(taskId)
    def uidCommitAndComment = pagure.splitPullRequestId(taskId)

    msgTemplate['artifact']['type'] = 'dist-git-pr'
    msgTemplate['artifact']['repository'] = "${pagure.url}/${pullRequestInfo.get('project', [:])?.get('fullname')}"
    msgTemplate['artifact']['id'] = pullRequestInfo.get('id')
    msgTemplate['artifact']['comment_id'] = uidCommitAndComment.get('commentId')
    msgTemplate['artifact']['commit_hash'] = uidCommitAndComment.get('commitId')
    msgTemplate['artifact']['issuer'] = pullRequestInfo.get('user', [:])?.get('name')
    msgTemplate['artifact']['uid'] = pullRequestInfo.get('uid')

    // test section
    msgTemplate['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.dist-git-pr"
    msgTemplate['category'] = pipelineMetadata['testCategory']
    msgTemplate['type'] = testType ?: pipelineMetadata['testType']
    msgTemplate['docs'] = pipelineMetadata['docs']
    if (scenario) {
        // msgTemplate['test']['scenario'] = scenario
        // OSCI-2612: we need to append the scenario to the test case name
        msgTemplate['category'] += "/scenario=${scenario}"
    }
    if (testProfile) {
        // this is a non-standard field
        msgTemplate['profile'] = testProfile
    }

    // pipeline section
    msgTemplate['thread_id'] = Utils.generatePipelineIdFromArtifactIdAndTestcase(
        "${artifactType}:${taskId}",
        "${msgTemplate['namespace']}.${artifactType}.${msgTemplate['type']}.${msgTemplate['category']}"
    )

    // run section
    msgTemplate['run']['log'] = "${env.BUILD_URL}console"
    msgTemplate['run']['debug'] = "${env.BUILD_URL}console"
    msgTemplate['run']['rebuild'] = "${env.BUILD_URL}rebuild"
    msgTemplate['run']['url'] = "${env.BUILD_URL}"

    msgTemplate['system'] = []  // do we need this?

    // misc
    msgTemplate['generated_at'] = Utils.getTimestamp()

    msgTemplate['version'] = "0.1.0"

    return msgTemplate
}


def buildMessageComplete(
    String artifactType,
    String taskId,
    Map pipelineMetadata,
    String xunit,
    Boolean isSkipped,
    String note,
    String scenario,
    String testType,
    String testProfile
) {
    def msgTemplate

    def msgTemplateString = libraryResource 'rh-pull-request.test.complete-template.json'
    msgTemplate = new groovy.json.JsonSlurperClassic().parseText(msgTemplateString)

    // CI/contact section
    msgTemplate['ci']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['ci']['team'] = pipelineMetadata['maintainer']
    msgTemplate['ci']['url'] = pipelineMetadata['docs']
    msgTemplate['ci']['docs'] = pipelineMetadata['docs']
    msgTemplate['ci']['irc'] = pipelineMetadata['contact']['irc']
    msgTemplate['ci']['email'] = pipelineMetadata['contact']['email']

    // artifact section
    def pagure = new Pagure(env.FEDORA_CI_PAGURE_DIST_GIT_URL)
    def pullRequestInfo = pagure.getPullRequestInfo(taskId)
    def uidCommitAndComment = pagure.splitPullRequestId(taskId)

    msgTemplate['artifact']['type'] = 'dist-git-pr'
    msgTemplate['artifact']['repository'] = "${pagure.url}/${pullRequestInfo.get('project', [:])?.get('fullname')}"
    msgTemplate['artifact']['id'] = pullRequestInfo.get('id')
    msgTemplate['artifact']['comment_id'] = uidCommitAndComment.get('commentId')
    msgTemplate['artifact']['commit_hash'] = uidCommitAndComment.get('commitId')
    msgTemplate['artifact']['issuer'] = pullRequestInfo.get('user', [:])?.get('name')
    msgTemplate['artifact']['uid'] = pullRequestInfo.get('uid')

    // test section
    def result = 'needs_inspection'
    if (isSkipped) {
        result = 'info'
    } else if (currentBuild.result == 'SUCCESS') {
        result = 'passed'
    } else if (currentBuild.result == 'UNSTABLE') {
        result = 'needs_inspection'
    }

    msgTemplate['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.dist-git-pr"
    msgTemplate['category'] = pipelineMetadata['testCategory']
    msgTemplate['type'] = testType ?: pipelineMetadata['testType']
    msgTemplate['note'] = note
    msgTemplate['status'] = result
    msgTemplate['xunit'] = xunit
    msgTemplate['docs'] = pipelineMetadata['docs']
    if (scenario) {
        // msgTemplate['test']['scenario'] = scenario
        // OSCI-2612: we need to append the scenario to the test case name
        msgTemplate['category'] += "/scenario=${scenario}"
    }
    if (testProfile) {
        // this is a non-standard field
        msgTemplate['profile'] = testProfile
    }

    // pipeline section
    msgTemplate['thread_id'] = Utils.generatePipelineIdFromArtifactIdAndTestcase(
        "${artifactType}:${taskId}",
        "${msgTemplate['namespace']}.${artifactType}.${msgTemplate['type']}.${msgTemplate['category']}"
    )

    // run section
    msgTemplate['run']['url'] = "${env.BUILD_URL}"
    msgTemplate['run']['log'] = "${env.BUILD_URL}console"
    msgTemplate['run']['debug'] = "${env.BUILD_URL}console"
    msgTemplate['run']['rebuild'] = "${env.BUILD_URL}rebuild"

    msgTemplate['system'] = []  // do we need this?

    // misc
    msgTemplate['generated_at'] = Utils.getTimestamp()

    msgTemplate['version'] = "0.1.0"

    return msgTemplate
}


def buildMessageError(
    String artifactType,
    String taskId,
    Map pipelineMetadata,
    String xunit,
    String scenario,
    String errorReason,
    String testType,
    String testProfile
) {
    def msgTemplate

    def msgTemplateString = libraryResource 'rh-pull-request.test.error-template.json'
    msgTemplate = new groovy.json.JsonSlurperClassic().parseText(msgTemplateString)

    // CI/contact section
    msgTemplate['ci']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['ci']['team'] = pipelineMetadata['maintainer']
    msgTemplate['ci']['url'] = pipelineMetadata['docs']
    msgTemplate['ci']['docs'] = pipelineMetadata['docs']
    msgTemplate['ci']['irc'] = pipelineMetadata['contact']['irc']
    msgTemplate['ci']['email'] = pipelineMetadata['contact']['email']

    // artifact section
    def pagure = new Pagure(env.FEDORA_CI_PAGURE_DIST_GIT_URL)
    def pullRequestInfo = pagure.getPullRequestInfo(taskId)
    def uidCommitAndComment = pagure.splitPullRequestId(taskId)

    msgTemplate['artifact']['type'] = 'dist-git-pr'
    msgTemplate['artifact']['repository'] = "${pagure.url}/${pullRequestInfo.get('project', [:])?.get('fullname')}"
    msgTemplate['artifact']['id'] = pullRequestInfo.get('id')
    msgTemplate['artifact']['comment_id'] = uidCommitAndComment.get('commentId')
    msgTemplate['artifact']['commit_hash'] = uidCommitAndComment.get('commitId')
    msgTemplate['artifact']['issuer'] = pullRequestInfo.get('user', [:])?.get('name')
    msgTemplate['artifact']['uid'] = pullRequestInfo.get('uid')

    msgTemplate['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.dist-git-pr"
    msgTemplate['category'] = pipelineMetadata['testCategory']
    msgTemplate['type'] = testType ?: pipelineMetadata['testType']
    msgTemplate['status'] = "failed"
    msgTemplate['xunit'] = xunit
    msgTemplate['docs'] = pipelineMetadata['docs']
    if (scenario) {
        // msgTemplate['test']['scenario'] = scenario
        // OSCI-2612: we need to append the scenario to the test case name
        msgTemplate['category'] += "/scenario=${scenario}"
    }
    if (testProfile) {
        // this is a non-standard field
        msgTemplate['profile'] = testProfile
    }

    // pipeline section
    msgTemplate['thread_id'] = Utils.generatePipelineIdFromArtifactIdAndTestcase(
        "${artifactType}:${taskId}",
        "${msgTemplate['namespace']}.${artifactType}.${msgTemplate['type']}.${msgTemplate['category']}"
    )

    // run section
    msgTemplate['run']['log'] = "${env.BUILD_URL}console"
    msgTemplate['run']['debug'] = "${env.BUILD_URL}console"
    msgTemplate['run']['rebuild'] = "${env.BUILD_URL}rebuild"
    msgTemplate['run']['url'] = "${env.BUILD_URL}"

    msgTemplate['system'] = []  // do we need this?

    // misc
    msgTemplate['generated_at'] = Utils.getTimestamp()

    msgTemplate['version'] = "0.1.0"

    return msgTemplate
}

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
    def msgTemplate

    def msgTemplateString = libraryResource 'pull-request.test.queued-template.json'
    msgTemplate = new groovy.json.JsonSlurperClassic().parseText(msgTemplateString)

    // contact section
    msgTemplate['contact']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['contact']['team'] = pipelineMetadata['maintainer']
    msgTemplate['contact']['url'] = pipelineMetadata['docs']
    msgTemplate['contact']['docs'] = pipelineMetadata['docs']
    msgTemplate['contact']['irc'] = pipelineMetadata['contact']['irc']
    msgTemplate['contact']['email'] = pipelineMetadata['contact']['email']

    // run section
    msgTemplate['run']['url'] = "${env.BUILD_URL}"
    msgTemplate['run']['log'] = "${env.BUILD_URL}console"
    msgTemplate['run']['log_raw'] = "${env.BUILD_URL}consoleText"
    msgTemplate['run']['log_stream'] = "${env.BUILD_URL}console"

    // artifact section
    def pagure = new Pagure(env.FEDORA_CI_PAGURE_DIST_GIT_URL)
    def pullRequestInfo = pagure.getPullRequestInfo(taskId)
    def uidCommitAndComment = pagure.splitPullRequestId(taskId)

    msgTemplate['artifact']['type'] = 'pull-request'
    msgTemplate['artifact']['repository'] = "${pagure.url}/${pullRequestInfo.get('project', [:])?.get('fullname')}"
    msgTemplate['artifact']['id'] = pullRequestInfo.get('id')
    msgTemplate['artifact']['comment_id'] = uidCommitAndComment.get('commentId')
    msgTemplate['artifact']['commit_hash'] = uidCommitAndComment.get('commitId')
    msgTemplate['artifact']['issuer'] = pullRequestInfo.get('user', [:])?.get('name')
    msgTemplate['artifact']['uid'] = pullRequestInfo.get('uid')

    // test section
    msgTemplate['test']['type'] = testType ?: pipelineMetadata['testType']
    msgTemplate['test']['category'] = pipelineMetadata['testCategory']
    msgTemplate['test']['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.dist-git-pr"
    msgTemplate['test']['docs'] = pipelineMetadata['docs']
    if (scenario) {
        msgTemplate['test']['scenario'] = scenario
    }
    if (testProfile) {
        // this is a non-standard field
        msgTemplate['test']['profile'] = testProfile
    }

    // misc
    msgTemplate['generated_at'] = Utils.getTimestamp()

    return msgTemplate
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

    def msgTemplateString = libraryResource 'pull-request.test.running-template.json'
    msgTemplate = new groovy.json.JsonSlurperClassic().parseText(msgTemplateString)

    // contact section
    msgTemplate['contact']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['contact']['team'] = pipelineMetadata['maintainer']
    msgTemplate['contact']['url'] = pipelineMetadata['docs']
    msgTemplate['contact']['docs'] = pipelineMetadata['docs']
    msgTemplate['contact']['irc'] = pipelineMetadata['contact']['irc']
    msgTemplate['contact']['email'] = pipelineMetadata['contact']['email']

    // run section
    msgTemplate['run']['url'] = "${env.BUILD_URL}"
    msgTemplate['run']['log'] = "${env.BUILD_URL}console"
    msgTemplate['run']['log_raw'] = "${env.BUILD_URL}consoleText"
    msgTemplate['run']['log_stream'] = "${env.BUILD_URL}console"

    // artifact section
    def pagure = new Pagure(env.FEDORA_CI_PAGURE_DIST_GIT_URL)
    def pullRequestInfo = pagure.getPullRequestInfo(taskId)
    def uidCommitAndComment = pagure.splitPullRequestId(taskId)

    msgTemplate['artifact']['type'] = 'pull-request'
    msgTemplate['artifact']['repository'] = "${pagure.url}/${pullRequestInfo.get('project', [:])?.get('fullname')}"
    msgTemplate['artifact']['id'] = pullRequestInfo.get('id')
    msgTemplate['artifact']['comment_id'] = uidCommitAndComment.get('commentId')
    msgTemplate['artifact']['commit_hash'] = uidCommitAndComment.get('commitId')
    msgTemplate['artifact']['issuer'] = pullRequestInfo.get('user', [:])?.get('name')
    msgTemplate['artifact']['uid'] = pullRequestInfo.get('uid')

    // test section
    msgTemplate['test']['type'] = testType ?: pipelineMetadata['testType']
    msgTemplate['test']['category'] = pipelineMetadata['testCategory']
    msgTemplate['test']['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.dist-git-pr"
    msgTemplate['test']['docs'] = pipelineMetadata['docs']
    if (scenario) {
        msgTemplate['test']['scenario'] = scenario
    }
    if (testProfile) {
        // this is a non-standard field
        msgTemplate['test']['profile'] = testProfile
    }

    // misc
    msgTemplate['generated_at'] = Utils.getTimestamp()

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

    def msgTemplateString = libraryResource 'pull-request.test.complete-template.json'
    msgTemplate = new groovy.json.JsonSlurperClassic().parseText(msgTemplateString)

    // contact section
    msgTemplate['contact']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['contact']['team'] = pipelineMetadata['maintainer']
    msgTemplate['contact']['url'] = pipelineMetadata['docs']
    msgTemplate['contact']['docs'] = pipelineMetadata['docs']
    msgTemplate['contact']['irc'] = pipelineMetadata['contact']['irc']
    msgTemplate['contact']['email'] = pipelineMetadata['contact']['email']

    // artifact section
    def pagure = new Pagure(env.FEDORA_CI_PAGURE_DIST_GIT_URL)
    def pullRequestInfo = pagure.getPullRequestInfo(taskId)
    def uidCommitAndComment = pagure.splitPullRequestId(taskId)

    msgTemplate['artifact']['type'] = 'pull-request'
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

    msgTemplate['test']['type'] = testType ?: pipelineMetadata['testType']
    msgTemplate['test']['category'] = pipelineMetadata['testCategory']
    msgTemplate['test']['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.dist-git-pr"
    msgTemplate['test']['note'] = note
    msgTemplate['test']['result'] = result
    msgTemplate['test']['xunit'] = xunit
    msgTemplate['test']['docs'] = pipelineMetadata['docs']
    if (scenario) {
        msgTemplate['test']['scenario'] = scenario
    }
    if (testProfile) {
        // this is a non-standard field
        msgTemplate['test']['profile'] = testProfile
    }

    // run section
    if (msgTemplate['test']['xunit']) {
        msgTemplate['run']['url'] = "${env.BUILD_URL}testReport/(root)/tests/"
    } else {
        msgTemplate['run']['url'] = "${env.BUILD_URL}"
    }
    msgTemplate['run']['log'] = "${env.BUILD_URL}console"
    msgTemplate['run']['log_raw'] = "${env.BUILD_URL}consoleText"
    msgTemplate['run']['log_stream'] = "${env.BUILD_URL}console"
    msgTemplate['run']['debug'] = "${env.BUILD_URL}console"
    msgTemplate['run']['rebuild'] = "${env.BUILD_URL}rebuild"

    msgTemplate['system'] = []  // do we need this?

    // misc
    msgTemplate['generated_at'] = Utils.getTimestamp()

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

    def msgTemplateString = libraryResource 'pull-request.test.error-template.json'
    msgTemplate = new groovy.json.JsonSlurperClassic().parseText(msgTemplateString)

    // contact section
    msgTemplate['contact']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['contact']['team'] = pipelineMetadata['maintainer']
    msgTemplate['contact']['url'] = pipelineMetadata['docs']
    msgTemplate['contact']['docs'] = pipelineMetadata['docs']
    msgTemplate['contact']['irc'] = pipelineMetadata['contact']['irc']
    msgTemplate['contact']['email'] = pipelineMetadata['contact']['email']

    // run section
    msgTemplate['run']['url'] = "${env.BUILD_URL}"
    msgTemplate['run']['log'] = "${env.BUILD_URL}console"
    msgTemplate['run']['log_raw'] = "${env.BUILD_URL}consoleText"
    msgTemplate['run']['log_stream'] = "${env.BUILD_URL}console"
    msgTemplate['run']['debug'] = "${env.BUILD_URL}console"
    msgTemplate['run']['rebuild'] = "${env.BUILD_URL}rebuild"

    // artifact section
    def pagure = new Pagure(env.FEDORA_CI_PAGURE_DIST_GIT_URL)
    def pullRequestInfo = pagure.getPullRequestInfo(taskId)
    def uidCommitAndComment = pagure.splitPullRequestId(taskId)

    msgTemplate['artifact']['type'] = 'pull-request'
    msgTemplate['artifact']['repository'] = "${pagure.url}/${pullRequestInfo.get('project', [:])?.get('fullname')}"
    msgTemplate['artifact']['id'] = pullRequestInfo.get('id')
    msgTemplate['artifact']['comment_id'] = uidCommitAndComment.get('commentId')
    msgTemplate['artifact']['commit_hash'] = uidCommitAndComment.get('commitId')
    msgTemplate['artifact']['issuer'] = pullRequestInfo.get('user', [:])?.get('name')
    msgTemplate['artifact']['uid'] = pullRequestInfo.get('uid')

    // test section
    msgTemplate['test']['type'] = testType ?: pipelineMetadata['testType']
    msgTemplate['test']['category'] = pipelineMetadata['testCategory']
    msgTemplate['test']['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.dist-git-pr"
    msgTemplate['test']['result'] = 'failed'
    msgTemplate['test']['docs'] = pipelineMetadata['docs']
    if (scenario) {
        msgTemplate['test']['scenario'] = scenario
    }
    if (testProfile) {
        // this is a non-standard field
        msgTemplate['test']['profile'] = testProfile
    }

    // test section
    if (errorReason) {
        msgTemplate['error']['reason'] = errorReason
    } else {
        msgTemplate['error']['reason'] = env.ERROR_MESSAGE ? "${env.ERROR_MESSAGE}" : 'Infrastructure Failure'
    }
    msgTemplate['error']['url'] = "${env.BUILD_URL}console"

    // misc
    msgTemplate['generated_at'] = Utils.getTimestamp()

    return msgTemplate
}

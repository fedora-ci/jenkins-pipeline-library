package org.fedoraproject.jenkins.messages

import groovy.json.JsonOutput
import groovy.json.JsonSlurperClassic

import org.fedoraproject.jenkins.pagure.Pagure
import org.fedoraproject.jenkins.Utils


def buildMessageQueued(String artifactType, String taskId, Map pipelineMetadata) {

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

    // pipeline section
    msgTemplate['pipeline']['id'] = Utils.generatePipelineIdFromJobNameAndParams(env, params)
    msgTemplate['pipeline']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['pipeline']['build'] = "${env.BUILD_NUMBER}"

    // test section
    msgTemplate['test']['type'] = pipelineMetadata['testType']
    msgTemplate['test']['category'] = pipelineMetadata['testCategory']
    msgTemplate['test']['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.dist-git-pr"

    // misc
    msgTemplate['generated_at'] = Utils.getTimestamp()

    return msgTemplate
}


def buildMessageRunning(String artifactType, String taskId, Map pipelineMetadata) {

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

    // pipeline section
    msgTemplate['pipeline']['id'] = Utils.generatePipelineIdFromJobNameAndParams(env, params)
    msgTemplate['pipeline']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['pipeline']['build'] = "${env.BUILD_NUMBER}"

    // test section
    msgTemplate['test']['type'] = pipelineMetadata['testType']
    msgTemplate['test']['category'] = pipelineMetadata['testCategory']
    msgTemplate['test']['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.dist-git-pr"

    // misc
    msgTemplate['generated_at'] = Utils.getTimestamp()

    return msgTemplate
}


def buildMessageComplete(String artifactType, String taskId, Map pipelineMetadata, String xunit) {

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

    // pipeline section
    msgTemplate['pipeline']['id'] = Utils.generatePipelineIdFromJobNameAndParams(env, params)
    msgTemplate['pipeline']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['pipeline']['build'] = "${env.BUILD_NUMBER}"

    // test section
    def result = 'needs_inspection'
    if (currentBuild.result == 'SUCCESS') {
        result = 'passed'
    } else if (currentBuild.result == 'UNSTABLE') {
        result = 'needs_inspection'
    }

    msgTemplate['test']['type'] = pipelineMetadata['testType']
    msgTemplate['test']['category'] = pipelineMetadata['testCategory']
    msgTemplate['test']['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.dist-git-pr"
    msgTemplate['test']['note'] = ''
    msgTemplate['test']['result'] = result
    msgTemplate['test']['xunit'] = xunit

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


def buildMessageError(String artifactType, String taskId, Map pipelineMetadata, String xunit) {

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

    // pipeline section
    msgTemplate['pipeline']['id'] = Utils.generatePipelineIdFromJobNameAndParams(env, params)
    msgTemplate['pipeline']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['pipeline']['build'] = "${env.BUILD_NUMBER}"

    // test section
    msgTemplate['test']['type'] = pipelineMetadata['testType']
    msgTemplate['test']['category'] = pipelineMetadata['testCategory']
    msgTemplate['test']['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.dist-git-pr"
    msgTemplate['test']['result'] = 'failed'

    // test section
    msgTemplate['error']['reason'] = 'Infrastructure Failure'
    msgTemplate['error']['url'] = "${env.BUILD_URL}console"

    // misc
    msgTemplate['generated_at'] = Utils.getTimestamp()

    return msgTemplate
}

package org.fedoraproject.jenkins.messages

import groovy.json.JsonOutput
import groovy.json.JsonSlurperClassic

import org.fedoraproject.jenkins.pagure.Pagure
import org.fedoraproject.jenkins.Utils


def buildMessageQueued(String artifactType, String taskId, Map pipelineMetadata) {

    // OSCI is not sending queued messages for pull requests (?)
    return [:]
}


def buildMessageRunning(String artifactType, String taskId, Map pipelineMetadata) {

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

    // pipeline section
    msgTemplate['thread_id'] = Utils.generatePipelineIdFromJobNameAndParams(env, params)

    // test section
    msgTemplate['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.dist-git-pr"
    msgTemplate['category'] = pipelineMetadata['testCategory']
    msgTemplate['type'] = pipelineMetadata['testType']

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


def buildMessageComplete(String artifactType, String taskId, Map pipelineMetadata, String xunit) {

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

    // pipeline section
    msgTemplate['thread_id'] = Utils.generatePipelineIdFromJobNameAndParams(env, params)

    // test section
    def result = 'needs_inspection'
    if (currentBuild.result == 'SUCCESS') {
        result = 'passed'
    } else if (currentBuild.result == 'UNSTABLE') {
        result = 'needs_inspection'
    }

    msgTemplate['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.dist-git-pr"
    msgTemplate['category'] = pipelineMetadata['testCategory']
    msgTemplate['type'] = pipelineMetadata['testType']
    msgTemplate['status'] = result
    msgTemplate['xunit'] = xunit

    // run section
    if (msgTemplate['test']['xunit']) {
        msgTemplate['run']['url'] = "${env.BUILD_URL}testReport/(root)/tests/"
    } else {
        msgTemplate['run']['url'] = "${env.BUILD_URL}"
    }
    msgTemplate['run']['log'] = "${env.BUILD_URL}console"
    msgTemplate['run']['debug'] = "${env.BUILD_URL}console"
    msgTemplate['run']['rebuild'] = "${env.BUILD_URL}rebuild"

    msgTemplate['system'] = []  // do we need this?

    // misc
    msgTemplate['generated_at'] = Utils.getTimestamp()

    msgTemplate['version'] = "0.1.0"

    return msgTemplate
}


def buildMessageError(String artifactType, String taskId, Map pipelineMetadata, String xunit) {

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

    // pipeline section
    msgTemplate['thread_id'] = Utils.generatePipelineIdFromJobNameAndParams(env, params)

    msgTemplate['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.dist-git-pr"
    msgTemplate['category'] = pipelineMetadata['testCategory']
    msgTemplate['type'] = pipelineMetadata['testType']
    msgTemplate['status'] = "failed"
    msgTemplate['xunit'] = xunit

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

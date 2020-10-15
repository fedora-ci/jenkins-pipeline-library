package org.fedoraproject.jenkins.messages

import groovy.json.JsonOutput
import groovy.json.JsonSlurperClassic

import org.fedoraproject.jenkins.koji.Koji
import org.fedoraproject.jenkins.Utils


def buildMessageQueued(String artifactType, String taskId, Map pipelineMetadata) {

    def msgTemplate

    def msgTemplateString = libraryResource 'koji-build.test.queued-template.json'
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
    def koji = new Koji()
    def taskInfo = koji.getTaskInfo(taskId.toInteger())
    msgTemplate['artifact']['id'] = taskInfo.id
    msgTemplate['artifact']['issuer'] = taskInfo.ownerName
    msgTemplate['artifact']['component'] = taskInfo.name
    msgTemplate['artifact']['nvr'] = taskInfo.nvr
    msgTemplate['artifact']['scratch'] = taskInfo.scratch
    msgTemplate['artifact']['type'] = artifactType

    // pipeline section
    msgTemplate['pipeline']['id'] = Utils.generatePipelineIdFromJobNameAndParams(env, params)
    msgTemplate['pipeline']['name'] = pipelineMetadata['pipelineName']

    // test section
    msgTemplate['test']['type'] = pipelineMetadata['testType']
    msgTemplate['test']['category'] = pipelineMetadata['testCategory']
    msgTemplate['test']['namespace'] = "${pipelineMetadata['contact']['maintainer'].toLowerCase().replace(' ', '-')}.${artifactType}"

    // misc
    msgTemplate['generated_at'] = Utils.getTimestamp()

    return msgTemplate
}


def buildMessageRunning(String artifactType, String taskId, Map pipelineMetadata) {

    def msgTemplate

    def msgTemplateString = libraryResource 'koji-build.test.running-template.json'
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
    def koji = new Koji()
    def taskInfo = koji.getTaskInfo(taskId.toInteger())
    msgTemplate['artifact']['id'] = taskInfo.id
    msgTemplate['artifact']['issuer'] = taskInfo.ownerName
    msgTemplate['artifact']['component'] = taskInfo.name
    msgTemplate['artifact']['nvr'] = taskInfo.nvr
    msgTemplate['artifact']['scratch'] = taskInfo.scratch
    msgTemplate['artifact']['type'] = artifactType

    // pipeline section
    msgTemplate['pipeline']['id'] = Utils.generatePipelineIdFromJobNameAndParams(env, params)
    msgTemplate['pipeline']['name'] = pipelineMetadata['pipelineName']

    // test section
    msgTemplate['test']['type'] = pipelineMetadata['testType']
    msgTemplate['test']['category'] = pipelineMetadata['testCategory']
    msgTemplate['test']['namespace'] = "${pipelineMetadata['contact']['maintainer'].toLowerCase().replace(' ', '-')}.${artifactType}"

    // misc
    msgTemplate['generated_at'] = Utils.getTimestamp()

    return msgTemplate
}


def buildMessageComplete(String artifactType, String taskId, Map pipelineMetadata, String xunit) {

    def msgTemplate

    def msgTemplateString = libraryResource 'koji-build.test.complete-template.json'
    msgTemplate = new groovy.json.JsonSlurperClassic().parseText(msgTemplateString)

    // contact section
    msgTemplate['contact']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['contact']['team'] = pipelineMetadata['maintainer']
    msgTemplate['contact']['url'] = pipelineMetadata['docs']
    msgTemplate['contact']['docs'] = pipelineMetadata['docs']
    msgTemplate['contact']['irc'] = pipelineMetadata['contact']['irc']
    msgTemplate['contact']['email'] = pipelineMetadata['contact']['email']

    // artifact section
    def koji = new Koji()
    def taskInfo = koji.getTaskInfo(taskId.toInteger())
    msgTemplate['artifact']['id'] = taskInfo.id
    msgTemplate['artifact']['issuer'] = taskInfo.ownerName
    msgTemplate['artifact']['component'] = taskInfo.name
    msgTemplate['artifact']['nvr'] = taskInfo.nvr
    msgTemplate['artifact']['scratch'] = taskInfo.scratch
    msgTemplate['artifact']['baseline'] = taskInfo.nvr
    msgTemplate['artifact']['source'] = taskInfo.source.raw
    msgTemplate['artifact']['type'] = artifactType

    // pipeline section
    msgTemplate['pipeline']['id'] = Utils.generatePipelineIdFromJobNameAndParams(env, params)
    msgTemplate['pipeline']['name'] = pipelineMetadata['pipelineName']

    // test section
    def result = 'needs_inspection'
    if (currentBuild.result == 'SUCCESS') {
        result = 'passed'
    } else if (currentBuild.result == 'UNSTABLE') {
        result = 'needs_inspection'
    }

    msgTemplate['test']['type'] = pipelineMetadata['testType']
    msgTemplate['test']['category'] = pipelineMetadata['testCategory']
    msgTemplate['test']['namespace'] = "${pipelineMetadata['contact']['maintainer'].toLowerCase().replace(' ', '-')}.${artifactType}"
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

    def msgTemplateString = libraryResource 'koji-build.test.error-template.json'
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
    def koji = new Koji()
    def taskInfo = koji.getTaskInfo(taskId.toInteger())
    msgTemplate['artifact']['id'] = taskInfo.id
    msgTemplate['artifact']['issuer'] = taskInfo.ownerName
    msgTemplate['artifact']['component'] = taskInfo.name
    msgTemplate['artifact']['nvr'] = taskInfo.nvr
    msgTemplate['artifact']['scratch'] = taskInfo.scratch
    msgTemplate['artifact']['type'] = artifactType

    // pipeline section
    msgTemplate['pipeline']['id'] = Utils.generatePipelineIdFromJobNameAndParams(env, params)
    msgTemplate['pipeline']['name'] = pipelineMetadata['pipelineName']

    // test section
    msgTemplate['test']['type'] = pipelineMetadata['testType']
    msgTemplate['test']['category'] = pipelineMetadata['testCategory']
    msgTemplate['test']['namespace'] = "${pipelineMetadata['contact']['maintainer'].toLowerCase().replace(' ', '-')}.${artifactType}"
    msgTemplate['test']['result'] = 'failed'

    // test section
    msgTemplate['error']['reason'] = 'Infrastructure Failure'
    msgTemplate['error']['url'] = "${env.BUILD_URL}console"

    // misc
    msgTemplate['generated_at'] = Utils.getTimestamp()

    return msgTemplate
}

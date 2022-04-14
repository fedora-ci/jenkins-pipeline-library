package org.fedoraproject.jenkins.messages

import groovy.json.JsonOutput
import groovy.json.JsonSlurperClassic

import org.fedoraproject.jenkins.koji.Koji
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

    def msgTemplateString = libraryResource 'koji-build.test.queued-template.json'
    msgTemplate = new groovy.json.JsonSlurperClassic().parseText(msgTemplateString)

    // contact section
    msgTemplate['contact']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['contact']['team'] = pipelineMetadata['maintainer']
    msgTemplate['contact']['docs'] = pipelineMetadata['docs']
    msgTemplate['contact']['irc'] = pipelineMetadata['contact']['irc']
    msgTemplate['contact']['email'] = pipelineMetadata['contact']['email']

    // run section
    msgTemplate['run']['url'] = "${env.BUILD_URL}"
    msgTemplate['run']['log'] = "${env.BUILD_URL}console"
    msgTemplate['run']['log_raw'] = "${env.BUILD_URL}consoleText"
    msgTemplate['run']['log_stream'] = "${env.BUILD_URL}console"

    // artifact section
    def koji = new Koji(env.KOJI_API_URL)
    def taskInfo = koji.getTaskInfo(taskId.toInteger())
    msgTemplate['artifact']['id'] = taskInfo.id
    msgTemplate['artifact']['issuer'] = taskInfo.ownerName
    msgTemplate['artifact']['component'] = taskInfo.name
    msgTemplate['artifact']['nvr'] = taskInfo.nvr
    msgTemplate['artifact']['scratch'] = taskInfo.scratch
    msgTemplate['artifact']['type'] = artifactType

    // test section
    msgTemplate['test']['type'] = testType ?: pipelineMetadata['testType']
    msgTemplate['test']['category'] = pipelineMetadata['testCategory']
    msgTemplate['test']['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.${artifactType}"
    msgTemplate['test']['docs'] = pipelineMetadata['docs']
    if (scenario) {
        // msgTemplate['test']['scenario'] = scenario
        // OSCI-2612: we need to append the scenario to the test case name
        msgTemplate['test']['category'] += "/scenario=${scenario}"
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

    def msgTemplateString = libraryResource 'koji-build.test.running-template.json'
    msgTemplate = new groovy.json.JsonSlurperClassic().parseText(msgTemplateString)

    // contact section
    msgTemplate['contact']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['contact']['team'] = pipelineMetadata['maintainer']
    msgTemplate['contact']['docs'] = pipelineMetadata['docs']
    msgTemplate['contact']['irc'] = pipelineMetadata['contact']['irc']
    msgTemplate['contact']['email'] = pipelineMetadata['contact']['email']

    // run section
    msgTemplate['run']['url'] = "${env.BUILD_URL}"
    msgTemplate['run']['log'] = "${env.BUILD_URL}console"
    msgTemplate['run']['log_raw'] = "${env.BUILD_URL}consoleText"
    msgTemplate['run']['log_stream'] = "${env.BUILD_URL}console"

    // artifact section
    def koji = new Koji(env.KOJI_API_URL)
    def taskInfo = koji.getTaskInfo(taskId.toInteger())
    msgTemplate['artifact']['id'] = taskInfo.id
    msgTemplate['artifact']['issuer'] = taskInfo.ownerName
    msgTemplate['artifact']['component'] = taskInfo.name
    msgTemplate['artifact']['nvr'] = taskInfo.nvr
    msgTemplate['artifact']['scratch'] = taskInfo.scratch
    msgTemplate['artifact']['source'] = taskInfo.source.raw
    msgTemplate['artifact']['type'] = artifactType

    // test section
    msgTemplate['test']['type'] = testType ?: pipelineMetadata['testType']
    msgTemplate['test']['category'] = pipelineMetadata['testCategory']
    msgTemplate['test']['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.${artifactType}"
    msgTemplate['test']['docs'] = pipelineMetadata['docs']
    if (scenario) {
        // msgTemplate['test']['scenario'] = scenario
        // OSCI-2612: we need to append the scenario to the test case name
        msgTemplate['test']['category'] += "/scenario=${scenario}"
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

    def msgTemplateString = libraryResource 'koji-build.test.complete-template.json'
    msgTemplate = new groovy.json.JsonSlurperClassic().parseText(msgTemplateString)

    // contact section
    msgTemplate['contact']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['contact']['team'] = pipelineMetadata['maintainer']
    msgTemplate['contact']['docs'] = pipelineMetadata['docs']
    msgTemplate['contact']['irc'] = pipelineMetadata['contact']['irc']
    msgTemplate['contact']['email'] = pipelineMetadata['contact']['email']

    // artifact section
    def koji = new Koji(env.KOJI_API_URL)
    def taskInfo = koji.getTaskInfo(taskId.toInteger())
    msgTemplate['artifact']['id'] = taskInfo.id
    msgTemplate['artifact']['issuer'] = taskInfo.ownerName
    msgTemplate['artifact']['component'] = taskInfo.name
    msgTemplate['artifact']['nvr'] = taskInfo.nvr
    msgTemplate['artifact']['scratch'] = taskInfo.scratch
    msgTemplate['artifact']['baseline'] = taskInfo.nvr
    msgTemplate['artifact']['source'] = taskInfo.source.raw
    msgTemplate['artifact']['type'] = artifactType

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
    msgTemplate['test']['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.${artifactType}"
    msgTemplate['test']['result'] = result
    msgTemplate['test']['xunit'] = xunit
    msgTemplate['test']['docs'] = pipelineMetadata['docs']
    if (scenario) {
        // msgTemplate['test']['scenario'] = scenario
        // OSCI-2612: we need to append the scenario to the test case name
        msgTemplate['test']['category'] += "/scenario=${scenario}"
    }
    if (testProfile) {
        // this is a non-standard field
        msgTemplate['test']['profile'] = testProfile
    }
    if (note) {
        msgTemplate['test']['note'] = note
        // version <= 0.x
        msgTemplate['note'] = note
    }

    // run section
    msgTemplate['run']['url'] = "${env.BUILD_URL}"
    if (msgTemplate['test']['xunit']) {
        msgTemplate['run']['url'] = "${env.BUILD_URL}testReport/(root)/tests/"
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

    def msgTemplateString = libraryResource 'koji-build.test.error-template.json'
    msgTemplate = new groovy.json.JsonSlurperClassic().parseText(msgTemplateString)

    // contact section
    msgTemplate['contact']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['contact']['team'] = pipelineMetadata['maintainer']
    msgTemplate['contact']['docs'] = pipelineMetadata['docs']
    msgTemplate['contact']['irc'] = pipelineMetadata['contact']['irc']
    msgTemplate['contact']['email'] = pipelineMetadata['contact']['email']

    // artifact section
    def koji = new Koji(env.KOJI_API_URL)
    def taskInfo = koji.getTaskInfo(taskId.toInteger())
    msgTemplate['artifact']['id'] = taskInfo.id
    msgTemplate['artifact']['issuer'] = taskInfo.ownerName
    msgTemplate['artifact']['component'] = taskInfo.name
    msgTemplate['artifact']['nvr'] = taskInfo.nvr
    msgTemplate['artifact']['scratch'] = taskInfo.scratch
    msgTemplate['artifact']['source'] = taskInfo.source.raw
    msgTemplate['artifact']['type'] = artifactType

    // test section
    msgTemplate['test']['type'] = testType ?: pipelineMetadata['testType']
    msgTemplate['test']['category'] = pipelineMetadata['testCategory']
    msgTemplate['test']['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.${artifactType}"
    msgTemplate['test']['result'] = 'failed'
    msgTemplate['test']['docs'] = pipelineMetadata['docs']
    if (scenario) {
        // msgTemplate['test']['scenario'] = scenario
        // OSCI-2612: we need to append the scenario to the test case name
        msgTemplate['test']['category'] += "/scenario=${scenario}"
    }
    if (testProfile) {
        // this is a non-standard field
        msgTemplate['test']['profile'] = testProfile
    }

    // run section
    msgTemplate['run']['url'] = "${env.BUILD_URL}"

    msgTemplate['run']['log'] = "${env.BUILD_URL}console"
    msgTemplate['run']['log_raw'] = "${env.BUILD_URL}consoleText"
    msgTemplate['run']['log_stream'] = "${env.BUILD_URL}console"
    msgTemplate['run']['debug'] = "${env.BUILD_URL}console"
    msgTemplate['run']['rebuild'] = "${env.BUILD_URL}rebuild"

    // test section
    if (errorReason) {
        msgTemplate['error']['reason'] = errorReason
    } else {
        msgTemplate['error']['reason'] = env.ERROR_MESSAGE ? "${env.ERROR_MESSAGE}" : 'Infrastructure Failure'
    }

    // misc
    msgTemplate['generated_at'] = Utils.getTimestamp()

    return msgTemplate
}

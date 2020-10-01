package org.fedoraproject.jenkins.messages

import java.security.MessageDigest

import groovy.json.JsonOutput
import groovy.json.JsonSlurperClassic

import org.fedoraproject.jenkins.koji.Koji
import org.fedoraproject.jenkins.Utils


def buildMessageQueued(String artifactId, Map pipelineMetadata) {

    def msgTemplate

    def msgTemplateString = libraryResource 'fedora-update.test.queued-template.json'
    msgTemplate = new groovy.json.JsonSlurperClassic().parseText(msgTemplateString)

    // contact section
    msgTemplate['contact']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['contact']['team'] = pipelineMetadata['maintainer']
    msgTemplate['contact']['url'] = pipelineMetadata['docs']
    msgTemplate['contact']['docs'] = pipelineMetadata['docs']
    msgTemplate['contact']['irc'] = pipelineMetadata['contact']['irc']
    msgTemplate['contact']['email'] = pipelineMetadata['contact']['email']

    // run section
    msgTemplate['run']['id'] = "${env.BUILD_ID}".toInteger()
    msgTemplate['run']['log'] = "${env.BUILD_URL}console"
    msgTemplate['run']['url'] = "${env.BUILD_URL}"

    // artifact section
    def taskIds = Utils.getIdFromArtifactId(artifactId: artifactId, asArray: true)
    def targetArtifactId = Utils.getTargetArtifactId(artifactId)
    def koji = new Koji()
    def taskInfo
    def nvrs = []
    taskIds.each { taskId ->
        taskInfo = koji.getTaskInfo(taskId.toInteger())
        nvrs.add(taskInfo.nvr)
    }
    nvrs.sort()
    def digest = Utils.string2sha256(nvrs.join())

    msgTemplate['artifact']['release']['version'] = Utils.getReleaseIdFromBranch(env) - ~/[a-zA-Z]/
    msgTemplate['artifact']['release']['name'] = Utils.getReleaseIdFromBranch(env).toUpperCase()
    msgTemplate['artifact']['alias'] = "${targetArtifactId.split(':')[1]}"
    msgTemplate['artifact']['id'] = "sha256:${digest}"

    def builds = []
    nvrs.each { nvr ->
        builds.add([nvr: nvr])
    }
    msgTemplate['artifact']['builds'] = builds

    // pipeline section
    msgTemplate['pipeline']['id'] = Utils.generatePipelineId()
    msgTemplate['pipeline']['name'] = pipelineMetadata['pipelineName']

    // test section
    msgTemplate['test']['type'] = pipelineMetadata['testType']
    msgTemplate['test']['category'] = pipelineMetadata['testCategory']
    msgTemplate['test']['namespace'] = 'fedora-ci.fedora-update'

    // misc
    msgTemplate['generated_at'] = Utils.getTimestamp()

    return msgTemplate
}


def buildMessageRunning(String artifactId, Map pipelineMetadata) {

    def msgTemplate

    def msgTemplateString = libraryResource 'fedora-update.test.running-template.json'
    msgTemplate = new groovy.json.JsonSlurperClassic().parseText(msgTemplateString)

    // "running" messages are not defined for fedora-updates

    return msgTemplate
}


def buildMessageComplete(String artifactId, Map pipelineMetadata, String xunit) {

    def msgTemplate

    def msgTemplateString = libraryResource 'fedora-update.test.complete-template.json'
    msgTemplate = new groovy.json.JsonSlurperClassic().parseText(msgTemplateString)

    // contact section
    msgTemplate['contact']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['contact']['team'] = pipelineMetadata['maintainer']
    msgTemplate['contact']['url'] = pipelineMetadata['docs']
    msgTemplate['contact']['docs'] = pipelineMetadata['docs']
    msgTemplate['contact']['irc'] = pipelineMetadata['contact']['irc']
    msgTemplate['contact']['email'] = pipelineMetadata['contact']['email']

    // artifact section
    def taskIds = Utils.getIdFromArtifactId(artifactId: artifactId, asArray: true)
    def targetArtifactId = Utils.getTargetArtifactId(artifactId)
    def koji = new Koji()
    def taskInfo
    def nvrs = []
    taskIds.each { taskId ->
        taskInfo = koji.getTaskInfo(taskId.toInteger())
        nvrs.add(taskInfo.nvr)
    }
    nvrs.sort()
    def digest = Utils.string2sha256(nvrs.join())

    msgTemplate['artifact']['release']['version'] = Utils.getReleaseIdFromBranch(env) - ~/[a-zA-Z]/
    msgTemplate['artifact']['release']['name'] = Utils.getReleaseIdFromBranch(env).toUpperCase()
    msgTemplate['artifact']['alias'] = "${targetArtifactId.split(':')[1]}"
    msgTemplate['artifact']['id'] = "sha256:${digest}"

    def builds = []
    nvrs.each { nvr ->
        builds.add([nvr: nvr])
    }
    msgTemplate['artifact']['builds'] = builds

    // pipeline section
    msgTemplate['pipeline']['id'] = Utils.generatePipelineId()
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
    msgTemplate['test']['namespace'] = 'fedora-ci.fedora-update'
    msgTemplate['test']['result'] = result

    // run section
    if (xunit) {
        msgTemplate['run']['url'] = "${env.BUILD_URL}testReport/(root)/tests/"
    } else {
        msgTemplate['run']['url'] = "${env.BUILD_URL}"
    }
    msgTemplate['run']['id'] = "${env.BUILD_ID}".toInteger()
    msgTemplate['run']['log'] = "${env.BUILD_URL}console"
    msgTemplate['run']['url'] = "${env.BUILD_URL}"

    msgTemplate['system'] = []  // do we need this?

    // misc
    msgTemplate['generated_at'] = Utils.getTimestamp()

    return msgTemplate
}


def buildMessageError(String artifactId, Map pipelineMetadata, String xunit) {

    def msgTemplate

    def msgTemplateString = libraryResource 'fedora-update.test.error-template.json'
    msgTemplate = new groovy.json.JsonSlurperClassic().parseText(msgTemplateString)

    // contact section
    msgTemplate['contact']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['contact']['team'] = pipelineMetadata['maintainer']
    msgTemplate['contact']['url'] = pipelineMetadata['docs']
    msgTemplate['contact']['docs'] = pipelineMetadata['docs']
    msgTemplate['contact']['irc'] = pipelineMetadata['contact']['irc']
    msgTemplate['contact']['email'] = pipelineMetadata['contact']['email']

    // run section
    msgTemplate['run']['id'] = "${env.BUILD_ID}".toInteger()
    msgTemplate['run']['log'] = "${env.BUILD_URL}console"
    msgTemplate['run']['url'] = "${env.BUILD_URL}"

    // artifact section
    def taskIds = Utils.getIdFromArtifactId(artifactId: artifactId, asArray: true)
    def targetArtifactId = Utils.getTargetArtifactId(artifactId)
    def koji = new Koji()
    def taskInfo
    def nvrs = []
    taskIds.each { taskId ->
        taskInfo = koji.getTaskInfo(taskId.toInteger())
        nvrs.add(taskInfo.nvr)
    }
    nvrs.sort()
    def digest = Utils.string2sha256(nvrs.join())

    msgTemplate['artifact']['release']['version'] = Utils.getReleaseIdFromBranch(env) - ~/[a-zA-Z]/
    msgTemplate['artifact']['release']['name'] = Utils.getReleaseIdFromBranch(env).toUpperCase()
    msgTemplate['artifact']['alias'] = "${targetArtifactId.split(':')[1]}"
    msgTemplate['artifact']['id'] = "sha256:${digest}"

    def builds = []
    nvrs.each { nvr ->
        builds.add([nvr: nvr])
    }
    msgTemplate['artifact']['builds'] = builds

    // pipeline section
    msgTemplate['pipeline']['id'] = Utils.generatePipelineId()
    msgTemplate['pipeline']['name'] = pipelineMetadata['pipelineName']

    // test section
    msgTemplate['test']['type'] = pipelineMetadata['testType']
    msgTemplate['test']['category'] = pipelineMetadata['testCategory']
    msgTemplate['test']['namespace'] = 'fedora-ci.fedora-update'
    msgTemplate['test']['result'] = 'failed'

    // test section
    msgTemplate['error']['reason'] = 'Infrastructure Failure'

    // misc
    msgTemplate['generated_at'] = Utils.getTimestamp()

    return msgTemplate
}

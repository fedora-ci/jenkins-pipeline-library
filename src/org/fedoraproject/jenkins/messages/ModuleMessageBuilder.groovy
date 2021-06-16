package org.fedoraproject.jenkins.messages

import groovy.json.JsonOutput
import groovy.json.JsonSlurperClassic

import org.fedoraproject.jenkins.mbs.Mbs
import org.fedoraproject.jenkins.Utils


def buildMessageQueued(String artifactType, String taskId, Map pipelineMetadata) {

    def msgTemplate

    def msgTemplateString = libraryResource 'redhat-module.test.queued-template.json'
    msgTemplate = new groovy.json.JsonSlurperClassic().parseText(msgTemplateString)

    // CI/contact section
    msgTemplate['ci']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['ci']['team'] = pipelineMetadata['maintainer']
    msgTemplate['ci']['url'] = pipelineMetadata['docs']
    msgTemplate['ci']['docs'] = pipelineMetadata['docs']
    msgTemplate['ci']['irc'] = pipelineMetadata['contact']['irc']
    msgTemplate['ci']['email'] = pipelineMetadata['contact']['email']

    // artifact section
    def mbs = new Mbs(env.FEDORA_CI_MBS_URL)
    def moduleInfo = mbs.getModuleBuildInfo(taskId)

    msgTemplate['artifact']['type'] = 'redhat-module'
    msgTemplate['artifact']['context'] = moduleInfo.get('context')
    msgTemplate['artifact']['id'] = moduleInfo.get('id')
    msgTemplate['artifact']['issuer'] = moduleInfo.get('owner')
    msgTemplate['artifact']['name'] = moduleInfo.get('name')
    msgTemplate['artifact']['nsvc'] = mbs.getModuleName(moduleInfo)
    msgTemplate['artifact']['stream'] = moduleInfo.get('stream')
    msgTemplate['artifact']['version'] = moduleInfo.get('version')

    // test section
    msgTemplate['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.${artifactType}"
    msgTemplate['category'] = pipelineMetadata['testCategory']
    msgTemplate['type'] = pipelineMetadata['testType']
    msgTemplate['docs'] = pipelineMetadata['docs']

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


def buildMessageRunning(String artifactType, String taskId, Map pipelineMetadata) {

    def msgTemplate

    def msgTemplateString = libraryResource 'redhat-module.test.running-template.json'
    msgTemplate = new groovy.json.JsonSlurperClassic().parseText(msgTemplateString)

    // CI/contact section
    msgTemplate['ci']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['ci']['team'] = pipelineMetadata['maintainer']
    msgTemplate['ci']['url'] = pipelineMetadata['docs']
    msgTemplate['ci']['docs'] = pipelineMetadata['docs']
    msgTemplate['ci']['irc'] = pipelineMetadata['contact']['irc']
    msgTemplate['ci']['email'] = pipelineMetadata['contact']['email']

    // artifact section
    def mbs = new Mbs(env.FEDORA_CI_MBS_URL)
    def moduleInfo = mbs.getModuleBuildInfo(taskId)

    msgTemplate['artifact']['type'] = 'redhat-module'
    msgTemplate['artifact']['context'] = moduleInfo.get('context')
    msgTemplate['artifact']['id'] = moduleInfo.get('id')
    msgTemplate['artifact']['issuer'] = moduleInfo.get('owner')
    msgTemplate['artifact']['name'] = moduleInfo.get('name')
    msgTemplate['artifact']['nsvc'] = mbs.getModuleName(moduleInfo)
    msgTemplate['artifact']['stream'] = moduleInfo.get('stream')
    msgTemplate['artifact']['version'] = moduleInfo.get('version')

    // test section
    msgTemplate['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.${artifactType}"
    msgTemplate['category'] = pipelineMetadata['testCategory']
    msgTemplate['type'] = pipelineMetadata['testType']
    msgTemplate['docs'] = pipelineMetadata['docs']

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


def buildMessageComplete(String artifactType, String taskId, Map pipelineMetadata, String xunit) {

    def msgTemplate

    def msgTemplateString = libraryResource 'redhat-module.test.complete-template.json'
    msgTemplate = new groovy.json.JsonSlurperClassic().parseText(msgTemplateString)

    // CI/contact section
    msgTemplate['ci']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['ci']['team'] = pipelineMetadata['maintainer']
    msgTemplate['ci']['url'] = pipelineMetadata['docs']
    msgTemplate['ci']['docs'] = pipelineMetadata['docs']
    msgTemplate['ci']['irc'] = pipelineMetadata['contact']['irc']
    msgTemplate['ci']['email'] = pipelineMetadata['contact']['email']

    // artifact section
    def mbs = new Mbs(env.FEDORA_CI_MBS_URL)
    def moduleInfo = mbs.getModuleBuildInfo(taskId)

    msgTemplate['artifact']['type'] = 'redhat-module'
    msgTemplate['artifact']['context'] = moduleInfo.get('context')
    msgTemplate['artifact']['id'] = moduleInfo.get('id')
    msgTemplate['artifact']['issuer'] = moduleInfo.get('owner')
    msgTemplate['artifact']['name'] = moduleInfo.get('name')
    msgTemplate['artifact']['nsvc'] = mbs.getModuleName(moduleInfo)
    msgTemplate['artifact']['stream'] = moduleInfo.get('stream')
    msgTemplate['artifact']['version'] = moduleInfo.get('version')

    // test section
    def result = 'needs_inspection'
    if (currentBuild.result == 'SUCCESS') {
        result = 'passed'
    } else if (currentBuild.result == 'UNSTABLE') {
        result = 'needs_inspection'
    }

    msgTemplate['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.${artifactType}"
    msgTemplate['category'] = pipelineMetadata['testCategory']
    msgTemplate['type'] = pipelineMetadata['testType']
    msgTemplate['status'] = result
    msgTemplate['xunit'] = xunit
    msgTemplate['docs'] = pipelineMetadata['docs']

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


def buildMessageError(String artifactType, String taskId, Map pipelineMetadata, String xunit) {

    def msgTemplate

    def msgTemplateString = libraryResource 'redhat-module.test.error-template.json'
    msgTemplate = new groovy.json.JsonSlurperClassic().parseText(msgTemplateString)

    // CI/contact section
    msgTemplate['ci']['name'] = pipelineMetadata['pipelineName']
    msgTemplate['ci']['team'] = pipelineMetadata['maintainer']
    msgTemplate['ci']['url'] = pipelineMetadata['docs']
    msgTemplate['ci']['docs'] = pipelineMetadata['docs']
    msgTemplate['ci']['irc'] = pipelineMetadata['contact']['irc']
    msgTemplate['ci']['email'] = pipelineMetadata['contact']['email']

    // artifact section
    def mbs = new Mbs(env.FEDORA_CI_MBS_URL)
    def moduleInfo = mbs.getModuleBuildInfo(taskId)

    msgTemplate['artifact']['type'] = 'redhat-module'
    msgTemplate['artifact']['context'] = moduleInfo.get('context')
    msgTemplate['artifact']['id'] = moduleInfo.get('id')
    msgTemplate['artifact']['issuer'] = moduleInfo.get('owner')
    msgTemplate['artifact']['name'] = moduleInfo.get('name')
    msgTemplate['artifact']['nsvc'] = mbs.getModuleName(moduleInfo)
    msgTemplate['artifact']['stream'] = moduleInfo.get('stream')
    msgTemplate['artifact']['version'] = moduleInfo.get('version')

    // test section
    msgTemplate['namespace'] = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.${artifactType}"
    msgTemplate['category'] = pipelineMetadata['testCategory']
    msgTemplate['type'] = pipelineMetadata['testType']
    msgTemplate['status'] = "failed"
    msgTemplate['xunit'] = xunit

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

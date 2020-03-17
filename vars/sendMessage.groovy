#!/usr/bin/groovy

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import org.fedoraproject.jenkins.koji.Koji
import org.fedoraproject.jenkins.Utils


def call(Map params = [:]) {

    def msgVersion = '0.2.1'

    def messageType = params.get('type')
    def artifactId = params.get('artifactId')
    def pipelineMetadata = params.get('pipelineMetadata')
    def dryRun = params.get('dryRun')

    // TODO: we will need to distinguish between multiple artifact types
    // maybe we should consider having steps like "sendNotificationTestRunning()" (?)
    def taskId = artifactId.split(':')[1]

    def msgTemplate
    if (messageType == 'running') {
        def msgTemplateString = libraryResource 'koji-build.test.running-template.json'
        msgTemplate = new groovy.json.JsonSlurper().parseText(msgTemplateString)

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

        // pipeline section
        msgTemplate['pipeline']['id'] = Utils.generatePipelineId()
        msgTemplate['pipeline']['id'] = pipelineMetadata['pipelineName']

        // test section
        msgTemplate['test']['type'] = pipelineMetadata['testType']
        msgTemplate['test']['category'] = pipelineMetadata['testCategory']
        msgTemplate['test']['namespace'] = 'fedora-ci.koji-build'

        // misc
        msgTemplate['generated_at'] = Utils.getTimestamp()
        msgTemplate['version'] = msgVersion
    } // TODO: else exception...

    def msgTopic = 'org.centos.prod.ci.koji-build.test.running'

    def msgProps = ''
    // def msgContent = JsonOutput.toJson(msgTemplate)

    // TODO: serialization exception - fix
    if (false) {
        retry(10) {
            try {
                // 1 minute should be more than enough time to send the message
                timeout(1) {
                    // Send message and return SendResult
                    sendResult = sendCIMessage messageContent: msgContent,
                        messageProperties: msgProps,
                        messageType: "Custom",
                        overrides: [topic: msgTopic],
                        failOnError: true,
                        providerName: env.MSG_PROVIDER
                    return sendResult
                }
            } catch(e) {
                echo "FAIL: Could not send message to ${env.MSG_PROVIDER} on topic ${msgTopic}"
                echo e.getMessage()
                sleep 30
                error e.getMessage()
            }
        }
        String id = sendResult.getMessageId()
        String msg = sendResult.getMessageContent()

        print("INFO: Sent message ${id}: ${msg}")
        return
    } else {
        // dry run, just print the message
        print("INFO: Skipping sending following message as this is a dry: ${msgTemplate.toString()}")
    }
}

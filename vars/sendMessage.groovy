#!/usr/bin/groovy

import groovy.json.JsonOutput
import groovy.json.JsonSlurperClassic

import org.fedoraproject.jenkins.koji.Koji
import org.fedoraproject.jenkins.Utils
import org.fedoraproject.jenkins.messages.MessageBuilder


def call(Map params = [:]) {

    def messageType = params.get('type')
    def artifactId = params.get('artifactId')
    def pipelineMetadata = params.get('pipelineMetadata')
    def dryRun = params.get('dryRun')
    def topic = params.get('topic')
    def messageProvider = params.get('messageProvider') ?: env.FEDORA_CI_MESSAGE_PROVIDER
    def xunit = params.get('xunit') ?: ''
    def runUrl = params.get('runUrl') ?: ''
    def isSkipped = params.get('isSkipped').asBoolean() ?: false
    def note = params.get('note') ?: ''
    def scenario = params.get('testScenario') ?: ''

    def targetArtifactId = artifactId
    if (Utils.isCompositeArtifact(artifactId)) {
        targetArtifactId = Utils.getTargetArtifactId(artifactId)
    }

    if (!targetArtifactId.contains(':')) {
        error("Invalid artifact Id: ${targetArtifactId} — the correct syntax is 'artifactType:id'")
    }

    def artifactType = targetArtifactId.split(':')[0]
    def taskId = targetArtifactId.split(':')[1]

    def msg

    if (!messageType in ['queued', 'running', 'complete', 'error']) {
        error("Unknown message type: ${messageType}")
    }

    if (!topic) {
        def topics = libraryResource 'mappings/topics.json'
        topics = new groovy.json.JsonSlurperClassic().parseText(topics)

        if (!artifactType in topics) {
            error("Unable to determine the topic for the ${artifactType} artifact type. The mapping is missing.")
        }
        topic = topics[artifactType]['test'][messageType]
    }

    if (messageType == 'queued') {
        msg = new MessageBuilder().buildMessageQueued(artifactId, artifactType, taskId, pipelineMetadata, runUrl, scenario)
    }

    if (messageType == 'running') {
        msg = new MessageBuilder().buildMessageRunning(artifactId, artifactType, taskId, pipelineMetadata, runUrl, scenario)
    }

    if (messageType == 'complete') {
        msg = new MessageBuilder().buildMessageComplete(artifactId, artifactType, taskId, pipelineMetadata, xunit, runUrl, isSkipped, note, scenario)
    }

    if (messageType == 'error') {
        msg = new MessageBuilder().buildMessageError(artifactId, artifactType, taskId, pipelineMetadata, xunit, runUrl, scenario)
    }

    def msgProps = ''
    def msgContent = JsonOutput.toJson(msg)

    if (!msg) {
        print("INFO: '${messageType}' message is not defined for the '${artifactType}' artifact type.")
        return
    }

    if (dryRun) {
        // dry run, just print the message
        print("INFO: This is a dry run — skipping following \"${messageType}\" message: ${Utils.mapToJsonString(msg, false)}\ntopic: ${topic}")
        return
    }

    if (!messageProvider) {
        print("FAIL: Missing configuration for the message provider - unable to send following message: ${msg.toString()}")
        return
    }

    retry(10) {
        try {
            // 1 minute should be more than enough time to send the message
            timeout(1) {
                // Send message and return SendResult
                sendResult = sendCIMessage(
                    messageContent: msgContent,
                    messageProperties: msgProps,
                    messageType: "Custom",
                    overrides: [
                        topic: topic
                    ],
                    failOnError: true,
                    providerName: messageProvider
                )
            }
        } catch(e) {
            echo "FAIL: Could not send message to ${messageProvider} on topic ${topic}"
            echo "${e}"
            sleep 30
            error e.getMessage()
        }
    }
    String resultMsgId = sendResult.getMessageId()
    String resultMsgContent = sendResult.getMessageContent()

    print("INFO: Message sent; id = ${resultMsgId}")
    return sendResult
}

#!/usr/bin/groovy

import groovy.json.JsonOutput

import org.fedoraproject.jenkins.koji.Koji
import org.fedoraproject.jenkins.Utils
import org.fedoraproject.jenkins.messages.MessageBuilder


def call(Map params = [:]) {

    def messageType = params.get('type')
    def artifactId = params.get('artifactId')
    def pipelineMetadata = params.get('pipelineMetadata')
    def dryRun = params.get('dryRun')

    def artifactType = artifactId.split(':')[0]
    def taskId = artifactId.split(':')[1]

    def msgTopic
    def msg

    if (messageType == 'queued') {
        msgTopic = 'org.centos.prod.ci.koji-build.test.queued'
        msg = new MessageBuilder().buildMessageQueued(artifactType, taskId, pipelineMetadata)
    }

    if (messageType == 'running') {
        msgTopic = 'org.centos.prod.ci.koji-build.test.running'
        msg = new MessageBuilder().buildMessageRunning(artifactType, taskId, pipelineMetadata)
    }

    if (messageType == 'complete') {
        msgTopic = 'org.centos.prod.ci.koji-build.test.complete'
        msg = new MessageBuilder().buildMessageComplete(artifactType, taskId, pipelineMetadata)
    }

    if (messageType == 'error') {
        msgTopic = 'org.centos.prod.ci.koji-build.test.error'
        msg = new MessageBuilder().buildMessageError(artifactType, taskId, pipelineMetadata)
    }

    def msgProps = ''
    def msgContent = JsonOutput.toJson(msg)

    if (!dryRun) {
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
                            topic: msgTopic
                        ],
                        failOnError: true,
                        providerName: env.MSG_PROVIDER
                    )
                }
            } catch(e) {
                echo "FAIL: Could not send message to ${env.MSG_PROVIDER} on topic ${msgTopic}"
                echo "${e}"
                sleep 30
                error e.getMessage()
            }
        }
        String resultMsgId = sendResult.getMessageId()
        String resultMsgContent = sendResult.getMessageContent()

        print("INFO: Sent message ${msgId}: ${msgContent}")
        return sendResult
    } else {
        // dry run, just print the message
        print("INFO: Skipping sending following message as this is a dry: ${msg.toString()}")
    }
}

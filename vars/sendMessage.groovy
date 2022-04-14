#!/usr/bin/groovy

import groovy.json.JsonOutput
import groovy.json.JsonSlurperClassic

import org.fedoraproject.jenkins.koji.Koji
import org.fedoraproject.jenkins.Utils
import org.fedoraproject.jenkins.messages.MessageBuilder


def call(Map params = [:]) {

    def messageType = params.get('type')
    def mainArtifactId = params.get('artifactId')
    def additionalArtifactIds = params.get('additionalArtifactIds') ?: ''
    def pipelineMetadata = params.get('pipelineMetadata')
    def dryRun = params.get('dryRun')
    def topic = params.get('topic')
    def messageProvider = params.get('messageProvider') ?: env.FEDORA_CI_MESSAGE_PROVIDER
    def xunit = params.get('xunit') ?: ''
    def runUrl = params.get('runUrl') ?: ''
    def runLog = params.get('runLog') ?: ''
    def isSkipped = params.get('isSkipped', false)?.toBoolean()
    def isInfo = params.get('isInfo', false)?.toBoolean()
    def note = params.get('note') ?: ''
    def scenario = params.get('testScenario') ?: ''
    def errorReason = params.get('errorReason') ?: ''
    def testType = params.get('testType') ?: ''
    def testResult = params.get('testResult') ?: ''
    def testProfile = params.get('testProfile') ?: ''

    // isInfo is an alias for isSkipped
    isSkipped = isSkipped || isInfo

    def sentResults = []
    additionalArtifactIds = additionalArtifactIds.split(',') ?: []
    def artifactIds = [mainArtifactId]
    if (additionalArtifactIds) {
        artifactIds = additionalArtifactIds + mainArtifactId
    }
    artifactIds.each { artifactId ->
        if (!artifactId) {
            return
        }
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
            // If we called this step in this pipeline run before,
            // we should find cached topics mapping in the environment.
            if (!env._PIPELINE_LIBRARY_TOPICS_MAPPING) {
                // If the mapping is not in the environment yet,
                // we fetch it from the configured URL and cache it.
                def response
                def sleepTime = 5  // seconds
                retry (10) {
                    try {
                        response = httpRequest(
                            url: env.TOPICS_MAPPING_CONFIG_URL,
                            quiet: true,
                            consoleLogResponseBody: false
                        )
                    } catch(e) {
                        echo "Error: Failed to fetch topics mapping from ${env.TOPICS_MAPPING_CONFIG_URL}"
                        sleep(time: sleepTime, unit:"SECONDS")
                        sleepTime *= 2  // double the sleep time
                        throw e
                    }
                }
                env._PIPELINE_LIBRARY_TOPICS_MAPPING = response.content
            }
            topics = new groovy.json.JsonSlurperClassic().parseText(env._PIPELINE_LIBRARY_TOPICS_MAPPING)

            if (!artifactType in topics) {
                error("Unable to determine the topic for the ${artifactType} artifact type. The mapping is missing.")
            }
            topic = topics[artifactType]['test'][messageType]
        }

        if (messageType == 'queued') {
            msg = new MessageBuilder().buildMessageQueued(
                artifactId,
                artifactType,
                taskId,
                pipelineMetadata,
                runUrl,
                runLog,
                scenario,
                testType,
                testProfile
            )
        }

        if (messageType == 'running') {
            msg = new MessageBuilder().buildMessageRunning(
                artifactId,
                artifactType,
                taskId,
                pipelineMetadata,
                runUrl,
                runLog,
                scenario,
                testType,
                testProfile
            )
        }

        if (messageType == 'complete') {
            msg = new MessageBuilder().buildMessageComplete(
                artifactId,
                artifactType,
                taskId,
                pipelineMetadata,
                xunit,
                runUrl,
                runLog,
                isSkipped,
                note,
                scenario,
                testType,
                testProfile,
                testResult
            )
        }

        if (messageType == 'error') {
            msg = new MessageBuilder().buildMessageError(
                artifactId,
                artifactType,
                taskId,
                pipelineMetadata,
                xunit,
                runUrl,
                runLog,
                scenario,
                errorReason,
                testType,
                testProfile
            )
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

        def sentResult
        retry(10) {
            try {
                // 1 minute should be more than enough time to send the message
                timeout(1) {
                    // Send message
                    sentResult = sendCIMessage(
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
        String resultMsgId = sentResult.getMessageId()
        String resultMsgContent = sentResult.getMessageContent()

        print("INFO: Message sent; id = ${resultMsgId}")
        sentResults += sentResult

        if (artifactIds.length > 1) {
            // there is more than one artifact id, so let's wait a bit before sending
            // the next message
            sleep(time: 1, unit: 'SECONDS')
        }
    }
    return sentResults
}

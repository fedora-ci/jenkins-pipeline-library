package org.fedoraproject.jenkins.messages

import groovy.json.JsonOutput
import groovy.json.JsonSlurperClassic

import org.fedoraproject.jenkins.koji.Koji
import org.fedoraproject.jenkins.Utils
import org.fedoraproject.jenkins.messages.RpmBuildMessageBuilder


def getMessageVersion() {
    return '0.2.1'
}

def buildMessageQueued(String artifactType, String taskId, Map pipelineMetadata) {

    def msg

    if (artifactType == 'koji-build') {
        msg = new RpmBuildMessageBuilder().buildMessageQueued(artifactType, taskId, pipelineMetadata)
    } else {
        throw new Exception("Unknown artifact type: ${artifactType}")
    }

    msg['version'] = getMessageVersion()
    return msg
}


def buildMessageRunning(String artifactType, String taskId, Map pipelineMetadata) {

    def msg

    if (artifactType == 'koji-build') {
        msg = new RpmBuildMessageBuilder().buildMessageRunning(artifactType, taskId, pipelineMetadata)
    } else {
        throw new Exception("Unknown artifact type: ${artifactType}")
    }

    msg['version'] = getMessageVersion()
    return msg
}


def buildMessageComplete(String artifactType, String taskId, Map pipelineMetadata, String xunit) {

    def msg

    if (artifactType == 'koji-build') {
        msg = new RpmBuildMessageBuilder().buildMessageComplete(artifactType, taskId, pipelineMetadata, xunit)
    } else {
        throw new Exception("Unknown artifact type: ${artifactType}")
    }

    msg['version'] = getMessageVersion()
    return msg
}


def buildMessageError(String artifactType, String taskId, Map pipelineMetadata, String xunit) {

    def msg

    if (artifactType == 'koji-build') {
        msg = new RpmBuildMessageBuilder().buildMessageError(artifactType, taskId, pipelineMetadata, xunit)
    } else {
        throw new Exception("Unknown artifact type: ${artifactType}")
    }

    msg['version'] = getMessageVersion()
    return msg
}

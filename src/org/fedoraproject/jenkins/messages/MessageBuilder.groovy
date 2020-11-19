package org.fedoraproject.jenkins.messages

import groovy.json.JsonOutput
import groovy.json.JsonSlurperClassic

import org.fedoraproject.jenkins.koji.Koji
import org.fedoraproject.jenkins.Utils
import org.fedoraproject.jenkins.messages.RpmBuildMessageBuilder
import org.fedoraproject.jenkins.messages.PullRequestMessageBuilder
import org.fedoraproject.jenkins.messages.FedoraUpdateMessageBuilder


def getMessageVersion() {
    return '0.2.1'
}

def buildMessageQueued(String artifactId, String artifactType, String taskId, Map pipelineMetadata) {

    def msg

    if (artifactType in ['koji-build', 'brew-build']) {
        msg = new RpmBuildMessageBuilder().buildMessageQueued(artifactType, taskId, pipelineMetadata)
    } else if (artifactType == 'fedora-dist-git') {
        msg = new PullRequestMessageBuilder().buildMessageQueued(artifactType, taskId, pipelineMetadata)
    } else if (artifactType == 'dist-git-pr') {
        msg = new RHPullRequestMessageBuilder().buildMessageQueued(artifactType, taskId, pipelineMetadata)
    } else if (artifactType == 'fedora-update') {
        msg = new FedoraUpdateMessageBuilder().buildMessageQueued(artifactId, pipelineMetadata)
    } else {
        throw new Exception("Unknown artifact type: ${artifactType}")
    }

    if (msg && !msg.get('version')) {
        msg['version'] = getMessageVersion()
    }
    return msg
}


def buildMessageRunning(String artifactId, String artifactType, String taskId, Map pipelineMetadata) {

    def msg

    if (artifactType in ['koji-build', 'brew-build']) {
        msg = new RpmBuildMessageBuilder().buildMessageRunning(artifactType, taskId, pipelineMetadata)
    } else if (artifactType == 'fedora-dist-git') {
        msg = new PullRequestMessageBuilder().buildMessageRunning(artifactType, taskId, pipelineMetadata)
    } else if (artifactType == 'dist-git-pr') {
        msg = new RHPullRequestMessageBuilder().buildMessageRunning(artifactType, taskId, pipelineMetadata)
    } else if (artifactType == 'fedora-update') {
        msg = new FedoraUpdateMessageBuilder().buildMessageRunning(artifactId, pipelineMetadata)
    } else {
        throw new Exception("Unknown artifact type: ${artifactType}")
    }

    if (msg && !msg.get('version')) {
        msg['version'] = getMessageVersion()
    }
    return msg
}


def buildMessageComplete(String artifactId, String artifactType, String taskId, Map pipelineMetadata, String xunit) {

    def msg

    if (artifactType in ['koji-build', 'brew-build']) {
        msg = new RpmBuildMessageBuilder().buildMessageComplete(artifactType, taskId, pipelineMetadata, xunit)
    } else if (artifactType == 'fedora-dist-git') {
        msg = new PullRequestMessageBuilder().buildMessageComplete(artifactType, taskId, pipelineMetadata, xunit)
    } else if (artifactType == 'dist-git-pr') {
        msg = new RHPullRequestMessageBuilder().buildMessageComplete(artifactType, taskId, pipelineMetadata, xunit)
    } else if (artifactType == 'fedora-update') {
        msg = new FedoraUpdateMessageBuilder().buildMessageComplete(artifactId, pipelineMetadata, xunit)
    } else {
        throw new Exception("Unknown artifact type: ${artifactType}")
    }

    if (msg && !msg.get('version')) {
        msg['version'] = getMessageVersion()
    }
    return msg
}


def buildMessageError(String artifactId, String artifactType, String taskId, Map pipelineMetadata, String xunit) {

    def msg

    if (artifactType in ['koji-build', 'brew-build']) {
        msg = new RpmBuildMessageBuilder().buildMessageError(artifactType, taskId, pipelineMetadata, xunit)
    } else if (artifactType == 'fedora-dist-git') {
        msg = new PullRequestMessageBuilder().buildMessageError(artifactType, taskId, pipelineMetadata, xunit)
    } else if (artifactType == 'dist-git-pr') {
        msg = new RHPullRequestMessageBuilder().buildMessageError(artifactType, taskId, pipelineMetadata, xunit)
    } else if (artifactType == 'fedora-update') {
        msg = new FedoraUpdateMessageBuilder().buildMessageError(artifactId, pipelineMetadata, xunit)
    } else {
        throw new Exception("Unknown artifact type: ${artifactType}")
    }

    if (msg && !msg.get('version')) {
        msg['version'] = getMessageVersion()
    }
    return msg
}

package org.fedoraproject.jenkins.messages

import groovy.json.JsonOutput
import groovy.json.JsonSlurperClassic

import org.fedoraproject.jenkins.koji.Koji
import org.fedoraproject.jenkins.Utils
import org.fedoraproject.jenkins.messages.RpmBuildMessageBuilder
import org.fedoraproject.jenkins.messages.PullRequestMessageBuilder
import org.fedoraproject.jenkins.messages.FedoraUpdateMessageBuilder


def getMessageVersion() {
    return '1.1.6'
}

def getPipelineSection(artifactType, taskId, pipelineMetadata) {
    // construct the pipeline section
    def namespace = "${pipelineMetadata['maintainer'].toLowerCase().replace(' ', '-')}.${artifactType}"
    def pipeline = [
        'id': Utils.generatePipelineIdFromArtifactIdAndTestcase(
            "${artifactType}:${taskId}",
            "${namespace}.${pipelineMetadata['testType']}.${pipelineMetadata['testCategory']}"
        ),
        'name': pipelineMetadata['pipelineName'],
        'build': "${env.BUILD_NUMBER}"
    ]
    return pipeline
}


def buildMessageQueued(
    String artifactId,
    String artifactType,
    String taskId,
    Map pipelineMetadata,
    String runUrl,
    String scenario
) {
    def msg

    if (artifactType in ['koji-build', 'brew-build']) {
        msg = new RpmBuildMessageBuilder().buildMessageQueued(
            artifactType, taskId, pipelineMetadata, scenario
        )
    } else if (artifactType == 'fedora-dist-git') {
        msg = new PullRequestMessageBuilder().buildMessageQueued(
            artifactType, taskId, pipelineMetadata
        )
    } else if (artifactType == 'dist-git-pr') {
        msg = new RHPullRequestMessageBuilder().buildMessageQueued(
            artifactType, taskId, pipelineMetadata
        )
    } else if (artifactType == 'redhat-module') {
        msg = new ModuleMessageBuilder().buildMessageQueued(
            artifactType, taskId, pipelineMetadata
        )
    } else if (artifactType == 'fedora-update') {
        msg = new FedoraUpdateMessageBuilder().buildMessageQueued(
            artifactId, pipelineMetadata
        )
    } else {
        throw new Exception("Unknown artifact type: ${artifactType}")
    }

    if (msg) {
        if (!msg.get('version')) {
            msg['version'] = getMessageVersion()
        }
        if (!msg.get('pipeline')?.get('id') && !msg.get('thread_id')) {
            msg['pipeline'] = getPipelineSection(artifactType, taskId, pipelineMetadata)
        }
        if (runUrl && msg.get('run', {})?.get('url')) {
            msg['run']['url'] = runUrl
        }
    }
    return msg
}


def buildMessageRunning(
    String artifactId,
    String artifactType,
    String taskId,
    Map pipelineMetadata,
    String runUrl,
    String scenario
) {
    def msg

    if (artifactType in ['koji-build', 'brew-build']) {
        msg = new RpmBuildMessageBuilder().buildMessageRunning(
            artifactType, taskId, pipelineMetadata, scenario
        )
    } else if (artifactType == 'fedora-dist-git') {
        msg = new PullRequestMessageBuilder().buildMessageRunning(
            artifactType, taskId, pipelineMetadata
        )
    } else if (artifactType == 'dist-git-pr') {
        msg = new RHPullRequestMessageBuilder().buildMessageRunning(
            artifactType, taskId, pipelineMetadata
        )
    } else if (artifactType == 'redhat-module') {
        msg = new ModuleMessageBuilder().buildMessageRunning(
            artifactType, taskId, pipelineMetadata
        )
    } else if (artifactType == 'fedora-update') {
        msg = new FedoraUpdateMessageBuilder().buildMessageRunning(
            artifactId, pipelineMetadata
        )
    } else {
        throw new Exception("Unknown artifact type: ${artifactType}")
    }

    if (msg) {
        if (!msg.get('version')) {
            msg['version'] = getMessageVersion()
        }
        if (!msg.get('pipeline')?.get('id') && !msg.get('thread_id')) {
            msg['pipeline'] = getPipelineSection(artifactType, taskId, pipelineMetadata)
        }
        if (runUrl && msg.get('run', {})?.get('url')) {
            msg['run']['url'] = runUrl
        }
    }
    return msg
}


def buildMessageComplete(
    String artifactId,
    String artifactType,
    String taskId,
    Map pipelineMetadata,
    String xunit,
    String runUrl,
    Boolean isSkipped,
    String note,
    String scenario
) {
    def msg

    if (artifactType in ['koji-build', 'brew-build']) {
        msg = new RpmBuildMessageBuilder().buildMessageComplete(
            artifactType, taskId, pipelineMetadata, xunit, isSkipped, note, scenario
        )
    } else if (artifactType == 'fedora-dist-git') {
        msg = new PullRequestMessageBuilder().buildMessageComplete(
            artifactType, taskId, pipelineMetadata, xunit
        )
    } else if (artifactType == 'dist-git-pr') {
        msg = new RHPullRequestMessageBuilder().buildMessageComplete(
            artifactType, taskId, pipelineMetadata, xunit
        )
    } else if (artifactType == 'redhat-module') {
        msg = new ModuleMessageBuilder().buildMessageComplete(
            artifactType, taskId, pipelineMetadata, xunit
        )
    } else if (artifactType == 'fedora-update') {
        msg = new FedoraUpdateMessageBuilder().buildMessageComplete(
            artifactId, pipelineMetadata, xunit, isSkipped
        )
    } else {
        throw new Exception("Unknown artifact type: ${artifactType}")
    }

    if (msg) {
        if (!msg.get('version')) {
            msg['version'] = getMessageVersion()
        }
        if (!msg.get('pipeline')?.get('id') && !msg.get('thread_id')) {
            msg['pipeline'] = getPipelineSection(artifactType, taskId, pipelineMetadata)
        }
        if (runUrl && msg.get('run', {})?.get('url')) {
            msg['run']['url'] = runUrl
        }
    }
    return msg
}


def buildMessageError(
    String artifactId,
    String artifactType,
    String taskId,
    Map pipelineMetadata,
    String xunit,
    String runUrl,
    String scenario,
    String errorReason
) {
    def msg

    if (artifactType in ['koji-build', 'brew-build']) {
        msg = new RpmBuildMessageBuilder().buildMessageError(
            artifactType, taskId, pipelineMetadata, xunit, scenario, errorReason
        )
    } else if (artifactType == 'fedora-dist-git') {
        msg = new PullRequestMessageBuilder().buildMessageError(
            artifactType, taskId, pipelineMetadata, xunit, errorReason
        )
    } else if (artifactType == 'dist-git-pr') {
        msg = new RHPullRequestMessageBuilder().buildMessageError(
            artifactType, taskId, pipelineMetadata, xunit
        )
    } else if (artifactType == 'redhat-module') {
        msg = new ModuleMessageBuilder().buildMessageError(
            artifactType, taskId, pipelineMetadata, xunit
        )
    } else if (artifactType == 'fedora-update') {
        msg = new FedoraUpdateMessageBuilder().buildMessageError(
            artifactId, pipelineMetadata, xunit
        )
    } else {
        throw new Exception("Unknown artifact type: ${artifactType}")
    }

    if (msg) {
        if (!msg.get('version')) {
            msg['version'] = getMessageVersion()
        }
        if (!msg.get('pipeline')?.get('id') && !msg.get('thread_id')) {
            msg['pipeline'] = getPipelineSection(artifactType, taskId, pipelineMetadata)
        }
        if (runUrl && msg.get('run', {})?.get('url')) {
            msg['run']['url'] = runUrl
        }
    }
    return msg
}

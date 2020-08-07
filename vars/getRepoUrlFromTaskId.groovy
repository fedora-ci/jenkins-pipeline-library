#!/usr/bin/groovy

import org.fedoraproject.jenkins.koji.Koji


/**
 * getRepoUrlFromTaskId() step.
 */
def call(taskId) {
    def koji = new Koji()
    def buildInfo = koji.getTaskInfo(taskId.toInteger())
    def url = buildInfo.source.url

    def prefix = ~/^git+/
    def suffix = ~/.git$/

    url -= prefix
    url -= suffix

    return url
}

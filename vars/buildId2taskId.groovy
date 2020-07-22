#!/usr/bin/groovy

import org.fedoraproject.jenkins.koji.Koji


/**
 * buildId2taskId() step.
 */
def call(buildId) {
    def koji = new Koji()
    def buildInfo = koji.getBuildInfo(buildId.toInteger())
    return buildInfo.taskId
}

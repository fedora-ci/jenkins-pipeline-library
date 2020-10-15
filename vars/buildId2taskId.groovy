#!/usr/bin/groovy

import org.fedoraproject.jenkins.koji.Koji


/**
 * buildId2taskId() step.
 */
def call(buildId) {
    def koji = new Koji(env.KOJI_API_URL)
    def buildInfo = koji.getBuildInfo(buildId.toInteger())
    return buildInfo.taskId
}

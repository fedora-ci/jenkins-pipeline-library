#!/usr/bin/groovy

import org.fedoraproject.jenkins.koji.Koji


/**
 * getBuildInfo() step.
 */
def call(buildId) {
    def koji = new Koji(env.KOJI_API_URL)
    def buildInfo = koji.getBuildInfo(buildId.toInteger())

    // TODO: we can do this mapping directly in BuildInfo class
    def buildInfoMap = [
        'id': buildInfo.id,
        'nvr': buildInfo.nvr
        'name': buildInfo.name,
        'packageName': buildInfo.packageName,
        'version': buildInfo.version,
        'release': buildInfo.release,
        'ownerName': buildInfo.ownerName,
        'taskId': buildInfo.taskId,
        'tags': buildInfo.tags
    ]

    return buildInfoMap
}

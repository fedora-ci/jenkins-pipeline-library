#!/usr/bin/groovy

import org.fedoraproject.jenkins.koji.Koji


/**
 * getTaskInfo() step.
 */
def call(taskId) {
    def koji = new Koji(env.KOJI_API_URL)
    def taskInfo = koji.getTaskInfo(taskId.toInteger())

    // TODO: we can do this mapping directly in TaskInfo class
    def taskInfoMap = [
        'id': taskInfo.id,
        'method': taskInfo.method,
        'ownerId': taskInfo.ownerId,
        'target': taskInfo.target,
        'scratch': taskInfo.scratch,
        'draft': taskInfo.draft,
        'name': taskInfo.name,
        'packageName': taskInfo.packageName,
        'version': taskInfo.version,
        'release': taskInfo.release,
        'nvr': taskInfo.nvr,
        'buildId': taskInfo.buildId
    ]

    return taskInfoMap
}

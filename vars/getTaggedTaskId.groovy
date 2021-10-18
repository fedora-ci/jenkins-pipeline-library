#!/usr/bin/groovy

import org.fedoraproject.jenkins.koji.Koji


/**
 * getTaggedTaskId() step.
 */
def call(Map params = [:]) {
    def tagName = params.get('tagName')
    def packageName = params.get('packageName')

    def koji = new Koji(env.KOJI_API_URL)
    def taskId = koji.getTaggedTaskId(tagName, packageName)

    return taskId
}

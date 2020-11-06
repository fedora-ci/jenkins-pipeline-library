#!/usr/bin/groovy

import org.fedoraproject.jenkins.koji.Koji


/**
 * getRepoUrlAndRefFromTaskId() step.
 */
def call(taskId) {
    def url
    def ref
    def koji = new Koji()
    def buildInfo = koji.getTaskInfo(taskId.toInteger())
    if (buildInfo.source.raw.startsWith('cli-build')) {
        // the SRPM was provided via CLI

        // srpmName is something like "fedora-ci_c7e32f545a8b4a0aa209b233c44b1f50_7579642717a0a4f83488560d50e2b5b5d76eaced_0;python-pygments-pytest.f34.src.rpm"
        def srpmName = buildInfo.source.raw.split('/')[-1]

        if (!srpmName.startsWith('fedora-ci_')) {
            error('Unable to determine the repo URL and the commit hash for packages built from custom SRPMs.')
        }

        def prParts = srpmName.split(';')[0].split('_')
        // we want just the "python-pygments-pytest" part from the srpmName
        def repoName = srpmName.split(';')[1].split('.src.rpm')[0].reverse().split('\\.', 2)[1].reverse().replace(':', '/')
        url = "${env.FEDORA_CI_PAGURE_DIST_GIT_URL}/${repoName}"
        // the second item in the list should be the commit hash
        ref = prParts[2]
    } else {
        url = buildInfo.source.url

        def prefix = ~/^git\+/
        def suffix = ~/.git$/

        url -= prefix
        url -= suffix

        ref = buildInfo.source.commitId
    }

    return [url: url, ref: ref]
}

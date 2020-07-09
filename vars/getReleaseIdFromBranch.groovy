#!/usr/bin/groovy


/**
 * getReleaseIdFromBranch() step.
 */
def call(Map params = [:]) {

    def branchName = env.BRANCH_NAME

    // pull request
    if (env.CHANGE_ID) {
        // If this is a pull request, we take the target branch
        branchName = env.CHANGE_TARGET
    }

    if (branchName == 'master') {
        // 'master' means rawhide in Fedora world
        branchName = env.FEDORA_CI_RAWHIDE_RELEASE_ID
    }

    return branchName
}

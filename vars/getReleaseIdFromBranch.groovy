#!/usr/bin/groovy


/**
 * getReleaseIdFromBranch() step.
 *
 * Get release id (f31, rawhide) from current branch.
 * 
 * For pull requests, this step looks at the target branch
 * and uses it instead of the current branch.
 *
 * @return release id
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
        branchName = 'rawhide'
    }

    return branchName
}

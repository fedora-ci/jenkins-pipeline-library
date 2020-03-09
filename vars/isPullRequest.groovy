#!/usr/bin/groovy


/**
 * isPullRequest() step.
 *
 * Check whether current build is for a pull request or not.
 *
 * @return true for pull requests, false otherwise.
 */
def call(Map params = [:]) {
    // env.CHANGE_ID is set by GitHub branch source plugin, but only for pull requests
    if (env.CHANGE_ID) {
        return true
    }
    return false
}

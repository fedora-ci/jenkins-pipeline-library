#!/usr/bin/groovy


/**
 * isPullRequest() step.
 */
def call(Map params = [:]) {
    // env.CHANGE_ID is set by GitHub branch source plugin, but only for pull requests
    if (env.CHANGE_ID) {
        return true
    }
    return false
}

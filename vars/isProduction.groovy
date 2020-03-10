#!/usr/bin/groovy


/**
 * isProduction() step.
 *
 * If this is not a pull request, then it is a production-like run.
 *
 * @return false for pull requests, true otherwise.
 */
def call(Map params = [:]) {
    // env.CHANGE_ID is set by GitHub branch source plugin, but only for pull requests
    if (env.CHANGE_ID == null) {
        return true
    }
    return false
}

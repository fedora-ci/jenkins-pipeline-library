#!/usr/bin/groovy


/**
 * getGitUrl() step.
 */
def call(Map params = [:]) {

    def gitUrl = env.GIT_URL

    // pull request
    if (env.CHANGE_ID) {
        def urlList = gitUrl.split('/')
        // Yeah, but what about the repository name?
        // https://issues.jenkins-ci.org/browse/JENKINS-58450
        urlList[-2] = env.CHANGE_FORK
        gitUrl = urlList.join('/')
    }

    return gitUrl
}

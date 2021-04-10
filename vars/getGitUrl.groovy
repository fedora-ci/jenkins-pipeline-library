#!/usr/bin/groovy


/**
 * getGitUrl() step.
 */
def call(Map params = [:]) {

    def gitUrl
    if (env.GIT_URL) {
        gitUrl = env.GIT_URL
    } else {
        // The pipeline is probably skipping the default checkout
        // and therefore the env.GIT_URL is not populated.
        // Let's hope that somebody called "checkout scm" before
        // calling this step.
        gitUrl = scm.getUserRemoteConfigs()[0].getUrl()
    }

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

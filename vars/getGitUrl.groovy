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

    def changeFork = params.get('changeFork') ?: env.CHANGE_FORK
    // pull request
    if (env.CHANGE_ID && changeFork) {
        def urlList = gitUrl.split('/')

        // for GitHub, the change id is just a username;
        // but for GitLab, the change id is "username/repository"...
        // meh...
        if (changeFork.contains('/')) {
            urlList = urlList[0..-3]
            urlList += changeFork + '.git'
        } else {
            // just replace the org/username
            urlList[-2] = changeFork
        }
        gitUrl = urlList.join('/')
    }

    return gitUrl
}

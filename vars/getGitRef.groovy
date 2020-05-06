#!/usr/bin/groovy


/**
 * getGitRef() step.
 */
def call(Map params = [:]) {

    def shortRef = params.get('short', false)

    def gitRef
    if (shortRef) {
        gitRef = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
    } else {
        gitRef = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
    }

    return gitRef
}

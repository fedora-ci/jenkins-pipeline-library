#!/usr/bin/groovy


/**
 * abort() step.
 *
 * Abort current build. This step sets build result to "ABORTED" (gray icon in Jenkins),
 * and immediately stops execution of the pipeline.
 *
 * @param message
 */
def call(String message) {
    currentBuild.result = 'ABORTED'
    error(message)
}

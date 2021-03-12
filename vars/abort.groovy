#!/usr/bin/groovy


/**
 * abort() step.
 */
def call(String message, String status = 'ABORTED') {
    env.ABORT_MESSAGE = message
    currentBuild.result = status
    error(message)
}

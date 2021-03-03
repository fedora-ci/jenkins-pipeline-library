#!/usr/bin/groovy


/**
 * abort() step.
 */
def call(String message, String status = 'ABORTED') {
    currentBuild.result = status
    error(message)
}

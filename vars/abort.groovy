#!/usr/bin/groovy


/**
 * abort() step.
 */
def call(String message) {
    currentBuild.result = 'ABORTED'
    error(message)
}

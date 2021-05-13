#!/usr/bin/groovy


/**
 * isTimeoutAborted() step.
 */
def call(Map params = [:]) {
    // There doesn't seem to be a way how to check
    // if the currently running build has been timeout-aborted.
    // So we just check for how long the build has been running
    // and compare it with the expected timeout. Meh...

    def timeout = params.get('timeout')
    def timeUnit = params.get('timeUnit', 'SECONDS')

    if (timeout == null) {
        error('Required argument missing: timeout')
    }

    if (currentBuild.result != 'ABORTED') {
        // This build is not aborted...
        return false
    }

    // we need the timeout in seconds
    if (timeUnit == 'MINUTES') {
        timeout = timeout * 60
    } else if (timeUnit == 'HOURS') {
        timeout = timeout * 60 * 60
    }

    def startTime = Math.floor(currentBuild.getStartTimeInMillis() / 1000).round()
    def currentTime = Math.ceil(System.currentTimeMillis() / 1000).round()

    def duration = currentTime - startTime

    if (duration >= timeout) {
        return true
    }

    return false
}

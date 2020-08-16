#!/usr/bin/groovy


/**
 * evaluateTestingFarmResults() step.
 */
def call(result) {

    catchError {
        if (!result || result['state'] == 'error') {
            error('There was an infrastructure failure.')
        }
    }

    catchError(buildResult: 'UNSTABLE') {
        if (result['state'] == 'complete' && result['result']['overall'] == 'failed') {
            error('There are test failures.')
        }
    }
}

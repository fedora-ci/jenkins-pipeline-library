#!/usr/bin/groovy


/**
 * evaluateTestingFarmResults() step.
 */
def call(result) {

    catchError {
        if (!result || result['state'] == 'error') {
            error
        }
    }

    catchError(buildResult: 'UNSTABLE') {
        if (result['state'] == 'failed') {
            error
        }
    }
}

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

    catchFailure {
        if (result['state'] == 'failed') {
            error
        }
    }
}

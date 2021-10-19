#!/usr/bin/groovy


/**
 * getTestingFarmOverallResult() step.
 */
def call(result) {

    if (result == null) {
        return 'error'
    }

    if (result.get('state') == 'error' || result.get('result')?.get('overall') == 'error') {
        return 'error'
    }

    if (result.get('state') == 'complete' && result.get('result')?.get('overall') == 'failed') {
        return 'failed'
    }

    return 'passed'
}

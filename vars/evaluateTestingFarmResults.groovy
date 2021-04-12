#!/usr/bin/groovy


/**
 * evaluateTestingFarmResults() step.
 */
def call(result) {

    if (result == null) {
        // null means that we haven't even called Testing Farm.
        // We are either aborting the pipeline before we had a chance to call it,
        // or something completely unexpected happened...
        // In any case, there are no results to evaluate.
        return
    }

    catchError {
        if (!result || result['state'] == 'error' || result['result']['overall'] == 'error') {
            def message = 'Infrastructure Error :/'
            if (result.get('result', [:]).get('summary')) {
                message = result['result']['summary']
                env.ERROR_MESSAGE = message
            }
            error(message)
        }
    }

    catchError(buildResult: 'UNSTABLE') {
        if (result['state'] == 'complete' && result['result']['overall'] == 'failed') {
            error('There are test failures.')
        }
    }
}

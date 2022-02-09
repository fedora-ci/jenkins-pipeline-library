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

    def message = ''
    if (result.get('result', [:])?.get('summary')) {
        message = result['result']['summary']
    } else if (result.get('notes')) {
        // the whole result.result section may be missing if TF failed to dispatch the request;
        // but we can still check if there are any message with explanation in the notes...
        result.get('notes').each { note ->
            if (note.get('level') == 'error') {
                // this should be the note which describes what happened
                message = note.get('message') ?: message
            }
        }
    }
    if (message) {
        // we can store the message in the environment
        // and then reuse it for example when sending messages
        // to the message bus
        env.ERROR_MESSAGE = message
    }

    // if the result is "error", we stop the pipeline and set the Jenkins build status to "FAILURE" (default for errors)
    catchError {
        if (!result || result.get('state') == 'error' || result.get('result')?.get('overall') == 'error') {
            if (!message) {
                // we don't know what happened...
                message = 'Infrastructure Error'
            }
            error(message)
        }
    }

    // if the result is "failed", we stop the pipeline and set the Jenkins build status to "UNSTABLE"
    catchError(buildResult: 'UNSTABLE') {
        if (result.get('state') == 'complete' && result.get('result')?.get('overall') == 'failed') {
            error("There are test failures. ${message}")
        }
        if (result.get('state') == 'complete' && result.get('result')?.get('overall') == 'skipped') {
            error("Tests were skipped. ${message}")
        }
    }
}

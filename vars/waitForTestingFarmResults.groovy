#!/usr/bin/groovy

import groovy.json.JsonSlurperClassic
import groovy.time.TimeDuration
import groovy.time.TimeCategory


/**
 * waitForTestingFarmResults() step.
 */
def call(Map params = [:]) {
    def requestId = params.get('requestId')
    def timeout = params.get('timeout')

    // FIXME: use the real Testing Farm URL, once it is available
    def apiUrl = params.get('apiUrl') ?: env.FEDORA_CI_TESTING_FARM_API_URL

    if (!apiUrl) {
        error('FAIL: Testing Farm API URL is not configured')
    }

    apiUrl = apiUrl + '/v0.1/requests/' + "${requestId}"

    def wait = true
    def response
    def state
    def napTime
    def notFirstCheck = false

    def timeStart = new Date()
    def timeNow

    // FIXME: this is here for easier debugging in the early stages; let's remove it once
    // things are more stable
    def tfArtifactsBaseUrl = env.FEDORA_CI_PAGURE_DIST_GIT_URL.startsWith('https://src.osci') ? "http://artifacts.osci.redhat.com/testing-farm" : "http://artifacts.dev.testing-farm.io"

    while (wait) {
        retry(30) {
            try {
                response = httpGet(apiUrl)
            } catch(e) {
                // TODO: remove
                echo "Testing Farm Artifacts URL: ${tfArtifactsBaseUrl}/${requestId}"

                echo "ERROR: Oops, something went wrong. We were unable to call ${apiUrl} — let's wait 120 seconds and then try again: ${e.getMessage()}"
                sleep(time: 120, unit: "SECONDS")
                error("Failed to call Testing Farm: ${e.getClass().getCanonicalName()}: ${e.getMessage()}")
            }
        }
        // TODO: remove
        echo "Testing Farm Artifacts URL: ${tfArtifactsBaseUrl}/${requestId}"
        state = response.get('state')
        if (state in ['complete', 'error']) {
            return response
        }
        napTime = 90
        if (state in ['new', 'queued'] && notFirstCheck) {
            // the request can stay queued for a very long time;
            // if that is the case, don't check the status that often
            napTime = 600
        }

        checkTimeout(timeStart, timeout)

        sleep(time: napTime, unit: "SECONDS")
        notFirstCheck = true
    }
}

@NonCPS
def checkTimeout(timeStart, timeout) {
    timeNow = new Date()
    TimeDuration duration = TimeCategory.minus(timeNow, timeStart)
    if (duration.getMinutes() >= timeout) {
        error("Timeout reached and there are still no test results")
    }
}


def httpGet(url) {
    def response = httpRequest consoleLogResponseBody: false, contentType: 'APPLICATION_JSON', httpMode: 'GET', url: "${url}", validResponseCodes: '200', quiet: true
    def contentJson = new JsonSlurperClassic().parseText(response.content)
    return contentJson
}

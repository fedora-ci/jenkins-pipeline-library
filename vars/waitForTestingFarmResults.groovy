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

    apiUrl = apiUrl + '/v0.1/requests'

    def wait = true
    def response
    def state

    def timeStart = new Date()
    def timeNow

    while (wait) {
        retry(5) {
            try {
                response = httpGet(apiUrl, null, payload)
            } catch(e) {
                error("Failed to call Testing Farm: ${e.getClass().getCanonicalName()}: ${e.getMessage()}")
            }
        }
        state = response.get('state')
        if (state in ['complete', 'error']) {
            return response
        }

        timeNow = new Date()
        TimeDuration duration = TimeCategory.minus(timeNow, timeStart)
        if (duration.getMinutes() >= timeout) {
            error("Timeout reached and there are still no test results")
        }
        sleep(time: 20, unit: "SECONDS")
    }
}


@NonCPS
def httpGet(url, headers, payload) {
    url = new URL(url)
    def connection = url.openConnection()

    if (headers) {
        headers.each { key, value ->
            connection.setRequestProperty(key, value)
        }
    }

    connection.setRequestMethod("GET")

    def response = null
    try {
        connection.connect()
        response = new JsonSlurperClassic().parse(new InputStreamReader(connection.getInputStream(), "UTF-8"))
    } finally {
        connection.disconnect()
    }

    return response
}

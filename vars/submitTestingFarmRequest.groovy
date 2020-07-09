#!/usr/bin/groovy

import groovy.json.JsonSlurperClassic


/**
 * submitTestingFarmRequest() step.
 */
def call(Map params = [:]) {
    // TODO: we could validate the payload against the Testing Farm schema
    def payload = params.get('payload')

    def apiUrl = params.get('apiUrl') ?: env.FEDORA_CI_TESTING_FARM_API_URL

    def headers = ['Content-Type': 'application/json']

    if (!apiUrl) {
        error('FAIL: Testing Farm API URL is not configured')
    }

    apiUrl = apiUrl + '/v0.1/requests'

    retry(5) {
        try {
            return httpPost(apiUrl, null, payload)
        } catch(e) {
            error("Failed to call Testing Farm: ${e.getClass().getCanonicalName()}: ${e.getMessage()}")
        }
    }
}


@NonCPS
def httpPost(url, headers, payload) {
    url = new URL(url)
    def connection = url.openConnection()

    if (headers) {
        headers.each { key, value ->
            connection.setRequestProperty(key, value)
        }
    }

    connection.setRequestMethod("POST")
    connection.setDoOutput(true)
    connection.getOutputStream().write(payload.getBytes("UTF-8"))

    def response = null
    try {
        connection.connect()
        response = new JsonSlurperClassic().parse(new InputStreamReader(connection.getInputStream(), "UTF-8"))
    } finally {
        connection.disconnect()
    }

    return response
}

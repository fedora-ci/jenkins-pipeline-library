#!/usr/bin/groovy

import groovy.json.JsonSlurperClassic


/**
 * submitTestingFarmRequest() step.
 */
def call(Map params = [:]) {
    // TODO: we could validate the payload against the Testing Farm schema
    def payload = params.get('payload')

    def apiUrl = params.get('apiUrl') ?: env.FEDORA_CI_TESTING_FARM_API_URL

    if (!apiUrl) {
        error('FAIL: Testing Farm API URL is not configured')
    }

    apiUrl = apiUrl + '/v0.1/requests'

    retry(5) {
        try {
            return httpPost(apiUrl, payload)
        } catch(e) {
            error("Failed to call Testing Farm: ${e.getClass().getCanonicalName()}: ${e.getMessage()}")
        }
    }
}


def httpPost(url, payload) {
    def response = httpRequest consoleLogResponseBody: true, contentType: 'APPLICATION_JSON', httpMode: 'POST', requestBody: "${payload}", url: "${url}", validResponseCodes: '200'
    def contentJson = new JsonSlurperClassic().parseText(response.content)
    return contentJson
}

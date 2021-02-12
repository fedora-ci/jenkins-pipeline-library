#!/usr/bin/groovy

import groovy.json.JsonSlurperClassic


/**
 * checkTestingFarmRequestStatus() step.
 */
def call(Map params = [:]) {
    def requestId = params.get('requestId')

    def apiUrl = params.get('apiUrl') ?: env.FEDORA_CI_TESTING_FARM_API_URL

    if (!apiUrl) {
        error('FAIL: Testing Farm API URL is not configured')
    }

    apiUrl = apiUrl + '/v0.1/requests/' + "${requestId}"

    def response
    def contentJson

    retry(30) {
        try {
            response = httpRequest(
                consoleLogResponseBody: true,
                contentType: 'APPLICATION_JSON',
                httpMode: 'GET',
                url: "${apiUrl}",
                validResponseCodes: '200'
            )
            contentJson = new JsonSlurperClassic().parseText(response.content)
            return [status: contentJson.get('state'), response: contentJson]
        } catch(e) {
            sleep(time: 10, unit: "SECONDS")
            error("Failed to call Testing Farm: ${e.getClass().getCanonicalName()}: ${e.getMessage()}")
        }
    }
}

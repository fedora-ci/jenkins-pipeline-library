#!/usr/bin/groovy

import org.fedoraproject.jenkins.Utils


/**
 * submitTestingFarmRequest() step.
 */
def call(Map params = [:]) {
    // TODO: we could validate the payload against the Testing Farm schema
    def payload = params.get('payload')
    def payloadMap = params.get('payloadMap')
    def suppressSslErrors = params.get('suppressSslErrors', false)?.toBoolean()

    if (!payload) {
        if (payloadMap) {
            payload = Utils.mapToJsonString(payloadMap, true)
            echo("Testing Farm payload: ${payload}")
        } else {
            error("Missing Testing Farm payload")
        }
    }


    def apiUrl = params.get('apiUrl') ?: env.FEDORA_CI_TESTING_FARM_API_URL

    if (!apiUrl) {
        error('FAIL: Testing Farm API URL is not configured')
    }

    apiUrl = apiUrl + '/v0.1/requests'

    retry(30) {
        try {
            return httpPost(apiUrl, payload, suppressSslErrors)
        } catch(e) {
            echo "ERROR: Oops, something went wrong. We were unable to call ${apiUrl} — let's wait 120 seconds and then try again: ${e.getMessage()}"
            sleep(time: 120, unit: "SECONDS")

            error("Failed to call Testing Farm: ${e.getClass().getCanonicalName()}: ${e.getMessage()}")
        }
    }
}


def httpPost(url, payload, suppressSslErrors) {
    def response = httpRequest(
        consoleLogResponseBody: false,
        contentType: 'APPLICATION_JSON',
        httpMode: 'POST',
        requestBody: "${payload}",
        url: "${url}",
        validResponseCodes: '200',
        ignoreSslErrors: suppressSslErrors
    )
    def contentJson = Utils.jsonStringToMap(response.content)
    return contentJson
}

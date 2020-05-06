#!/usr/bin/groovy

import groovy.json.JsonSlurperClassic


/**
 * submitTestingFarmRequest() step.
 */
def call(Map params = [:]) {
    // TODO: we could validate the payload against the Testing Farm schema
    def payload = params.get('payload')

    // FIXME: use the real Testing Farm URL, once it is available
    def apiUrl = params.get('apiUrl') ?: 'https://localhost:8080/api/v1/requests'

    retry(5) {
        return httpPost(apiUrl, null, payload)
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

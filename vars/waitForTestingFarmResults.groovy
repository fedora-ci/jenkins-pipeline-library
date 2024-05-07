#!/usr/bin/groovy

import groovy.json.JsonSlurperClassic


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

    // FIXME: this is here for easier debugging in the early stages; let's remove it once
    // things are more stable
    def tfArtifactsBaseUrl = env.FEDORA_CI_PAGURE_DIST_GIT_URL.startsWith('https://src.osci') ? "https://artifacts.osci.redhat.com/testing-farm" : "http://artifacts.dev.testing-farm.io"

    echo "Testing Farm API Request URL: ${apiUrl}"
    echo "Testing Farm Artifacts URL: ${tfArtifactsBaseUrl}/${requestId}"
    echo "\n"

    sh(
        script: """
set +x
prev_state="none"
while true
do
  state="\$(curl --retry 10 --retry-connrefused --connect-timeout 10 --retry-delay 30  -s '${apiUrl}' | jq --raw-output ''.state'')"
  if [ "\$state" = "complete" ] || [ "\$state" = "error" ]; then
    echo "Done! The current state is \\"\$state\\"."
    break
  fi
  if [ "\$state" != "\$prev_state" ]; then
    echo "The current state is \\"\$state\\"."
    echo "Waiting for Testing Farm..."
  fi
  prev_state="\$state"
  sleep 90
done
""", label: "Wait for test results"
    )

    def response = httpRequest(
        consoleLogResponseBody: false,
        contentType: 'APPLICATION_JSON',
        httpMode: 'GET',
        url: "${apiUrl}",
        validResponseCodes: '200',
        quiet: true
    )
    def contentJson = new JsonSlurperClassic().parseText(response.content)
    return contentJson
}

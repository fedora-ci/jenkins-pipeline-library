#!/usr/bin/groovy


/**
 * repoHasStiTests() step.
 */
def call(Map params = [:]) {

    def repoUrl = params.get('repoUrl')
    def branch = params.get('branch', 'master')
    def stiUrl

    if (repoUrl.contains('src.fedoraproject.org')) {
        stiUrl = repoUrl + "/raw/${branch}/f/tests/tests.yml"
    } else {
        error("Unsupported repo URL: ${repoUrl}")
    }

    def response = httpRequest consoleLogResponseBody: false, httpMode: 'GET', url: "${stiUrl}", validResponseCodes: '200,404'
    if (response.status == 200) {
        return True
    } else if (response.status == 404) {
        return False
    } else {
        error("HTTP GET on ${stiUrl} returned an unexpected return code: ${response.status}")
    }
}

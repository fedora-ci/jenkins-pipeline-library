#!/usr/bin/groovy


/**
 * repoHasTmtTests() step.
 */
def call(Map params = [:]) {

    def repoUrl = params.get('repoUrl')
    def branch = params.get('branch', 'master')
    def tmtUrl

    if (repoUrl.contains('src.fedoraproject.org')) {
        tmtUrl = repoUrl + "/raw/${branch}/f/.fmf/version"
    } else {
        error("Unsupported repo URL: ${repoUrl}")
    }

    def response = httpRequest consoleLogResponseBody: false, httpMode: 'GET', url: "${tmtUrl}", validResponseCodes: '200,404'
    if (response.status == 200) {
        return true
    } else if (response.status == 404) {
        return false
    } else {
        error("HTTP GET on ${tmtUrl} returned an unexpected return code: ${response.status}")
    }
}

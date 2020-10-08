#!/usr/bin/groovy


/**
 * repoHasTests() step.
 */
def call(Map params = [:]) {

    def repoUrl = params.get('repoUrl')
    def ref = params.get('ref')
    def stiUrl
    def tmtUrl

    // TODO: these templates should probably go to some config file
    if (repoUrl.contains('src.fedoraproject.org')) {
        stiUrl = repoUrl + "/blob/${ref}/f/tests/tests.yml"
        tmtUrl = repoUrl + "/blob/${ref}/f/.fmf/version"
    } else {
        error("Unsupported repo URL: ${repoUrl}")
    }

    // check FMF
    def response = httpRequest consoleLogResponseBody: false, httpMode: 'GET', url: "${tmtUrl}", validResponseCodes: '200,404'
    if (response.status == 200) {
        return 'fmf'
    } else if (response.status == 404) {
        // No FMF, but let's check STI
    } else {
        error("HTTP GET on ${tmtUrl} returned an unexpected return code: ${response.status}")
    }

    // check STI
    response = httpRequest consoleLogResponseBody: false, httpMode: 'GET', url: "${stiUrl}", validResponseCodes: '200,404'
    if (response.status == 200) {
        return 'sti'
    } else if (response.status == 404) {
        // do nothing
    } else {
        error("HTTP GET on ${stiUrl} returned an unexpected return code: ${response.status}")
    }
}

#!/usr/bin/groovy

/**
 * readFileFromArtifactStorage() step.
 */
def call(Map params = [:]) {
    def url = params.get('url')
    def timeoutSeconds = params.get('timeoutSeconds', 900)?.toInteger()

    def response
    if (url) {
        timeout(time: timeoutSeconds, unit: 'SECONDS') {
            waitUntil(initialRecurrencePeriod: 15000, quiet: true) {
                // Sync to artifact storate happens in background, so Testing Farm can report the job to be done,
                // but the data is still not in the artifact storage -- therefore the 404
                response = httpRequest(url: url, validResponseCodes: '100:404', quiet: true)
                return response.status == 200
            }
            return response.content
        }
    }

    return ''
}

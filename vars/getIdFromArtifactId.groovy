#!/usr/bin/groovy


/**
 * getIdFromArtifactId() step.
 *
 * Experimental step. The API can change...
 */
def call(Map params = [:]) {

    def artifactId = params.get('artifactId', '')
    def additionalArtifactIds = params.get('additionalArtifactIds', '')
    def separator = params.get('separator', ',')

    def resultIds = []

    if (artifactId) {
        artifactId.split(',').each { a ->
            resultIds.add("${a}".split(':')[1])
        }
    }

    if (additionalArtifactIds) {
        additionalArtifactIds.split(',').each { a ->
            resultIds.add("${a}".split(':')[1])
        }
    }

    return resultIds.join(separator)
}

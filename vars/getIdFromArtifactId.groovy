#!/usr/bin/groovy

import org.fedoraproject.jenkins.Utils


/**
 * getIdFromArtifactId() step.
 *
 * Experimental step. The API can change...
 */
def call(Map params = [:]) {

    def artifactId = params.get('artifactId', '')
    def additionalArtifactIds = params.get('additionalArtifactIds', '')
    def separator = params.get('separator', ',')

    if (Utils.isCompositeArtifact(artifactId)) {
        artifactId = artifactId.split('->')[0] - '(' - ')'
    }

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

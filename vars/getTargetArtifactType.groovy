#!/usr/bin/groovy

import org.fedoraproject.jenkins.Utils


/**
 * getTargetArtifactType() step.
 */
def call(artifactId) {

    def targetArtifactId = artifactId

    if (Utils.isCompositeArtifact(artifactId)) {
        targetArtifactId = Utils.getTargetArtifactId(artifactId)
    }

    return targetArtifactId.split(':')[0]
}

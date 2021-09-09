#!/usr/bin/groovy

import org.fedoraproject.jenkins.Utils


/**
 * getArtifactType() step.
 */
def call(artifactId) {

    // artifactId can be a composite artifact: (brew-build:1234)->dist-git-pr:abcd,
    // and we are only interested in the "brew-build:1234" part here
    if (Utils.isCompositeArtifact(artifactId)) {
        def compositeArtifactId = artifactId
        def artifactIds = artifactId.split('->')[0] - '(' - ')'
        // there can be multiple brew-builds, we just take the first one
        artifactId = artifactIds.split(',')[0]
        if (!artifactId) {
            // composite artifactId looks like this: ()->type:1234,
            // so let's just return the target artifactId (type:1234)
            artifactId = compositeArtifactId.split('->')[1]
        }
    }

    return artifactId.split(':')[0]
}

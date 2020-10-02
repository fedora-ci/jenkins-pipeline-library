package org.fedoraproject.jenkins

import java.time.Instant
import java.security.MessageDigest
import javax.xml.bind.annotation.adapters.HexBinaryAdapter

import org.fedoraproject.jenkins.koji.model.BuildSource


class Utils {

    /*
     * Returns randomly generated pipeline ID.
     *
     * @return generated pipeline ID
     */
    static String generatePipelineId() {
        return UUID.randomUUID().toString()
    }

    /*
     * Returns current UTC timestamp.
     *
     * @return current UTC timestamp
     */
    static String getTimestamp() {
        return Instant.now().toString()
    }

    /*
     * Checks if given artifact is a composite artifact or not.
     *
     * @return true if given artifact is a composite artifact, false otherwise.
     */
    static Boolean isCompositeArtifact(def artifactId) {
        if (artifactId && artifactId[0] == '(' && artifactId.contains(')->')) {
            return true
        }

        return false
    }

    /*
     * Extract and return the target artifact Id.
     *
     * @return target artifact Id
     */
    static String getTargetArtifactId(def artifactId) {
        return artifactId.split('->')[1]
    }

    static String string2sha256(def input) {
        def digest = MessageDigest.getInstance("SHA-256")
        def hash = digest.digest(input.getBytes())
        return new HexBinaryAdapter().marshal(hash).toLowerCase()
    }

    static String getReleaseIdFromBranch(def env) {
        def branchName = env.BRANCH_NAME

        // pull request
        if (env.CHANGE_ID) {
            // If this is a pull request, we take the target branch
            branchName = env.CHANGE_TARGET
        }

        if (branchName == 'master') {
            // 'master' means rawhide in Fedora world
            branchName = env.FEDORA_CI_RAWHIDE_RELEASE_ID
        }

        return branchName
    }

    static String getIdFromArtifactId(Map params = [:]) {
        def artifactId = params.get('artifactId', '')
        def additionalArtifactIds = params.get('additionalArtifactIds', '')
        def separator = params.get('separator', ',')
        def asArray = params.get('asArray', false)

        if (isCompositeArtifact(artifactId)) {
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

        if (!asArray) {
            resultIds = resultIds.join(separator)
        }
        return resultIds
    }

    /*
     * Returns RPM filename extracted from the provided BuildSource
     *
     * Note this is not 100% reliable as some guessing is involved.
     *
     * @return RPM filename
     */
    static String getRPMfilenameFromSource(def source) {

        if (source.isRepoUrlWithRef() || !source.raw.endsWith('src.rpm')) {
            throw new IllegalArgumentException('Unable to extract RPM filename from source: ' + source)
        }

        source = source.raw

        def filename = source.split('/')[-1]

        return filename
    }


    /*
     * Returns NVR extracted from the provided BuildSource
     *
     * Note this is not 100% reliable as some guessing is involved.
     *
     * @return NVR
     */
    static String getNVRfromSource(def source) {

        def filename = getRPMfilenameFromSource(source)

        def nvr = filename[0..-('.src.rpm'.length() + 1)]

        return nvr
    }

    /*
     * Splits RPM filename into (name, version, release, arch).
     *
     * Usage:
     *   def (name, version, release, arch) = Utils.splitRpmFilename(filename)
     *
     * This is a reimplementation of following function from YUM:
     * https://github.com/rpm-software-management/yum/blob/043e869b08126c1b24e392f809c9f6871344c60d/rpmUtils/miscutils.py#L301
     *
     * @return tuple (name, version, release, arch)
     */
    static def splitRpmFilename(def filename) {
        if (filename[-4..-1] == '.rpm') {
            filename = filename[0..-5]
        }

        def archIndex = filename.lastIndexOf('.')
        def arch = filename[archIndex+1..-1]
        filename = filename[0..archIndex-1]

        def relIndex = filename.lastIndexOf('-')
        def rel = filename[relIndex+1..-1]
        filename = filename[0..relIndex-1]

        def verIndex = filename.lastIndexOf('-')
        def ver = filename[verIndex+1..-1]
        filename = filename[0..verIndex-1]

        def name = filename

        // TODO: what about epoch?

        return [name, ver, rel, arch]
    }
}

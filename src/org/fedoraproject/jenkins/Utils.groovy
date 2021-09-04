package org.fedoraproject.jenkins

import groovy.json.JsonSlurperClassic
import groovy.json.JsonBuilder
import java.time.Instant
import java.security.MessageDigest
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

import org.fedoraproject.jenkins.koji.model.BuildSource


class Utils {

    /*
     * Returns randomly generated pipeline ID.
     *
     * @return generated pipeline ID
     */
    static String generateRandomPipelineId() {
        return UUID.randomUUID().toString()
    }

    /*
     * Returns a pipeline ID for the current build.
     *
     * The pipeline ID is generated from the job name and its parameters.
     * Running the same job, with the same parameters will produce the same
     * pipeline ID.
     *
     * @return pipeline ID
     */
    static String generatePipelineIdFromJobNameAndParams(def env, def params) {
        // string2sha256(env.JOB_NAME + env.BUILD_ID)
        def pipelineId = "${env.JOB_NAME}("

        params.each { key, value ->
            pipelineId += "${key}:${value}"
        }

        pipelineId += ')'

        return string2sha256(pipelineId)
    }

    /*
     * Returns a pipeline ID for the artifactId and testcase name.
     *
     * Working with the same artifactId and the same testcase name
     * will produce the same pipeline ID.
     *
     * @return pipeline ID
     */
    static String generatePipelineIdFromArtifactIdAndTestcase(def artifactId, def testcase) {
        return string2sha256(artifactId + testcase)
    }

    /*
     * Returns current UTC timestamp.
     *
     * @return current UTC timestamp
     */
    static String getTimestamp() {
        return Instant.now().toString()
    }

    /* Converts Groovy map to JSON string
     *
     * @return JSON string
     */
    static String mapToJsonString(def map, def pretty) {
        def json
        if (pretty) {
            json = new JsonBuilder(map).toPrettyString()
        } else {
            json = new JsonBuilder(map).toString()
        }
        return json
    }

    /* Converts JSON string to Groovy map
     *
     * @return Groovy map
     */
    static Map jsonStringToMap(def jsonStr) {
        return new JsonSlurperClassic().parseText(jsonStr)
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
        return hash.encodeHex().toString().toLowerCase()
    }

    /*
     * Extract results from XUnit and return them as a map.
     *
     * Result example:
     * [
     *    "test-suite-1": "passed",
     *    "test-suite-2": "failed"
     * ]
     *
     * @return result map
     */
    static Map xunitResults2map(def xunit) {
        def result = [:]
        if (xunit) {
            xunit = xunit.replace('\\"', '"')
            def xml = new XmlSlurper().parseText(xunit)
            if (xml.testsuite.size() > 0) {
                xml.testsuite.each { ts -> result[ts.@'name'] = ts.@'overall-result' }
            }
        }
        return result
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

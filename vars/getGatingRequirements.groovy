#!/usr/bin/groovy

import groovy.json.JsonSlurperClassic

import org.fedoraproject.jenkins.koji.Koji
import org.fedoraproject.jenkins.Utils


/**
 * getGatingRequirements() step.
 */
def call(Map params = [:]) {
    def artifactId = params.get('artifactId')
    def decisionContext = params.get('decisionContext')
    def productVersion = params.get('productVersion')
    def testcase = params.get('testcase')
    def testcasePrefix = params.get('testcasePrefix')
    def scenarioPrefix = params.get('scenarioPrefix')
    def scenario = params.get('scenario')

    def targetArtifactId
    if (Utils.isCompositeArtifact(artifactId)) {
        targetArtifactId = Utils.getTargetArtifactId(artifactId)
    } else {
        targetArtifactId = artifactId
    }

    def artifactType = targetArtifactId.split(':')[0]
    def taskId = targetArtifactId.split(':')[1]

    if (!artifactType in ['koji-build', 'brew-build']) {
        error("Unsupported artifact type: ${artifactType}; only koji-build/brew-build is supported by getGatingRequirements() step")
    }

    // Greenwave's "subject_type" is our artifact type, but with underscores
    def subjectType = "koji_build"

    def koji = new Koji(env.KOJI_API_URL)
    def nvr = "${koji.getTaskInfo(taskId.toInteger()).nvr}"
    koji = null

    filteredRequirements = [] as Set

    def isRetry = false
    retry(10) {
        // retry Greenwave query up to 10 times
        if (isRetry) {
            // sleep 20 seconds if this is not a first attempt
            sleep(time: 20, unit: 'SECONDS')
        }
        isRetry = true

        gatingDecisionResponse = httpRequest(
            url: env.FEDORA_CI_GREENWAVE_API_URL + '/decision',
            httpMode: 'POST',
            acceptType: 'APPLICATION_JSON',
            contentType: 'APPLICATION_JSON',
            validResponseCodes: '200',
            consoleLogResponseBody: false,
            requestBody: """
                {
                    "decision_context": "${decisionContext}",
                    "product_version": "${productVersion}",
                    "subject_type": "${subjectType}",
                    "subject_identifier": "${nvr}",
                    "verbose": false
                }
            """
        )
    }
    gatingDecision = new JsonSlurperClassic().parseText(gatingDecisionResponse.content)

    results = gatingDecision.get('satisfied_requirements') + gatingDecision.get('unsatisfied_requirements')
    results.each { requirement ->
        if (testcase && testcase != requirement.get('testcase')) {
            return
        }
        if (testcasePrefix && !requirement.get('testcase').startsWith(testcasePrefix)) {
            return
        }
        if (scenario && scenario != requirement.get('scenario')) {
            return
        }
        if (scenarioPrefix && !requirement.get('scenario').startsWith(scenarioPrefix)) {
            return
        }

        filteredRequirements.add(requirement)
    }

    return filteredRequirements
}

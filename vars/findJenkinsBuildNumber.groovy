#!/usr/bin/groovy


/**
 * findJenkinsBuildNumber() step.
 */
def call(Map params = [:]) {

    def jobName = params.get('jobName', '')
    def wantedBuildParams = params.get('buildParams', [:])
    def isRunning = params.get('isRunning', false)?.toBoolean()
    def result = params.get('result', 'SUCCESS')

    def job = Jenkins.instance.getItemByFullName(jobName)
    if (!job) {
        error("Job \"${jobName}\" doesn't exist")
    }

    def foundBuildNumber = null
    job.getBuilds().any { build ->

        if (build.isInProgress() != isRunning) {
            return false  // continue
        }
        if (!build.getResult() || build.getResult().toString() != result) {
            return false  // continue
        }

        def paramActions = build.getActions(hudson.model.ParametersAction)
        if (!paramActions || paramActions.isEmpty()) {
            return false  // continue
        }

        def buildParams = paramActions[0]
        def allMatch = wantedBuildParams.every { k, v ->
            def param = buildParams.getParameter(k.toString())
            return param && param.getValue() == v
        }

        if (allMatch) {
            foundBuildNumber = build.number
            return true  // break loop
        }
    }
    return foundBuildNumber
}

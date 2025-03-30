#!/usr/bin/groovy

import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper


/**
 * getJenkinsBuildEnv() step.
 */
def call(Map params = [:]) {

    def jobName = params.get('jobName')
    def buildNumber = params.get('buildNumber')

    def job = Jenkins.instance.getItemByFullName(jobName)
    if (!job) {
        error("Job \"${jobName}\" doesn't exist")
    }

    if (buildNumber == null) {
        error("Missing required parameter: buildNumber")
    }

    def build = job.getBuildByNumber(buildNumber)
    if (!build) {
        error("Build #${buildNumber} for job \"${jobName}\" does not exist")
    }

    return new RunWrapper(build, true).buildVariables
}

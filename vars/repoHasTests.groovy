#!/usr/bin/groovy

/**
 * repoHasTests() step.
 */
def call(Map params = [:]) {

    def repoUrl = params.get('repoUrl')
    def ref = params.get('ref')
    def context = params.get('context')
    def useCloneCredentials = params.get('useCloneCredentials', false)
    def fetchMergeRequests = params.get('fetchMergeRequests', false)

    if (useCloneCredentials && env.GIT_CLONE_AUTH_STRING) {
        repoUrl = repoUrl.replace('://', "://\${GIT_CLONE_AUTH_STRING}@")
    }

    dir("temp-repoHasTests${env.BUILD_ID}") {
        try {

            def retryCounter = 0

            // retry git-clone 10 times, and sleep 1 minute between retries
            retry(10) {
                if (retryCounter) {
                    sleep(time: 1, unit: 'MINUTES')
                }
                retryCounter += 1
                sh("git clone ${repoUrl} .")

                if (fetchMergeRequests) {
                    sh("git fetch origin +refs/merge-requests/*/head:refs/remotes/origin/merge-requests/*")
                }
            }
            // check that the git reference exists
            def refExists = sh(script: "git checkout ${ref}", returnStatus: true)
            if (refExists != 0) {
                echo """
*******************************************************************************
    Given commit hash (${ref}) is not in the repository.
    Somebody probably force-pushed.
    In any case, there is nothing we can do here...
*******************************************************************************
                """
                return [:]
            }
            sh("git reset --hard ${ref}")

            // TODO: This whole "CI config" section shouldn't be here.
            // We need to find a better place how to work with repositories.
            // And therefore, this "CI config" feature is not mentioned in documentation.
            def ciConfig = [:]
            def plans = []
            def gatingPath = findFiles glob: 'gating.yaml'
            echo "CI config in ${repoUrl} (${ref}): ${gatingPath}"
            if (gatingPath) {
                // FIXME: Groovy 2.x in Jenkins doesn't have a built-in support for YAML,
                // and I don't want to add an external dependency.
                // See also the comment above -- this whole section shouldn't be here
                // and it will be moved elsewhere soon-ish.

                // Check if a relevant entry (see regex expression) is in gating.yaml
                def has_gating_request = sh(
script: """
python3 <<SCRIPT
from pathlib import Path
import re
import yaml

PLAN_REGEX = re.compile(r"^(fedora-ci\\.koji-build|osci\\.brew-build)\\.(?:/.+)\\.functional\$")

with Path("gating.yaml").open("r") as f:
    for gating in yaml.load_all(f, yaml.BaseLoader):
        for rule in gating.get("rules", []):
            if PLAN_REGEX.search(rule.get("test_case_name", "")):
                # Found a specific plan that was requested
                exit(0)

# gating.yaml did not have an entry requesting a specific plan
exit(1)
SCRIPT
""", returnStatus: true)
                if (has_gating_request == 0) {
                    def contextStr = ''
                    context.each { key, value ->
                        contextStr += " --context ${key}=${value}"
                    }
                    plans = sh(script: "tmt ${contextStr} plan ls --filter enabled:true", returnStdout: true).trim().split('\n').findAll{it != null && !it.isEmpty()}
                }
            }

            // check FMF first
            def stdFmf = findFiles glob: '.fmf/version'

            echo "FMF tests in ${repoUrl} (${ref}): ${stdFmf}"
            if (stdFmf) {
                return [type: 'fmf', files: stdFmf.collect{ it.path }, ciConfig: ciConfig, plans: plans]
            }

            // if FMF tests were not found, let's try STI      
            def stdStiFiles = findFiles glob: 'tests/tests*.yml'
            def nonStdStiFiles = findFiles glob: 'tests*.yml'

            echo "STI tests in ${repoUrl} (${ref}): ${stdStiFiles} ${nonStdStiFiles}"
            if (stdStiFiles || nonStdStiFiles) {
                return [type: 'sti', files: (stdStiFiles + nonStdStiFiles).collect{ it.path }, ciConfig: ciConfig]
            }

            return [:]
        } finally {
            deleteDir()
        }
    }

}

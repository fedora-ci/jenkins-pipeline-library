#!/usr/bin/groovy

/**
 * repoHasTests() step.
 */
def call(Map params = [:]) {

    def repoUrl = params.get('repoUrl')
    def ref = params.get('ref')

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
            }
            // check that the commit hash exists
            def refExists = sh(script: "git cat-file -e ${ref}", returnStatus: true)
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
            def ciConfigPath = findFiles glob: 'ci.fmf'
            echo "CI config in ${repoUrl} (${ref}): ${ciConfigPath}"
            if (ciConfigPath) {
                // FIXME: Groovy 2.x in Jenkins doesn't have a built-in support for YAML,
                // and I don't want to add an external dependency.
                // See also the comment above -- this whole section shouldn't be here
                // and it will be moved elsewhere soon-ish.

                // Convert ci.fmf (YAML) to ci.fmf.json (JSON)
                sh(
"""
python3 -c "import yaml, json; y=yaml.safe_load(open('ci.fmf')); json.dump(y, open('ci.fmf.json', 'w'))"
""")
                ciConfig = readJSON(file: 'ci.fmf.json')
            }

            // check STI first
            def stdStiFiles = findFiles glob: 'tests/tests*.yml'
            def nonStdStiFiles = findFiles glob: 'tests*.yml'

            echo "STI tests in ${repoUrl} (${ref}): ${stdStiFiles} ${nonStdStiFiles}"
            if (stdStiFiles || nonStdStiFiles) {
                return [type: 'sti', files: (stdStiFiles + nonStdStiFiles).collect{ it.path }, ciConfig: ciConfig]
            }

            // if STI tests were not found, let's try FMF
            def stdFmf = findFiles glob: '.fmf/version'
            echo "FMF tests in ${repoUrl} (${ref}): ${stdFmf}"

            if (stdFmf) {
                return [type: 'fmf', files: stdFmf.collect{ it.path }, ciConfig: ciConfig]
            }

            return [:]
        } finally {
            deleteDir()
        }
    }

}

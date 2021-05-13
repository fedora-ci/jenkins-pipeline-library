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

            // check STI first
            def stdStiFiles = findFiles glob: 'tests/tests*.yml'
            def nonStdStiFiles = findFiles glob: 'tests*.yml'

            echo "STI tests in ${repoUrl} (${ref}): ${stdStiFiles} ${nonStdStiFiles}"
            if (stdStiFiles || nonStdStiFiles) {
                return [type: 'sti', files: (stdStiFiles + nonStdStiFiles).collect{ it.path } ]
            }

            // if STI tests were not found, let's try FMF
            def stdFmf = findFiles glob: '.fmf/version'
            echo "FMF tests in ${repoUrl} (${ref}): ${stdFmf}"

            if (stdFmf) {
                return [type: 'fmf', files: stdFmf.collect{ it.path }]
            }

            return [:]
        } finally {
            deleteDir()
        }
    }

}

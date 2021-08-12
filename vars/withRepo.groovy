#!/usr/bin/groovy

/**
 * withRepo() step.
 */
def call(Map params = [:], body) {

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

            // evaluate the body block
            // def config = [:]
            // body.resolveStrategy = Closure.DELEGATE_FIRST
            // body.delegate = config
            body()

        } finally {
            deleteDir()
        }
    }
}

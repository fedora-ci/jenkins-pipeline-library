#!/usr/bin/groovy


/**
 * repoHasTests() step.
 */
def call(Map params = [:]) {

    def repoUrl = params.get('repoUrl')
    def ref = params.get('ref')

    sh('ls -la')
    dir("temp-repoHasTests${env.BUILD_ID}") {
        checkout([$class: 'GitSCM', branches: [[name: ref ]], userRemoteConfigs: [[url: repoUrl ]]])

        sh('ls -la')
        // check STI first
        def stdStiFiles = findFiles glob: 'tests/tests*.yml'
        def nonStdStiFiles = findFiles glob: 'tests*.yml'

        echo "STI tests in ${repoUrl} (${ref}): ${stdStiFiles} ${nonStdStiFiles}"
        if (stdStiFiles || nonStdStiFiles) {
            return [type: 'sti', files: stdStiFiles + nonStdStiFiles]
        }

        // if STI tests were not found, let's try FMF
        def stdFmf = findFiles glob: '.fmf/version'
        echo "FMF tests in ${repoUrl} (${ref}): ${stdFmf}"

        if (stdFmf) {
            return [type: 'fmf', files: stdFmf]
        }

        deleteDir()
        return [:]
    }
}

#!/usr/bin/groovy


/**
 * abort() step.
 *
 * Abort current build. This step sets build result to "ABORTED" (gray icon in Jenkins),
 * and immediately stops execution of the pipeline.
 *
 * @param 
 */
def call(Map params = [:]) {
    currentBuild.result = 'ABORTED'
    error(params[msg])
}

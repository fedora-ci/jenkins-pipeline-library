#!/usr/bin/groovy

import org.fedoraproject.jenkins.Utils


/**
 * xunitResults2map() step.
 */
def call(Map params = [:]) {
    def xunit = params.get('xunit')

    return Utils.xunitResults2map(xunit)
}

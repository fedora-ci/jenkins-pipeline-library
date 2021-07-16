#!/usr/bin/groovy

import org.fedoraproject.jenkins.Utils

/**
 * gzip() step.
 */
def call(String s) {
	return Utils.gzip()
}

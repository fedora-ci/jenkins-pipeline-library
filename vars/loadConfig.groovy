#!/usr/bin/groovy

import groovy.json.JsonSlurper


/**
 * loadConfig() step.
 */
def call(Map params = [:]) {
    def profileName = params.get('profile')
    def configFile = 'config.json'

    if (!fileExists(configFile)) {
        error("Config file \"${configFile}\" doesn't exist")
    }

    def rawConfig = readFile(configFile)
    def config = new JsonSlurper().parseText(rawConfig)

    if (profileName) {
        def profile = config.get('profiles', [:])?.get(profileName)
        if (profile == null) {
            error("Profile \"${profileName}\" doesn't exist in ${configFile}")
        }
        return profile
    }

    return config
}

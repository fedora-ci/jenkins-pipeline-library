#!/usr/bin/groovy


/**
 * loadConfig() step.
 */
def call(Map params = [:]) {
    def profileName = params.get('profile')
    def configFile = 'config.json'

    if (!profileName) {
        error('No profile name specified, cannot continue...')
    }

    if (!fileExists(configFile)) {
        error("Config file \"${configFile}\" doesn't exist")
    }

    def config = readJSON(file: configFile)
    def profile = config.get('profiles', [:])?.get(profileName)

    if (profile == null) {
        error("Profile \"${profileName}\" doesn't exist in ${configFile}")
    }

    return profile
}

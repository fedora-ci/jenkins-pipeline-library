package org.fedoraproject.jenkins.mbs

import groovy.json.JsonSlurperClassic


/*
 * A collection of helper methods for MBS.
 */
class Mbs implements Serializable {

    String url
    String apiUrl

    Mbs() {
        this(System.getenv('FEDORA_CI_MBS_URL') ?: 'https://mbs.fedoraproject.org')
    }

    /*
     * Constructor.
     *
     * @param url MBS URL
     */
    Mbs(String url) {
        this.url = url
        this.apiUrl = url + '/module-build-service/1'
    }

    /*
     * Get module build info.
     *
     * @param mbsId 
     * @return a map with results
     */
    def getModuleBuildInfo(String mbsId) {
        def url = this.apiUrl + '/module-builds/' + mbsId

        def response = new URL(url).text
        def contentJson = new JsonSlurperClassic().parseText(response)
        return contentJson
    }

    /*
     * Get module name.
     *
     * @param buildInfo Obtained via getModuleBuildInfo()
     * @return module name
     */
    def getModuleName(def buildInfo) {
        if (!buildInfo) {
            return ''
        }
        return "${buildInfo.name}:${buildInfo.stream}:${buildInfo.version}:${buildInfo.context}"
    }
}

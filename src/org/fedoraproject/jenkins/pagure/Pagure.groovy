package org.fedoraproject.jenkins.pagure

import groovy.json.JsonSlurperClassic


/*
 * A collection of helper methods for Pagure.
 */
class Pagure implements Serializable {

    String apiUrl

    Pagure() {
        this(System.getenv('FEDORA_CI_PAGURE_DIST_GIT_API_URL') ?: 'https://src.fedoraproject.org/api/0')
    }

    /*
     * Constructor.
     *
     * @param url Pagure API URL
     */
    Pagure(String apiUrl) {
        this.apiUrl = apiUrl
    }

    def getPullRequestInfo(String pullRequestId) {
        def uid = splitPullRequestId(pullRequestId)[0]

        def url = this.apiUrl + '/pull-requests/' + uid

        def response = new URL(url).text
        def contentJson = new JsonSlurperClassic().parseText(response)
        return contentJson
    }

    /*
     * Split given Pull Request Id into individual components — uid, commit id, comment id
     *
     * @param pullRequestId Pull Request Id
     * @return an array with 3 elements: uid, commit id, comment id
     */
    def splitPullRequestId(String pullRequestId) {
        def uidSplit = pullRequestId.split('@')
        def uid = uidSplit[0]
        def commitAndCommentSplit = uidSplit[1].split('#')
        def commit = commitAndCommentSplit[0]
        def comment = commitAndCommentSplit[1]

        return [uid, commit, comment]
    }
}

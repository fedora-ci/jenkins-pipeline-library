package org.fedoraproject.jenkins.pagure

import groovy.json.JsonSlurperClassic


/*
 * A collection of helper methods for Pagure.
 */
class Pagure implements Serializable {

    String url
    String apiUrl

    Pagure() {
        this(System.getenv('FEDORA_CI_PAGURE_DIST_GIT_URL') ?: 'https://src.fedoraproject.org')
    }

    /*
     * Constructor.
     *
     * @param url Pagure URL
     */
    Pagure(String url) {
        this.url = url
        this.apiUrl = url + '/api/0'
    }

    def getPullRequestInfo(String pullRequestId) {
        def uid = splitPullRequestId(pullRequestId)['uid']

        def url = this.apiUrl + '/pull-requests/' + uid

        def response = new URL(url).text
        def contentJson = new JsonSlurperClassic().parseText(response)
        return contentJson
    }

    /*
     * Split given Pull Request Id into individual components — uid, commit id, comment id
     *
     * @param pullRequestId Pull Request Id
     * @return a map with results
     */
    def splitPullRequestId(String pullRequestId) {
        def uidSplit = pullRequestId.split('@')
        def uid = uidSplit[0]
        def commitAndCommentSplit = uidSplit[1].split('#')
        def commitId = commitAndCommentSplit[0]
        def commentId = commitAndCommentSplit[1].toInteger()

        return [uid: uid, commitId: commitId, commentId: commentId]
    }
}

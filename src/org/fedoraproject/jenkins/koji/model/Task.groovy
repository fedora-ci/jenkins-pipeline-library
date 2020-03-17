package org.fedoraproject.jenkins.koji.model


class Task {

    def request

    Integer id
    String method

    Integer priority
    Integer hostId
    Integer ownerId
}

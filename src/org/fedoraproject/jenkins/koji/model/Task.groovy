package org.fedoraproject.jenkins.koji.model


class Task implements Serializable {

    def request

    Integer id
    String method

    Integer priority
    Integer hostId
    Integer ownerId
}

package org.fedoraproject.jenkins.koji.model


import org.fedoraproject.jenkins.koji.model.BuildSource

/*
 * Representation of a build in Koji
 */
class Build {

    String packageName
    String extra
    String creationTime
    String completionTime
    int packageId

    int id
    int buildId
    String epoch // ?

    BuildSource source
    int state

    String version
    double completionTs
    int ownerId
    String ownerName
    String nvr
    String startTime
    int creationEventId
    String startTs
    int volumeId
    double creationTs
    String name
    int taskId
    String volumeName
    String release

    Object cgName
    Object cgId
}


enum BuildState {

    BUILDING(0),
    COMPLETE(1),
    FAILED(3),
    CANCELLED(4)
}

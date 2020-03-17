package org.fedoraproject.jenkins.koji;

import org.fedoraproject.jenkins.koji.model.BuildSource


class BuildInfo {

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

    List<String> tags
}

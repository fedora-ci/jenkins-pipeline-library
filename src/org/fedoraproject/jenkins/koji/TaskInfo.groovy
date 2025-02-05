package org.fedoraproject.jenkins.koji;

import org.fedoraproject.jenkins.koji.model.BuildSource


class TaskInfo implements Serializable {

    Integer id
    String method
    Integer priority
    Integer hostId

    String name
    String version
    String release
    String nvr

    String extra
    Integer packageId
    Integer buildId
    String epoch  // ?
    BuildSource source
    Integer state
    Integer ownerId
    String ownerName
    String packageName
    String volumeName

    String target
    Boolean scratch
    Boolean draft
}

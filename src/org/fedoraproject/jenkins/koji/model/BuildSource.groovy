package org.fedoraproject.jenkins.koji.model


class BuildSource implements Serializable {

    String url = ''
    String commitId = ''
    String raw

    private BuildSource() {
        // private
    }

    String toString() {
        return this.raw
    }

    Boolean isRepoUrlWithRef() {
        if ((this.commitId != null && !this.commitId.isEmpty()) && (this.url != null && !this.url.isEmpty())) {
            return true
        }
        return false
    }

    static BuildSource fromString(String sourceStr) {
        BuildSource source = new BuildSource()
        source.raw = sourceStr
        if (sourceStr && sourceStr.contains('#')) {
            source.url = sourceStr.split('#')[0]
            source.commitId = sourceStr.split('#')[1]
        }
        return source
    }
}

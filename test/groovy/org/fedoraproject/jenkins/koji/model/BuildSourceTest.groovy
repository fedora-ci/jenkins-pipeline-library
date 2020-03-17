package org.fedoraproject.jenkins.koji

import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.Test
import static org.junit.Assert.*

import org.fedoraproject.jenkins.koji.model.BuildSource


class BuildSourceTest extends BasePipelineTest {

    @Test
    void buildSourceTest() {
        def source = 'cli-build/1573203680.9164877.zdiCObgR/libsemanage-2.9-2.el8.pr.b89bce79d10f482da1524ea02d3e5dff.c.7591.src.rpm'
        def bs = BuildSource.fromString(source)

        assertFalse bs.isRepoUrlWithRef()
        assertTrue bs.commitId.isEmpty()
        assertTrue bs.url.isEmpty()
        assertEquals bs.raw, source
        assertEquals bs.toString(), source
    }

    @Test
    void buildRepoUrlSourceTest() {
        def source = 'git+https://src.fedoraproject.org/rpms/python-requests.git#f9d62a3fa1306bd3744f29b4a4136adfc3de6603'
        def bs = BuildSource.fromString(source)

        assertTrue bs.isRepoUrlWithRef()
        assertEquals bs.commitId, 'f9d62a3fa1306bd3744f29b4a4136adfc3de6603'
        assertEquals bs.url, 'git+https://src.fedoraproject.org/rpms/python-requests.git'
        assertEquals bs.raw, source
        assertEquals bs.toString(), source
    }
}

package org.fedoraproject.jenkins

import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.Test
import static org.junit.Assert.*
import static groovy.test.GroovyAssert.shouldFail

import org.fedoraproject.jenkins.koji.model.BuildSource
import org.fedoraproject.jenkins.Utils


class UtilsTest extends BasePipelineTest {

    @Test
    void getNVRfromSourceTest() {
        def source = 'cli-build/1573203680.9164877.zdiCObgR/libsemanage-2.9-2.el8.pr.b89bce79d10f482da1524ea02d3e5dff.c.7591.src.rpm'
        def bs = BuildSource.fromString(source)

        def nvr = Utils.getNVRfromSource(bs)
        assertTrue nvr == 'libsemanage-2.9-2.el8.pr.b89bce79d10f482da1524ea02d3e5dff.c.7591'
    }

    @Test
    void splitRpmFilenameTest() {
        def filename = 'libsemanage-2.9-2.el8.pr.b89bce79d10f482da1524ea02d3e5dff.c.7591.src.rpm'

        def (name, version, release, arch) = Utils.splitRpmFilename(filename)
        assertEquals name, 'libsemanage'
        assertEquals version, '2.9'
        assertEquals release, '2.el8.pr.b89bce79d10f482da1524ea02d3e5dff.c.7591'
        assertEquals arch, 'src'
    }

    @Test
    void getRPMfilenameFromSourceTest() {
        def source = 'cli-build/1573203680.9164877.zdiCObgR/libsemanage-2.9-2.el8.pr.b89bce79d10f482da1524ea02d3e5dff.c.7591.src.rpm'
        def bs = BuildSource.fromString(source)

        def filename = Utils.getRPMfilenameFromSource(bs)
        assertTrue filename == 'libsemanage-2.9-2.el8.pr.b89bce79d10f482da1524ea02d3e5dff.c.7591.src.rpm'
    }

    @Test
    void getRPMfilenameFromSourceRepoUrlTest() {
        def source = 'git://pkgs.devel.redhat.com/rpms/dwz#5f6f3a9ec91fdbeff45aedee96671441b3ef64fa'
        def bs = BuildSource.fromString(source)

        shouldFail(IllegalArgumentException) {
            def filename = Utils.getRPMfilenameFromSource(bs)
        }
    }
}

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
        assertEquals 'libsemanage-2.9-2.el8.pr.b89bce79d10f482da1524ea02d3e5dff.c.7591', nvr
    }

    @Test
    void splitRpmFilenameTest() {
        def filename = 'libsemanage-2.9-2.el8.pr.b89bce79d10f482da1524ea02d3e5dff.c.7591.src.rpm'

        def (name, version, release, arch) = Utils.splitRpmFilename(filename)
        assertEquals 'libsemanage', name
        assertEquals '2.9', version
        assertEquals '2.el8.pr.b89bce79d10f482da1524ea02d3e5dff.c.7591', release
        assertEquals 'src', arch
    }

    @Test
    void getRPMfilenameFromSourceTest() {
        def source = 'cli-build/1573203680.9164877.zdiCObgR/libsemanage-2.9-2.el8.pr.b89bce79d10f482da1524ea02d3e5dff.c.7591.src.rpm'
        def bs = BuildSource.fromString(source)

        def filename = Utils.getRPMfilenameFromSource(bs)
        assertEquals 'libsemanage-2.9-2.el8.pr.b89bce79d10f482da1524ea02d3e5dff.c.7591.src.rpm', filename
    }

    @Test
    void getRPMfilenameFromSourceRepoUrlTest() {
        def source = 'git://pkgs.devel.redhat.com/rpms/dwz#5f6f3a9ec91fdbeff45aedee96671441b3ef64fa'
        def bs = BuildSource.fromString(source)

        shouldFail(IllegalArgumentException) {
            def filename = Utils.getRPMfilenameFromSource(bs)
        }
    }

    @Test
    void isCompositeArtifactTest() {
        def compositeArtifactId = '(koji-build:46436038,koji-build:46436038)->fedora-update:FEDORA-2020-008cb761a2'
        def notCompositeArtifactId = 'koji-build:46436038'

        assertTrue Utils.isCompositeArtifact(compositeArtifactId)
        assertFalse Utils.isCompositeArtifact(notCompositeArtifactId)
    }

    @Test
    void getTargetArtifactIdTest() {
        def compositeArtifactId = '(koji-build:46436038,koji-build:46436038)->fedora-update:FEDORA-2020-008cb761a2'

        assertEquals 'fedora-update:FEDORA-2020-008cb761a2', Utils.getTargetArtifactId(compositeArtifactId)
    }

    @Test
    void mapToJsonStringTest() {
        def payload = [name: 'Douglas', surname: 'Quaid', info: [:]]

        assertEquals '{"name":"Douglas","surname":"Quaid","info":{}}', Utils.mapToJsonString(payload, false)
    }

    @Test
    void jsonStringToMapTest() {
        def payload = '{"name":"Douglas","surname":"Quaid","info":{}}'

        def result = Utils.jsonStringToMap(payload)
        assertEquals 'Quaid', result['surname']
    }
}

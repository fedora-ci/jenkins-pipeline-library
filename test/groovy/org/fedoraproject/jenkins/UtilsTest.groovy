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

    @Test
    void string2sha256Test() {
        assertEquals 'f5497b5bef7e1dd5985b5641e54239821b88cb2150a44774d8f0f8bdd251983a', Utils.string2sha256('abc abc')
    }

    @Test
    void xunitResults2mapTest() {
        def xunit = '''
<testsuites overall-result=\"passed\">
    <testsuite result=\"passed\" tests=\"1\" name=\"ts1\">
        <testcase name=\"/check-sat-x86_64\" result=\"passed\">
            <testing-environment name=\"requested\">
                <property name=\"arch\" value=\"x86_64\"/>
            </testing-environment>
        </testcase>
    </testsuite>
    <testsuite result=\"failed\" tests=\"1\" name=\"ts2\">
        <testcase name=\"/check-sat-x86_64\" result=\"failed\">
            <testing-environment name=\"requested\">
                <property name=\"arch\" value=\"x86_64\"/>
            </testing-environment>
        </testcase>
    </testsuite>
</testsuites>
        '''
        def result = Utils.xunitResults2map(xunit)
        assertEquals 2, result.size()
        println("${result}")
        assertEquals 'passed', result['ts1'].toString()
        assertEquals 'needs_inspection', result['ts2'].toString()
    }

    @Test
    void generatePipelineIdFromArtifactIdAndTestcaseTest() {
        def result = Utils.generatePipelineIdFromArtifactIdAndTestcase('koji-build:123456', 'fedora-ci.koji-build.rpminspect.static-analysis')
        assertEquals 'f17d6cc02a77a34db48fd6bf39f2fb2a86a823b4849058292abc5300eb9b0c5b', result
    }
}

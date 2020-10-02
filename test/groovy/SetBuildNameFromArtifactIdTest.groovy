import org.junit.*
import com.lesfurets.jenkins.unit.*
import static groovy.test.GroovyAssert.*


class SetBuildNameFromArtifactIdTest extends BasePipelineTest {

    @Before
    void init() {
        super.setUp()
    }

    def setBuildNameFromArtifactId(params) {
        super.setUp()
        def step = helper.loadScript('vars/setBuildNameFromArtifactId.groovy', binding)
        return step(params)
    }

    @Test
    void testSetBuildNameFromArtifactId() {
        def result = setBuildNameFromArtifactId(artifactId: 'koji-build:46436038')
        assertEquals '[koji-build] wget-1.20.3-6.fc33', binding.getVariable('currentBuild').displayName.toString()
    }

    @Test
    void testSetBuildNameFromArtifactIdBadArtifactType() {
        def result = setBuildNameFromArtifactId(artifactId: 'unknown-build:46436038')
        assertEquals "UNKNOWN ARTIFACT TYPE: 'unknown-build'", binding.getVariable('currentBuild').displayName.toString()
    }

    @Test
    void testSetBuildNameFromArtifactIdError() {
        def result = setBuildNameFromArtifactId(artifactId: 'koji-build:99999999')
        assertEquals "INVALID ARTIFACT ID: 'koji-build:99999999'", binding.getVariable('currentBuild').displayName.toString()
    }

    @Test
    void testCompositeArtifact() {
        def result = setBuildNameFromArtifactId(artifactId: '(koji-build:46436038,koji-build:46436038)->fedora-update:FEDORA-2020-008cb761a2')
        assertEquals '[fedora-update] FEDORA-2020-008cb761a2', binding.getVariable('currentBuild').displayName.toString()
    }
}

import org.junit.*
import com.lesfurets.jenkins.unit.*
import static groovy.test.GroovyAssert.*


class GetIdFromArtifactIdTest extends BasePipelineTest {

    @Before
    void init() {
        super.setUp()
    }

    def getIdFromArtifactId(params) {
        super.setUp()
        def step = helper.loadScript('vars/getIdFromArtifactId.groovy', binding)
        return step(params)
    }

    @Test
    void testGetIdFromArtifactId() {
        def result = getIdFromArtifactId(artifactId: 'koji-build:46436038,koji-build:9999999')
        assertEquals '46436038,9999999', result
    }

    @Test
    void testGetIdFromAdditionalArtifactIds() {
        def result = getIdFromArtifactId(artifactId: 'koji-build:46436038', additionalArtifactIds: 'koji-build:123456789,koji-build:00000000')
        assertEquals '46436038,123456789,00000000', result
    }

    @Test
    void testCompositeArtifact() {
        def result = getIdFromArtifactId(artifactId: '(koji-build:46436038,koji-build:46436038)->fedora-update:FEDORA-2020-008cb761a2')
        assertEquals '46436038,46436038', result
    }
}

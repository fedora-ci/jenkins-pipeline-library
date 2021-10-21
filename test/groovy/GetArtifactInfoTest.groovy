import org.junit.*
import com.lesfurets.jenkins.unit.*
import static groovy.test.GroovyAssert.*


class GetArtifactInfoTest extends BasePipelineTest {

    @Before
    void init() {
        super.setUp()
    }

    def getArtifactInfo(params) {
        super.setUp()
        binding.setVariable('env', ['KOJI_API_URL': 'https://koji.fedoraproject.org/kojihub'])
        def step = helper.loadScript('vars/getArtifactInfo.groovy', binding)
        return step(params)
    }

    @Test
    void testGetArtifactInfo() {
        def result = getArtifactInfo(artifactId: 'koji-build:46436038')
        assertEquals 1, result.size()
        assertEquals 'wget-1.20.3-6.fc33', result['koji-build:46436038'].nvr
    }
}

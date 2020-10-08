import org.junit.*
import com.lesfurets.jenkins.unit.*
import static groovy.test.GroovyAssert.*


class GetRepoUrlAndRefFromTaskIdTest extends BasePipelineTest {

    @Before
    void init() {
        super.setUp()
    }

    def getRepoUrlAndRefFromTaskId(params) {
        super.setUp()
        binding.setVariable('env', ['FEDORA_CI_PAGURE_DIST_GIT_URL': 'https://src.fedoraproject.org'])
        def step = helper.loadScript('vars/getRepoUrlAndRefFromTaskId.groovy', binding)
        return step(params)
    }

    @Test
    void testGetIdFromArtifactIdSRPMFromCli() {
        def result = getRepoUrlAndRefFromTaskId(52957188)
        assertEquals 'https://src.fedoraproject.org/rpms/python-pygments-pytest', result[0].toString()
        assertEquals '7579642717a0a4f83488560d50e2b5b5d76eaced', result[1].toString()
    }

    @Test
    void testGetIdFromArtifactId() {
        def result = getRepoUrlAndRefFromTaskId(52973984)
        assertEquals 'https://src.fedoraproject.org/rpms/python-pycdlib', result[0].toString()
        assertEquals '4e2a406598390225cc346fb13111d930c7a0461d', result[1].toString()
    }
}

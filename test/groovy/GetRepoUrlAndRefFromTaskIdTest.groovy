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
        binding.setVariable('env', [
            'FEDORA_CI_PAGURE_DIST_GIT_URL': 'https://src.fedoraproject.org',
            'KOJI_API_URL': 'https://koji.fedoraproject.org/kojihub'
        ])
        def step = helper.loadScript('vars/getRepoUrlAndRefFromTaskId.groovy', binding)
        return step(params)
    }

    @Test
    void testGetIdFromArtifactIdSRPMFromCli() {
        def result = getRepoUrlAndRefFromTaskId(53007551)
        assertEquals 'https://src.fedoraproject.org/forks/msrb/rpms/tmt', result['url'].toString()
        assertEquals '161692bc1069b5a920cd43f4bf209dff76a49c60', result['ref'].toString()
    }

    @Test
    void testGetIdFromArtifactId() {
        def result = getRepoUrlAndRefFromTaskId(52973984)
        assertEquals 'https://src.fedoraproject.org/rpms/python-pycdlib', result['url'].toString()
        assertEquals '4e2a406598390225cc346fb13111d930c7a0461d', result['ref'].toString()
    }
}

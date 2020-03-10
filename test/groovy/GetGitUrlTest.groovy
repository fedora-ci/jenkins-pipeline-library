import org.junit.*
import com.lesfurets.jenkins.unit.*
import static groovy.test.GroovyAssert.*


class GetGitUrlTest extends BasePipelineTest {

    def getGitUrlStep

    @Before
    void setUp() {
        super.setUp()
        binding.setVariable('env', ['CHANGE_ID': '1', 'CHANGE_FORK': 'msrb', 'GIT_URL': 'https://github.com/fedora-ci/rpmdeplint-image.git'])
        getGitUrlStep = helper.loadScript('vars/getGitUrl.groovy', binding)
    }

    @Test
    void testIsProduction() {
        def result = getGitUrlStep()
        assertEquals 'https://github.com/msrb/rpmdeplint-image.git', result
    }

}

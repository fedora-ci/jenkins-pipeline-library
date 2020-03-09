import org.junit.*
import com.lesfurets.jenkins.unit.*
import static groovy.test.GroovyAssert.*


class IsPullRequestTest extends BasePipelineTest {

    def isPullRequestStep

    @Before
    void setUp() {
        super.setUp()
        binding.setVariable('env', ['CHANGE_ID': 'yes-there-are-some-changes'])
        isPullRequestStep = helper.loadScript('vars/isPullRequest.groovy', binding)
    }

    @Test
    void testIsPullRequest() {
        def result = isPullRequestStep()
        assertEquals true, result
    }

}

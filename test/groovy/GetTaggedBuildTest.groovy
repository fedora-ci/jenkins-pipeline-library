import org.junit.*
import com.lesfurets.jenkins.unit.*
import static groovy.test.GroovyAssert.*


class GetTaggedBuildTest extends BasePipelineTest {

    @Before
    void init() {
        super.setUp()
    }

    def getTaggedBuild(params) {
        super.setUp()
        binding.setVariable('env', ['KOJI_API_URL': 'https://koji.fedoraproject.org/kojihub'])
        def step = helper.loadScript('vars/getTaggedBuild.groovy', binding)
        return step(params)
    }

    @Test
    void testGetTaggedBuild() {
        def result = getTaggedBuild(tagName: 'f31', packageName: 'wget')
        assertTrue 'nvr' in result
        assertEquals 'wget-1.20.3-2.fc31', result.nvr
    }
}

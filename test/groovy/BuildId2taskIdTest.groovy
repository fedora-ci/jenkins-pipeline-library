import org.junit.*
import com.lesfurets.jenkins.unit.*
import static groovy.test.GroovyAssert.*


class BuildId2taskIdTest extends BasePipelineTest {

    @Before
    void init() {
        super.setUp()
    }

    def buildId2taskId(taskId) {
        super.setUp()
        def step = helper.loadScript('vars/buildId2taskId.groovy', binding)
        return step(taskId)
    }

    @Test
    void testBuildId2taskId() {
        // https://koji.fedoraproject.org/koji/buildinfo?buildID=1536155
        def result = buildId2taskId(1536155)
        assertEquals 46436038, result
    }

    @Test
    void testBuildId2taskIdNonExistent() {
        shouldFail(IllegalArgumentException) {
            // the build id doesn't exist
            def result = buildId2taskId(9999999)
        }
    }
}

import org.junit.*
import com.lesfurets.jenkins.unit.*
import static groovy.test.GroovyAssert.*


class IsScratchBuildTest extends BasePipelineTest {

    def isScratchBuild

    @Before
    void setUp() {
        super.setUp()
        isScratchBuild = helper.loadScript('vars/isScratchBuild.groovy', binding)
    }

    @Test
    void test() {
        def result = isScratchBuild(artifactId: 'koji-build:62674132')  // not a scratch build
        assertFalse result
    }
}

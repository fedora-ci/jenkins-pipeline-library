import org.junit.*
import com.lesfurets.jenkins.unit.*
import static groovy.test.GroovyAssert.*


class GetTargetArtifactTypeTest extends BasePipelineTest {

    def getTargetArtifactType

    @Before
    void setUp() {
        super.setUp()
        getTargetArtifactType = helper.loadScript('vars/getTargetArtifactType.groovy', binding)
    }

    @Test
    void test() {
        def result = getTargetArtifactType('koji-build:123456')
        assertEquals 'koji-build', result
        result = getTargetArtifactType('()->fedora-dist-git:0b1996feb53a4040a8edf11890eb3599@fb577daf3708dc030fef1ae08aeaa9b848b8ad3d#67246')
        assertEquals 'fedora-dist-git', result
    }
}

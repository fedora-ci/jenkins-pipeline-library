import org.junit.*
import com.lesfurets.jenkins.unit.*
import static groovy.test.GroovyAssert.*


class GetArtifactTypeTest extends BasePipelineTest {

    def getArtifactType

    @Before
    void setUp() {
        super.setUp()
        getArtifactType = helper.loadScript('vars/getArtifactType.groovy', binding)
    }

    @Test
    void test() {
        def result = getArtifactType('koji-build:123456')
        assertEquals 'koji-build', result
        result = getArtifactType('(koji-build:123456,koji-build:654321)->fedora-dist-git:0b1996feb53a4040a8edf11890eb3599@fb577daf3708dc030fef1ae08aeaa9b848b8ad3d#67246')
        assertEquals 'koji-build', result
        result = getArtifactType('()->fedora-dist-git:0b1996feb53a4040a8edf11890eb3599@fb577daf3708dc030fef1ae08aeaa9b848b8ad3d#67246')
        assertEquals 'fedora-dist-git', result
    }
}

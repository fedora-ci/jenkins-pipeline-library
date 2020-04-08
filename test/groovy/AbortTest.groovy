import org.junit.*
import com.lesfurets.jenkins.unit.*
import static groovy.test.GroovyAssert.*


class AbortTest extends BasePipelineTest {

    def abortStep

    @Before
    void setUp() {
        super.setUp()
        binding.setVariable('env', [:])
        abortStep = helper.loadScript('vars/abort.groovy', binding)
    }

    @Test
    void testAbortStep() {
        abortStep('Abort test')
    }
}

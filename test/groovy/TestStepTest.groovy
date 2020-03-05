import org.junit.*
import com.lesfurets.jenkins.unit.*
import static groovy.test.GroovyAssert.*


class TestStepTest extends BasePipelineTest {

    def testStep

    @Before
    void setUp() {
        super.setUp()
        testStep = loadScript('vars/testStep.groovy')
    }

    @Test
    void testCall() {
        def result = testStep()
        assertEquals "result: ", "Hello World!", result
    }

    @Test
    void testCallWithName() {
        def result = testStep(name: 'OSCI')
        assertEquals "result: ", "Hello OSCI!", result
    }
}

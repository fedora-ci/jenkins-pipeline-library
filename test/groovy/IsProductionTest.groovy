import org.junit.*
import com.lesfurets.jenkins.unit.*
import static groovy.test.GroovyAssert.*


class IsProductionTest extends BasePipelineTest {

    def isProductionStep

    @Before
    void setUp() {
        super.setUp()
        binding.setVariable('env', [:])
        isProductionStep = helper.loadScript('vars/isProduction.groovy', binding)
    }

    @Test
    void testIsProduction() {
        def result = isProductionStep()
        assertEquals true, result
    }

}

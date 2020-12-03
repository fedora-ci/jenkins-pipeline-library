import org.junit.*
import com.lesfurets.jenkins.unit.*
import static groovy.test.GroovyAssert.*


class GzipTest extends BasePipelineTest {

    def gzipStep

    @Before
    void setUp() {
        super.setUp()
        binding.setVariable('env', [:])
        gzipStep = helper.loadScript('vars/gzip.groovy', binding)
    }

    @Test
    void testGzip() {
        def result = gzipStep('hello compressed world')
        assertTrue result.startsWith('H4sIAAA')  // base64-encoded gzipped string
    }

    @Test
    void testGzipNull() {
        def result = gzipStep(null)
        assertEquals null, result
    }

    @Test
    void testGzipEmptyString() {
        def result = gzipStep('')
        assertEquals '', result
    }
}

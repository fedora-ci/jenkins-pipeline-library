package org.fedoraproject.jenkins.koji

import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.Test
import static org.junit.Assert.*
import static groovy.test.GroovyAssert.*

import org.fedoraproject.jenkins.koji.Koji


class KojiTest extends BasePipelineTest {

    @Test
    void smokeTest() {
        Koji koji = new Koji()

        def apiVersion = koji.getAPIVersion()

        assertEquals apiVersion, 1
    }

    @Test
    void getBuildTargetsTest() {
        Koji koji = new Koji()
        def buildTarget = koji.getBuildTargets('f36')
        assertNotNull buildTarget
        assertTrue buildTarget.size == 1
        assertTrue buildTarget[0]['name'] == 'f36'
    }

    @Test
    void getUsernameTest() {
        Koji koji = new Koji()
        def username = koji.getUsername(2364)
        assertNotNull username
        assertEquals username, 'msrb'
    }

    @Test
    void getTaskInfoTest() {
        Koji koji = new Koji()

        // https://koji.fedoraproject.org/koji/taskinfo?taskID=41762736
        def taskInfo = koji.getTaskInfo(41762736)

        assertNotNull taskInfo
        assertFalse taskInfo.scratch
        assertEquals 'python-requests', taskInfo.name
        assertEquals 'python-requests', taskInfo.packageName
        assertEquals '2.23.0', taskInfo.version
        assertEquals '1.fc33', taskInfo.release
        assertEquals 'python-requests-2.23.0-1.fc33', taskInfo.nvr
        assertEquals 'bowlofeggs', taskInfo.ownerName
        assertEquals 'git+https://src.fedoraproject.org/rpms/python-requests.git#f9d62a3fa1306bd3744f29b4a4136adfc3de6603', taskInfo.source.raw
    }

    @Test
    void getNonExistentTaskInfoTest() {
        Koji koji = new Koji()

        // the taskId doesn't exist
        shouldFail(IllegalArgumentException) {
            koji.getTaskInfo(99999999)
        }
    }
}

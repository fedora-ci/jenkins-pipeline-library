package org.fedoraproject.jenkins.koji

@Grab(group='fr.turri', module='aXMLRPC', version='1.12.0')

import java.net.URL
import java.util.Map

import org.fedoraproject.jenkins.koji.model.Build
import org.fedoraproject.jenkins.koji.model.BuildSource
import org.fedoraproject.jenkins.koji.model.Task
import org.fedoraproject.jenkins.Utils

import de.timroes.axmlrpc.XMLRPCClient


/*
 * A collection of helper methods around Koji XML-RPC.
 */
class Koji implements Serializable {

    URL url

    private XMLRPCClient client

    /*
     * Constructor.
     *
     * The instance will take Koji API URL from BREW_API_URL environment variable.
     * If the variable doesn't exist, the default value (URL of a production Koji)
     * will be used.
     */
    Koji() {
        this(System.getenv('KOJI_API_URL') ?: 'https://koji.fedoraproject.org/kojihub')
    }

    /*
     * Constructor.
     *
     * @param url Koji API URL
     */
    Koji(String url) {
        this.url = url.toURL()

        this.client = new XMLRPCClient(this.url, XMLRPCClient.FLAGS_NIL);
    }

    /*
     * Returns Koji API version.
     *
     * @return Koji APi version
     */
    Integer getAPIVersion() {
        return (Integer) this.call('getAPIVersion')
    }

    /*
     * Call Koji.
     *
     * @return returns whatever Koji returns
     */
    private def call(String method, Object... params) {

        this.retry {
            return this.client.call(method , params)
        }
    }

    /*
     * Returns information about a build.
     *
     * @param buildId build ID
     * @return object holding information about the build
     */
    BuildInfo getBuildInfo(Integer buildId) {

        Map<String, Object> result = (Map<String, Object>) this.call('getBuild' , buildId) ?: [:]

        if (!result.get('id')) {
            throw new IllegalArgumentException("No such build id:  ${buildId}")
        }

        BuildInfo build = new BuildInfo()

        build.id = result.get('id')
        build.ownerName = result.get('owner_name')
        build.nvr = result.get('nvr')
        build.name = result.get('name')
        build.packageName = result.get('package_name')
        build.version = result.get('version')
        build.release = result.get('release')
        build.taskId = result.get('task_id')

        // TODO: I think we need "source" from extras here (?)
        build.source = BuildSource.fromString(result.get('source'))

        List<String> tags = this.listTags(buildId)

        build.tags = tags

        return build
    }

    /*
     * Returns information about a task.
     *
     * @param taskId task ID
     * @return object holding information about the task
     */
    TaskInfo getTaskInfo(Integer taskId) {

        Map<String, Object> result = (Map<String, Object>) this.call('getTaskInfo', taskId, ['request': 1, '__starstar': 1]) ?: [:]

        if (!result.get('id')) {
            throw new IllegalArgumentException("No such task id:  ${taskId}")
        }

        Build build = this.listBuilds(taskId)[0]

        TaskInfo taskInfo = new TaskInfo()

        taskInfo.id = result.get('id')
        taskInfo.method = result.get('method')
        taskInfo.priority = result.get('priority')
        taskInfo.hostId = result.get('host_id')
        taskInfo.ownerId = result.get('owner')

        taskInfo.target = result.get('request')[1]
        taskInfo.scratch = result.get('request')[2].get('scratch', false)

        if (build != null) {
            // TODO: find a better way how to do this "delegation" in Groovy (inheritance:))
            taskInfo.name = build.name
            taskInfo.version = build.version
            taskInfo.release = build.release
            taskInfo.nvr = build.nvr

            taskInfo.extra = build.extra
            taskInfo.packageId = build.packageId
            taskInfo.buildId = build.id
            taskInfo.epoch = build.epoch
            taskInfo.source = build.source
            taskInfo.state = build.state
            taskInfo.ownerId = build.ownerId
            taskInfo.ownerName = build.ownerName
            taskInfo.packageName = build.packageName
            taskInfo.volumeName = build.volumeName
        } else {
            // hacks for scratch builds... :(
            taskInfo.source = BuildSource.fromString(result.get('request')[0])
            taskInfo.ownerName = this.getUsername(taskInfo.ownerId)

            def source = taskInfo.source

            // If the source is repo URL, we cannot extract the filename from it;
            // therefore we need to get a list of sub-tasks and look for the filename there
            if (taskInfo.source.isRepoUrlWithRef()) {
                def descendents = this.getTaskDescendents(taskId)
                for (Task task: descendents) {
                    if ('buildArch'.equals(task.method)) {
                        source = BuildSource.fromString(task.request[0])
                    }
                }
            }

            def rpmFilename = Utils.getRPMfilenameFromSource(source)
            def (name, version, release, arch) = Utils.splitRpmFilename(rpmFilename)
            def nvr = Utils.getNVRfromSource(source)

            taskInfo.name = name
            taskInfo.packageName = name
            taskInfo.version = version
            taskInfo.release = release
            taskInfo.nvr = nvr
        }

        return taskInfo
    }

    /*
     * Returns a list of Builds associated with given task.
     *
     * @param taskId task ID
     * @return a list of Builds
     */
    private List<Build> listBuilds(Integer taskId) {

        Object[] results = (Object[]) this.call('listBuilds', [ 'taskID': taskId, '__starstar': true ])

        List<Build> builds = new ArrayList<Build>()
        for (Map<String, Object> result: results) {
            Build build = new Build()
            build.id = result.get('build_id')
            build.ownerName = result.get('owner_name')
            build.nvr = result.get('nvr')
            build.name = result.get('name')
            build.version = result.get('version')
            build.release = result.get('release')
            build.taskId = result.get('task_id')
            build.packageName = result.get('package_name')

            build.source = BuildSource.fromString(result.get('source'))

            builds.add(build)
        }

        return builds
    }

    /*
     * Returns a list of tags for given build ID.
     *
     * @param buildId build ID
     * @return a list of tags (as strings)
     */
    private List<String> listTags(Integer buildId) {

        Object[] results = (Object[]) this.call('listTags', buildId)

        List<String> tags = new ArrayList<String>()
        for (Map<String, Object> result: results) {
            tags.add(result.get('name'))
        }

        return tags
    }

    /*
     * Returns information about all build targets known to Koji.
     *
     * If "name" parameter is provided, then only information
     * about this build target will be returned.
     *
     * @param name build target name
     * @return a list of maps holding information about build targets
     */
    List<Map<String, Object>> getBuildTargets(String name) {

        // TODO: name param is actually mandatory now, but it shouldn't be

        Object[] results = (Object[]) this.call('getBuildTargets', name)

        return results
    }

    /*
     * Returns username for given user ID.
     *
     * @param userId user ID
     * @return username
     */
    String getUsername(Integer userId) {

        Map<String, Object> result = (Map<String, Object>) this.call('getUser', userId)

        return result['name']
    }

    /*
     * Returns a list of descendent tasks.
     *
     * @param taskId task ID
     * @return a list of descendent tasks
     */
    List<Task> getTaskDescendents(Integer taskId) {

        Map<String, Object> results = (Map<String, Object>) this.call('getTaskDescendents', taskId, ['request': true])

        def tasks = results[taskId.toString()]

        List<Task> descendents = new ArrayList<Task>()
        for (Map<String, Object> result: tasks) {
            Task task = new Task()
            task.id = result['id']
            task.ownerId = result['owner']
            task.priority = result['priority']
            task.method = result['method']
            task.request = result['request']

            descendents.add(task)
        }

        return descendents
    }

    /*
     * Retry given closure, if it fails with exception.
     *
     * @param times how many times to retry on failures
     * @param errorHandler error handler which runs on failures
     * @param body code to retry
     * @return output of the body closure or RuntimeException if none of the tries succeeded
     */
    private def retry(int times = 5, Closure errorHandler = {e-> println(e.message)}, Closure body) {
        int retries = 0
        def exceptions = []
        while(retries++ < times) {
            try {
                return body.call()
            } catch(e) {
                exceptions << e
                errorHandler.call(e)
                sleep(1000)  // 1 second
            }
        }
        throw new RuntimeException("Failed after $times retries".toString(), exceptions[-1])
    }
}

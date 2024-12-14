#!/usr/bin/groovy

/**
 * loadComponentConfig() step.
 */
def call(Map params = [:]) {
    def componentName = params.get('componentName')

    // internal/undocumented
    def configBaseUrl = params.get('configBaseUrl') ?: env.COMPONENT_CONFIG_YAML_BASE_URL

    // TODO: Find a better way... hardcoding the namespace ("rpms"),
    // and the filename ("tests.yaml") will cause some headaches in the future...
    def configUrl = "${configBaseUrl}/rpms/${componentName}/tests.yaml"

    def response
    retry(30) {
        try {
            response = httpRequest(
                consoleLogResponseBody: false,
                contentType: 'TEXT_PLAIN',  // "application/yaml" not supported by the http plugin
                httpMode: 'GET',
                url: configUrl,
                validResponseCodes: '200,404',
                quiet: true
            )
        } catch(e) {
            sleep(time: 10, unit: "SECONDS")
            error("Failed to fetch component config: ${e.getClass().getCanonicalName()}: ${e.getMessage()}")
        }
        def config = [:]  // 404 = empty component config
        if (response.status == 200) {
            config = readYaml(text: response.content)
        }
        return config
    }
}

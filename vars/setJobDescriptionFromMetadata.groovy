#!/usr/bin/groovy

/**
 * setJobDescriptionFromMetadata() step.
 */
def call(Map params = [:]) {
    def pipelineMetadata = params.get('pipelineMetadata')

    def jobDescription
    def job = Jenkins.instance.getItemByFullName(env.JOB_NAME)

    def description = pipelineMetadata.get('pipelineDescription')
    def maintainer = pipelineMetadata.get('maintainer')
    def docs = pipelineMetadata.get('docs')

    def contact = ''
    // translate 'contact' map into a human readable string, e.g.:
    // [irc: '#fedora-ci', email: 'ci@lists.fedoraproject.org']
    // translates into "via irc on #fedora-ci or via email on ci@lists.fedoraproject.org"
    pipelineMetadata.get('contact').each { key, value ->
        if (contact) {
            contact += " or "
        }
        contact += "via ${key} on ${value}"
    }

    jobDescription = """
<p>
${description}
</p>
<p>
This pipeline is maintained by ${maintainer}. Feel free to reach out to us ${contact}.
</p>
<p>
You can find more information about this pipeline in the <a href="${docs}">official documentation</a>.
</p>
"""

    job.setDescription(jobDescription)
}

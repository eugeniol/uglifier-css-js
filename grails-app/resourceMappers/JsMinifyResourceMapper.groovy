

import groovy.time.TimeCategory
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.support.aware.GrailsApplicationAware
import org.grails.plugin.resource.AggregatedResourceMeta
import org.grails.plugin.resource.mapper.MapperPhase
import org.mozilla.javascript.EvaluatorException

class JsMinifyResourceMapper implements GrailsApplicationAware {
    // define our own logger, otherwise it gets registered with the name:
    //grails.app.resourceMappers.org.grails.plugin.resource.minified.js.uglify.UglifyJsResourceMapper
    private static final log = LogFactory.getLog(this)

    def phase = MapperPhase.COMPRESSION
    def operation = "uglify"
    static defaultExcludes = ['**/*.min.js']
    static defaultIncludes = ['**/*.js']

    GrailsApplication grailsApplication

    def map(resource, config) {
        if (config?.disable) {
            if (log.debugEnabled)
                log.debug "JS Minifier disabled in Config.groovy"
            return false
        }

        if (resource instanceof AggregatedResourceMeta && !config?.forceBundleMinification) {
            if (log.debugEnabled)
                log.debug "Skipping ${resource.id} because it is a bundle."
            return false
        }

        File original = resource.processedFile
        File target = new File(original.absolutePath.replaceAll(/(?i)\.js$/, ".min.js"))


        if (log.debugEnabled) {
            log.debug "JS Minifier"
            log.debug "--> "  + original
            log.debug "<-- "  + target
        }



        try {
            Date start = new Date()
            def output = new UglifyEngine().minify(original.text, [filename: original.name, noMunge: config?.noMunge ?: false])
            target.write(output)

            resource.processedFile = target
            resource.updateActualUrlFromProcessedFile()
            if (log.infoEnabled)
                log.info "Uglified javascript file [${original.name}] to [${target.name}] in ${TimeCategory.minus(new Date(), start)}. " +
                           "Reduced size by ${Math.round((original.text.size() - output.size()) * 100 / original.text.size())}%"
        } catch (Exception e) {
            log.error """
                Problems uglifying javascript ${original.name}
                $e
                """
            Throwable cause = e
            while (cause.cause) {
                cause = cause.cause
                if (cause instanceof EvaluatorException) {
                    log.error("Uglify error: $cause.message")
                }
            }
            e.printStackTrace()
        }

    }
}
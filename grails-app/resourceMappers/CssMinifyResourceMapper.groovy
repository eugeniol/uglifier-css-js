import groovy.time.TimeCategory
import org.grails.plugin.resource.mapper.MapperPhase
import org.mozilla.javascript.EvaluatorException

class CssMinifyResourceMapper {

    def phase = MapperPhase.COMPRESSION

    static defaultExcludes = ['**/*.min.css']
    static defaultIncludes = ['**/*.css']

    def map(resource, config) {

        if (config?.disable) {
            if (log.debugEnabled) log.debug "CSS Minifier disabled in Config.groovy"
            return false
        }

        File original = resource?.processedFile
        //File target = Util.getTargetFile(resource, Util.cssFilePattern)

        File target = new File(original.absolutePath.replaceAll(/\.css$/, ".min.css").toString())

        if (log.debugEnabled) {
            log.debug "CSS Minifier"
            log.debug "--> $original"
            log.debug "<-- $target"
        }

        if (!target) return false

        try {
            Date start = new Date()
            def output = new UglifyEngine().minifycss(original.text, [filename: original.name, noMunge: config?.noMunge ?: false])
            target.write(output)

            resource.processedFile = target
            resource.updateActualUrlFromProcessedFile()
            if (log.infoEnabled)
                log.info "Uglified css file [${original.name}] to [${target.name}] in ${TimeCategory.minus(new Date(), start)}. " +
                        "Reduced size by ${Math.round((original.text.size() - output.size()) * 100 / original.text.size())}%"
        } catch (Exception e) {
            log.error """
                Problems uglifying css ${original.name}
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

//        try {
//            String encoding = config?.charset ?: 'UTF-8'
//            if (log.debugEnabled) log.debug "Minifying CSS file [$inputFile] to [$targetFile]"
//            CssCompressor compressor = null
//            inputFile.withReader(encoding) {
//                compressor = new CssCompressor(it)
//            }
//            if (compressor) {
//                targetFile.withWriter(encoding) {
//                    compressor.compress(it, config?.lineBreak ?: -1)
//                }
//            }
//            resource.processedFile = targetFile
//            resource.updateActualUrlFromProcessedFile()
//        } catch (Exception e) {
//            log.error "Stopped minifying [${inputFile}]: ${e.message} Set log level to warn for more details."
//            return false
//        }
    }
}

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
            log.debug "--> "  + original
            log.debug "<-- "  + target
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


import java.util.regex.Matcher
import java.util.regex.Pattern
import org.apache.commons.logging.LogFactory

/**
 * Helper Methods for Resource Mapper
 *
 * @author Carsten Block
 * @version 1.0 , Date: 09.08.11
 */
class Util {

    private static final log = LogFactory.getLog(this)

    static filePattern = Pattern.compile(/(.css|.js)$/)
    static cssFilePattern = Pattern.compile(/(.css)$/)
    static jsFilePattern = Pattern.compile(/(.js)$/)

    /**
     * Returns a string that represents the original file name
     * with the file name extension prefixed with a ".min".
     * Examples:
     *    test.css -> test.min.css
     *    test.js -> test.min.js
     *
     * Note: Only files ending with css or js are considered
     *
     * @param file used to extract the file name as base for computing the modified file name
     * @param filePattern an optional file pattern as base for computing the minified file name. Default: {@code /(.css|.js)$/}
     * @return String of the form {@code originalFileName.min.css}
     */
    static String calcMinifiedFileName(File file, Pattern filePattern = this.filePattern) {
        String newName = null
        if (file?.name) {
            Matcher matcher = filePattern.matcher(file.name)
            if (matcher.find()) newName = matcher.replaceAll('.min$1')
        }
        return newName
    }

    static File getTargetFile(resource, Pattern filePattern = this.filePattern) {

        def pattern = filePattern ?: this.filePattern

        //skip resources where attribute "nominify=true" is set in resources config file
        if (resource.attributes?.nominify) {
            if (log.debugEnabled) log.debug "Skip minifying [${resource?.processedFile}] as 'nominify' option is set to true."
            return null
        }

        //We only support processing of files that end with .css or .js
        File inputFile = resource?.processedFile
        String targetFileName = calcMinifiedFileName(inputFile, pattern)
        if (!targetFileName) {
            log.error "Cannot minify $inputFile. Only files ending with .css or .js are processed"
            return null
        }

        //if a target file (i.e. a .min.js or a .min.css already exists we do nothing
        File targetFile = new File(inputFile.parentFile, targetFileName)
        if (targetFile.exists()) {
            if (log.debugEnabled) log.debug "Skip minifying [$inputFile] as minified version exists. Using that version instead."
            resource.processedFile = targetFile
            resource.updateActualUrlFromProcessedFile()
            return null
        }

        return targetFile
    }
}

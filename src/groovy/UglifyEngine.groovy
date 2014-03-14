

import org.springframework.core.io.ClassPathResource
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable

// from: http://lisperator.net/uglifyjs/
// uglify.js generation steps:
// 1) install node.js
// 2) command prompt: npm install uglify-js -g
// 3) command prompt: uglifyjs --self -c -m -o uglify-2.3.6.js

// Copied from CoffeeScript resource plugin -- use Mozilla Rhino to run uglify.js
class UglifyEngine {
    Scriptable globalScope

    UglifyEngine() {
        Context jsContext

        try {
            def uglifyJsSource = new ClassPathResource('/uglify-2.3.6.js', this.class.classLoader)

            if (!uglifyJsSource.exists())
                throw new MissingResourceException("Could not find uglify.js", "UglifyEngine", "uglify-2.3.6.js")

            jsContext = Context.enter()
            jsContext.optimizationLevel = -1

            this.globalScope = jsContext.initStandardObjects()
            jsContext.evaluateReader(this.globalScope,
                                     new InputStreamReader(uglifyJsSource.inputStream, 'UTF-8'),
                                     uglifyJsSource.filename,
                                     0,
                                     null)

            def uglifyCssSource = new ClassPathResource('/cssmin.js', this.class.classLoader)

            if (!uglifyCssSource.exists())
                throw new MissingResourceException("Could not find uglify.js", "UglifyEngine", "uglify-2.3.6.js")

            jsContext.evaluateReader(this.globalScope,
                    new InputStreamReader(uglifyCssSource.inputStream, 'UTF-8'),
                    uglifyCssSource.filename,
                    0,
                    null)
        } catch (Exception e) {
            throw new Exception("Could not initialize uglifyjs engine.", e)
        } finally {
            if (jsContext)
                Context.exit()
        }
    }

    String minifycss(String input, options) {
        Context jsContext
        try {
            jsContext = Context.enter()
            def scope = jsContext.newObject(this.globalScope)
            scope.setParentScope(this.globalScope)
            scope.put("codeToMinify", scope, input)
            scope.put("filename", scope, options?.filename ?: "unknown")

            // https://github.com/mishoo/UglifyJS2/issues/67
            // can't use this
            // var uglifyCommand = "UglifyJS.minify(codeToMinify, { fromString: true, mangle: true });"
            def uglifyCommand = """\
                cssmin(codeToMinify , { filename: filename} )
"""

            def result = jsContext.evaluateString(scope, uglifyCommand, "Uglify command", 0, null)

            return result
        } catch (Exception e) {
            throw new Exception("""
                Uglify minification of javascript failed..
                $e
            """)
        } finally {
            if (jsContext)
                Context.exit()
        }
    }
    String minify(String input, options) {
        Context jsContext
        try {
            jsContext = Context.enter()
            def scope = jsContext.newObject(this.globalScope)
            scope.setParentScope(this.globalScope)
            scope.put("codeToMinify", scope, input)
            scope.put("filename", scope, options?.filename ?: "unknown")

            // https://github.com/mishoo/UglifyJS2/issues/67
            // can't use this
            // var uglifyCommand = "UglifyJS.minify(codeToMinify, { fromString: true, mangle: true });"
            def uglifyCommand = """\
                (function() {
                    var parsedAst = UglifyJS.parse(codeToMinify, { filename: filename} )
                    parsedAst.figure_out_scope()

                    var compressedAst = parsedAst.transform(UglifyJS.Compressor())
"""
            if (!options?.noMunge)
                uglifyCommand += """\
                    compressedAst.figure_out_scope()
                    compressedAst.compute_char_frequency()
                    compressedAst.mangle_names()
"""

            uglifyCommand += """\
                    return compressedAst.print_to_string()
                }())
"""

            def result = jsContext.evaluateString(scope,
                    uglifyCommand,
                    "Uglify command", 0, null)
            return result
        } catch (Exception e) {
            throw new Exception("""
                Uglify minification of javascript failed..
                $e
            """)
        } finally {
            if (jsContext)
                Context.exit()
        }
    }
}

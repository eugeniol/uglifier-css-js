class UglifierCssJsGrailsPlugin {
    // the plugin version
    def version = "0.1-SNAPSHOT"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.7 > *"


    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def dependsOn = [resources:'1.1.6 > *']

    def loadAfter = ['resources']


    // TODO Fill in these fields
    def author = "Eugenio Lattanzio"
    def authorEmail = "eugenio63@gmail.com"
    def title = "Css/Js Minifier Resource plugin"
    def description = '''\\
Minify Css/Js assets using yui uglify implementation
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/uglifier-css-js"

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}

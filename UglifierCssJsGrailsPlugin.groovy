class UglifierCssJsGrailsPlugin {
    def version = "0.1"
    def grailsVersion = "1.3.7 > *"
    def loadAfter = ['resources']
    def author = "Eugenio Lattanzio"
    def authorEmail = "eugenio63@gmail.com"
    def title = "Css/Js Minifier Resource plugin"
    def description = 'Minify Css/Js assets using yui uglify implementation'
    def documentation = "http://grails.org/plugin/uglifier-css-js"
    def license = "APACHE"
    def developers = [
            [name: "Eugenio Lattanzio", email: "eugenio63@gmail.com"]

    ]
    def issueManagement = [system: 'github', url: 'https://github.com/eugeniol/uglifier-css-js/issues']
    def scm = [url: "https://github.com/eugeniol/uglifier-css-js"]

}

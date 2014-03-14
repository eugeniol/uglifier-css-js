uglifier-css-js
===============

Css/Js Uglifier for grails resources

This is a Grails plugin which provides minification of css and javascript resources using [Uglify2](https://github.com/mishoo/UglifyJS2) and [cssmin](https://github.com/jbleuzen/node-cssmin)


It is dependent on the standard [Grails Resources Plugin](http://grails.org/plugin/resources)
 

Since yui minify was deprecated, I developed this plugin that minifies the js and css using [yuglify](https://github.com/yui/yuglify). The plugin doesn't cause issues integrating with lesscss. 

Install
-------
Dependency

	compile ':uglifier-css-js:0.1'



This plugin ignores javascript files which have an extension of *.min.js and *.min.css


The default configuration is

	grails.resources.mappers.cssminify.includes = ['**/*.css']
	grails.resources.mappers.cssminify.excludes = ['**/*.min.css']
	grails.resources.mappers.jsminify.includes = ['**/*.js']
	grails.resources.mappers.jsminify.excludes = ['**/*.min.js']

By default, bundles are ignored since the individual files are minified before getting bundled. To enable bundling, set the following in Config.groovy:

	grails.resources.mappers.uglifyjs.forceBundleMinification = true

To disable javascript variable munging, set the following in Config.groovy:

	grails.resources.mappers.uglifyjs.noMunge = true


Loggin 

	debug "JsMinifyResourceMapper"
	debug "CssMinifyResourceMapper"


I've made use of this [uglify-js-minified-resources-grails](https://github.com/chriserickson/uglify-js-minified-resources-grails) and integrated with a css minifier.


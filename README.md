uglifier-css-js
===============

Css/Js Uglifier for grails resources
 
This is another minifier that uses yuglify and mozilla rihno. See https://github.com/yui/yuglify The plugin works ok with lesscss integration, since the previuos yui minifier had know issues on this. The plugin minifies the css and js resources

The default configuration is

	grails.resources.mappers.cssminify.includes = ['**/*.css']
	grails.resources.mappers.cssminify.excludes = ['**/*.min.css']
	grails.resources.mappers.jsminify.includes = ['**/*.js']
	grails.resources.mappers.jsminify.excludes = ['**/*.min.js']

Loggin 

	debug "JsMinifyResourceMapper"
	debug "CssMinifyResourceMapper"


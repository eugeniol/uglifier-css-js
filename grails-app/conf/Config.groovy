log4j = {
    error 'org.codehaus.groovy.grails',
          'org.springframework',
          'org.hibernate',
          'net.sf.ehcache.hibernate'
    debug "JsMinifyResourceMapper", "CssMinifyResourceMapper"
}

//grails.resources.mappers.cssminify.includes = ['**/*.css']
//grails.resources.mappers.cssminify.excludes = ['**/*.min.css']
//grails.resources.mappers.jsminify.includes = ['**/*.js']
//grails.resources.mappers.jsminify.excludes = ['**/*.min.js']
//grails.resources.mappers.uglifyjs.forceBundleMinification = true
//grails.resources.mappers.uglifyjs.noMunge = true
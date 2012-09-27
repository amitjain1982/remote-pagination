grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        //inherits true // Whether to inherit repository definitions from plugins

        grailsHome()
        grailsPlugins()
        grailsCentral()
        mavenCentral()
        //grailsPlugins()

        grailsRepo "http://grails.org/plugins"

    }

    plugins {
        build ":release:2.0.0", {
            export = false
        }
    }
}

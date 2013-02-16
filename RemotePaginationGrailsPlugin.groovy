class RemotePaginationGrailsPlugin {
    // the plugin version
    def version = "0.4.2"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0.3 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "web-app/images",
            "web-app/css"
    ]

    // TODO Fill in these fields
    def author = "Amit Jain"
    def authorEmail = "amitjain1982@gmail.com"
    def title = "Remote Pagination Plugin"
    def description = '''
   Remote-Pagination plugin provides tags for pagination and to sort columns without page refresh, using ajax and loads only the list of objects needed. It supports multiple paginations as well. More than one remotePaginate, remotePageScroll, remoteNonStopPageScroll and remoteSortableColumn can be used on the same page unlike non-ajax pagination tags.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/RemotePagination+Plugin"

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        if(!application.config.grails?.plugins?.remotepagination?.max){
            application.config.grails.plugins.remotepagination.max = 10
        }
    }

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
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

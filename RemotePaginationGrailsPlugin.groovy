class RemotePaginationGrailsPlugin {
    // the plugin version
    def version = "0.4.8"
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

    def author = "Amit Jain"
    def authorEmail = "amitjain1982@gmail.com"
    def title = "Remote Pagination Plugin"
    def description = '''
   Remote-Pagination plugin provides tags for pagination and to sort columns without page refresh, using ajax and loads only the list of objects needed. It supports multiple paginations as well. More than one remotePaginate, remotePageScroll, remoteNonStopPageScroll and remoteSortableColumn can be used on the same page unlike non-ajax pagination tags.
'''

    // URL to the plugin's documentation
    def documentation = "https://github.com/amitjain1982/remote-pagination"

    def doWithSpring = {

    }

    def doWithApplicationContext = { applicationContext ->
        if(!application.config.grails?.plugins?.remotepagination?.max){
            application.config.grails.plugins.remotepagination.max = 10
        }
		if(!application.config.grails?.plugins?.remotepagination?.enableBootstrap){
	    	application.config.grails.plugins.remotepagination.enableBootstrap = false
	    }
    }

    def doWithWebDescriptor = { xml ->
    }

    def doWithDynamicMethods = { ctx ->
    }

    def onChange = { event ->
    }

    def onConfigChange = { event ->
    }
}

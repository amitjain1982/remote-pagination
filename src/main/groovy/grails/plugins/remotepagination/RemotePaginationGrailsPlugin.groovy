package grails.plugins.remotepagination

import grails.plugins.Plugin

class RemotePaginationGrailsPlugin extends Plugin {
    def grailsVersion = "4.0.0 > *"
    def author = "Amit Jain"
    def authorEmail = "amitjain1982@gmail.com"
    def title = "Remote Pagination Plugin"
    def description = '''
   Remote-Pagination plugin provides tags for pagination and to sort columns without page refresh, using ajax and loads only the list of objects needed. It supports multiple paginations as well. More than one remotePaginate, remotePageScroll, remoteNonStopPageScroll and remoteSortableColumn can be used on the same page unlike non-ajax pagination tags.
'''
    def profiles = ['web']
    def documentation = "https://github.com/amitjain1982/remote-pagination"

    void doWithApplicationContext() {
        def conf = config.grails.plugins.remotepagination
        if (!conf.max) {
            conf.max = 10
        }
        if (!conf.enableBootstrap) {
            conf.enableBootstrap = false
        }
    }
}

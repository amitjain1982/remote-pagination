package grails.plugins.remotepagination

import org.springframework.web.servlet.support.RequestContextUtils as RCU

/**
 * Enables pagination on the list asynchronously.
 * @author Amit Jain (amitjain1982@gmail.com)
 */
class RemotePaginationTagLib {
    static namespace = "util"

    def messageSource

    def remotePaginate = { attrs ->

        assertNotNullAttribute attrs, 'total',  'remotePaginate'
        assertAttribute        attrs, 'update', 'remotePaginate'
        assertAttribute        attrs, 'total',  'remotePaginate'

        def writer = out

        Integer total = attrs.int('total') ?: 0
        Integer offset = params.int('offset') ?: (attrs.int('offset') ?: 0)
        Integer max = params.int('max') ?: (attrs.int('max') ?: config.max as Integer)
        Integer maxsteps = (params.maxsteps ?: (attrs.maxsteps ?: 10))?.toInteger()
        Boolean alwaysShowPageSizes = new Boolean(attrs.alwaysShowPageSizes ?: false)
        def pageSizes = attrs.pageSizes ?: []
        Map linkTagAttrs = attrs
        boolean bootstrapEnabled = config.enableBootstrap as boolean

        if (bootstrapEnabled) {
            writer << '<ul class="pagination">'
        }

        Map linkParams = [offset: offset - max, max: max]
        Map selectParams = [:]
        if (params.sort) linkParams.sort = params.sort
        if (params.order) linkParams.order = params.order
        if (attrs.params) {
            linkParams.putAll(attrs.params)
            selectParams.putAll(linkParams)
        }

        if (attrs.id != null) {
            linkTagAttrs.id = attrs.id
        }
        linkTagAttrs.params = linkParams

        // determine paging variables
        boolean steps = maxsteps > 0
        Integer offsetRest = offset % max
        boolean hasOffset = offsetRest > 0
        Integer currentstep = Math.ceil(offset / max) + 1
        Integer firststep = 1
        Integer laststep = Math.round(Math.ceil(total / max)) + (hasOffset ? 1 : 0)

        // display previous link when not on firststep
        if (currentstep > firststep) {
            linkTagAttrs.class = 'prevLink'
            linkParams.offset = Math.max(0, offset - max)
            writer << wrapInListItem(bootstrapEnabled, remoteLink(linkTagAttrs.clone()) {
                (attrs.prev ? attrs.prev : messageSource.getMessage('paginate.prev', null, messageSource.getMessage('default.paginate.prev', null, 'Previous', locale), locale))
            })
        }

        // display steps when steps are enabled and laststep is not firststep
        if (steps && laststep > firststep) {
            linkTagAttrs.class = 'step'

            // determine begin and endstep paging variables
            Integer beginstep = currentstep - Math.round(maxsteps / 2) + (maxsteps % 2)
            Integer endstep = currentstep + Math.round(maxsteps / 2) - 1

            if (beginstep < firststep) {
                beginstep = firststep
                endstep = maxsteps
            }
            if (endstep > laststep) {
                beginstep = laststep - maxsteps + 1
                if (beginstep < firststep) {
                    beginstep = firststep
                }
                endstep = laststep
            }

            // display firststep link when beginstep is not firststep
            if (beginstep > firststep) {
                linkParams.offset = 0
                writer << wrapInListItem(bootstrapEnabled, remoteLink(linkTagAttrs.clone()) {
                    firststep.toString()
                })
                writer << wrapInListItem(bootstrapEnabled, '<span class="step">..</span>')
            }

            // display paginate steps
            (beginstep..endstep).each { i ->
                if (currentstep == i) {
                    String currentStepClass = bootstrapEnabled ? "active" : "currentStep"
                    writer << wrapInListItem(bootstrapEnabled, """<span class="$currentStepClass">$i</span>""")
                } else {
                    linkParams.offset = computeOffset(offset, max, currentstep, i)
                    writer << wrapInListItem(bootstrapEnabled, remoteLink(linkTagAttrs.clone()) { i.toString() })
                }
            }

            // display laststep link when endstep is not laststep
            if (endstep < laststep) {
                writer << wrapInListItem(bootstrapEnabled, '<span class="step">..</span>')
                linkParams.offset = computeOffset(offset, max, currentstep, laststep)
                writer << wrapInListItem(bootstrapEnabled, remoteLink(linkTagAttrs.clone()) { laststep.toString() })
            }
        }
        // display next link when not on laststep
        if (currentstep < laststep) {
            linkTagAttrs.class = 'nextLink'
            linkParams.offset = offset + max
            writer << wrapInListItem(bootstrapEnabled, remoteLink(linkTagAttrs.clone()) {
                (attrs.next ? attrs.next : messageSource.getMessage('paginate.next', null, messageSource.getMessage('default.paginate.next', null, 'Next', locale), locale))
            })
        }

        if ((alwaysShowPageSizes || total > max) && pageSizes) {
            selectParams.remove("max")
            selectParams.offset = 0
            String paramsStr = selectParams.collect { it.key + "=" + it.value }.join("&")
            paramsStr = '\'' + paramsStr + '&max=\' + this.value'
            linkTagAttrs.params = paramsStr
            Boolean isPageSizesMap = pageSizes instanceof Map

            writer << wrapInListItem(bootstrapEnabled, "<span>" + select(from: pageSizes, value: max, name: "max", onchange: "${remoteFunction(linkTagAttrs.clone())}", class: 'remotepagesizes',
                    optionKey: isPageSizesMap ? 'key' : '', optionValue: isPageSizesMap ? 'value' : '') + "</span>")
        }

        if (bootstrapEnabled) {
            writer << '</ul>'
        }
    }

    private static int computeOffset(int offset, int max, int currentStep, int step) {
        return (step == 1 ? 0 : offset) + (Math.max(0, step - currentStep - 1)) * max
    }

    /**
     * This tag enables sort in an ascending/descending order on the particular attribute of an object, asynchronously.
     * @author Amit Jain (amit@intelligrape.com)
     */
    def remoteSortableColumn = { attrs ->

        assertAttribute attrs, 'property', 'remoteSortableColumn'
        assertAttribute attrs, 'update',   'remoteSortableColumn'
        assertAttribute attrs, 'action',   'remoteSortableColumn'

        if (!attrs.title && !attrs.titleKey) {
            throwTagError("Tag [remoteSortableColumn] is missing required attribute [title] or [titleKey]")
        }

        def writer = out
        def property = attrs.remove("property")
        String defaultOrder = attrs.remove("defaultOrder")
        if (defaultOrder != "desc") defaultOrder = "asc"
        attrs.offset = params.int('offset') ?: (attrs.offset ?: 0)
        attrs.max = params.int('max') ?: (attrs.int('max') ?: config.max as Integer)
        Map linkTagAttrs = attrs

        // current sorting property and order
        String sort = params.sort
        String order = params.order

        // add sorting property and params to link params
        Map linkParams = [:]
        if (params.id) linkParams.put("id", params.id)
        if (attrs.params) linkParams.putAll(attrs.remove("params"))
        linkParams.sort = property

        // determine and add sorting order for this column to link params
        attrs.class = (attrs.class ? "${attrs.class} sortable" : "sortable")
        if (property == sort) {
            attrs.class = attrs.class + " sorted " + order
            if (order == "asc") {
                linkParams.order = "desc"
            } else {
                linkParams.order = "asc"
            }
        } else {
            linkParams.order = defaultOrder
        }

        // determine column title
        def title = attrs.remove("title")
        def titleKey = attrs.remove("titleKey")
        if (titleKey) {
            if (!title) title = titleKey
            title = messageSource.getMessage(titleKey, null, title, locale)
        }

        linkTagAttrs.params = linkParams
        writer << "<th "
        // process remaining attributes
        attrs.each { k, v ->
            writer << """${k}="${v.encodeAsHTML()}" """
        }
        writer << """>${remoteLink(linkTagAttrs.clone()) { title }}</th>"""
    }

    /**
     * This tag enables pagination asynchronously, but unlike remotePaginate it appends latest records to the existing list.
     * Important : This tag currently works only if grails views javascript library uses jQuery.
     * @author Amit Jain (amit@intelligrape.com)
     */
    def remotePageScroll = { attrs ->

        assertNotNullAttribute attrs, 'total',  'remotePageScroll'
        assertNotNullAttribute attrs, 'update', 'remotePageScroll'
        assertAttribute        attrs, 'action', 'remotePageScroll'

        def writer = out

        Integer total = attrs.int('total') ?: 0
        Integer offset = params.int('offset') ?: (attrs.int('offset') ?: 0)
        Integer max = params.int('max') ?: (attrs.int('max') ?: config.max as Integer)
        String title = attrs.title ?: 'Show more...'

        Map linkParams = [offset: offset - max, max: max]
        Map selectParams = [:]
        if (params.sort) linkParams.sort = params.sort
        if (params.order) linkParams.order = params.order
        if (attrs.params) {
            linkParams.putAll(attrs.params)
            selectParams.putAll(linkParams)
        }

        String moreIdPrefix = "more" + attrs.update
        String moreDivId = moreIdPrefix + offset

        Map linkTagAttrs = attrs

        if (attrs.id != null) {
            linkTagAttrs.id = attrs.id
        }
        linkTagAttrs.params = linkParams

        if (offset + max < total) {
            linkTagAttrs.class = "moreLink"
            linkParams.offset = offset + max

            out << "<div id='${moreDivId}' class='paginateButtons'>"
            String linkHtml = remoteLink(linkTagAttrs.clone()) {
                (attrs.title ? attrs.title : messageSource.getMessage('paginate.more.title', null, messageSource.getMessage('default.paginate.more.title', null, title, locale), locale))
            }
            linkHtml = linkHtml.replace("jQuery('#${attrs.update}').html(data);", "jQuery('#${moreDivId}').remove();jQuery('#${attrs.update}').append(data);")
            out << linkHtml
            out << '</div>'
        }
    }

    def remoteNonStopPageScroll = { attrs ->

        assertNotNullAttribute attrs, 'total',  'remoteNonStopPageScroll'
        assertNotNullAttribute attrs, 'update', 'remoteNonStopPageScroll'
        assertAttribute        attrs, 'action', 'remoteNonStopPageScroll'

        def writer = out

        Integer total = attrs.int('total') ?: 0
        Integer offset = params.int('offset') ?: (attrs.int('offset') ?: 0)
        Integer max = params.int('max') ?: (attrs.int('max') ?: config.max as Integer)

        Map linkParams = [max: max]
        if (params.sort) linkParams.sort = params.sort
        if (params.order) linkParams.order = params.order
        if (attrs.params) {
            linkParams.putAll(attrs.params)
        }

        Map linkTagAttrs = attrs

        if (attrs.id != null) {
            linkTagAttrs.id = attrs.id
        }

        writer << "<script type='text/javascript'>"
        if (offset + max < total) {
            linkParams.offset = offset + max
            linkTagAttrs.params = linkParams
            attrs.url = createLink(linkTagAttrs.clone())
            writer << """
                jQuery('#${attrs.update}').data('remote-pagination-updatedOptions',{
                     url:"${attrs.url}",
                     scrollTarget:${attrs.scrollTarget ?: 'window'},
                     heightOffset:${attrs.heightOffset?.toInteger() ?: 10},
                     onLoading : ${attrs.onLoading ?: null},
                     onComplete : ${attrs.onComplete ?: null},
                     onSuccess : ${attrs.onSuccess ?: null},
                     onFailure : ${attrs.onFailure ?: null},
                     loadingHTML:'${attrs.loadingHTML ?: null}'
                     });
                jQuery(document).ready(function(){jQuery("#${attrs.update}").remoteNonStopPageScroll({});});
            """
        } else {
            writer << "jQuery(document).ready(function(){jQuery('#${attrs.update}').stopRemotePaginateOnScroll();});"
        }
        writer << "</script>"
    }

    def remoteLink = { attrs, body ->
        out << '<a href="'
        def cloned = deepClone(attrs)

        out << createLink(cloned)
        out << '" onclick="' // create remote function
        out << remoteFunction(attrs)
        attrs.remove('url')
        out << 'return false;"'
        // handle elementId like link
        def elementId = attrs.remove('elementId')
        if (elementId) {
            out << " id=\"${elementId}\""
        }
        // process remaining attributes
        attrs.each { k, v ->
            out << ' ' << k << '="' << v << '"'
        }
        out << ">"
        // output the body
        out << body()
        // close tag
        out << "</a>"
    }

    /**
     * Normal map implementation does a shallow clone. This implements a deep clone for maps
     * using recursion
     */
    private deepClone(Map map) {
        def cloned = [:]
        map?.each { k,v ->
            if(v instanceof Map) {
                cloned[k] = deepClone(v)
            }
            else {
                cloned[k] = v
            }
        }
        return cloned
    }

    def remoteFunction = { attrs ->
        // before remote function
        def after = ''
        if (attrs.before) {
            out << "${attrs.remove('before')};"
        }
        if (attrs.after) {
            after = "${attrs.remove('after')};"
        }
        doRemoteFunction(owner, attrs, out)
        attrs.remove('update')
        // after remote function
        if (after) {
            out << after
        }
    }

    /**
     * doRemoteFunction creates a jQuery-AJAX-Call
     *
     * @param taglib
     * @param attrs
     * @param out
     *
     * @return the jQuery-like formatted code for an AJAX-request
     */
    private doRemoteFunction(taglib, attrs, out) {
        // Optional, onLoad
        if (attrs.onLoading) {
            out << "${attrs.onLoading};"
        }

        // Start ajax
        out << /jQuery.ajax({/

        // Method
        def method = (attrs.method ? attrs.remove('method') : 'POST')
        out << "type:'$method'"

        // Optional, synchron call
        if ("false" == attrs.asynchronous) {
            out << ",async:false"
            attrs.remove('asynchronous')
        }

        // Optional, dataType to use
        if (attrs.dataType) {
            out << ",dataType:'${attrs.remove('dataType')}'"
        }

        // Additional attributes
        if (attrs.params || attrs.jsParams) {
            if (!(attrs?.params instanceof Map)) {
                // tags like remoteField don't deliver a map
                out << ",data:${attrs.remove('params')}"
            } else {
                out << ",data:{"

                boolean hasParams = false

                if (attrs?.params instanceof Map) {
                    hasParams = true
                    out << attrs.remove('params').collect { k, v ->
                        "\'" +
                                "${k}".encodeAsJavaScript() +
                                "\': \'" +
                                "${v}".encodeAsJavaScript() +
                                "\'"
                    }.join(",")
                }

                if (attrs?.jsParams instanceof Map) {
                    if (hasParams) {
                        out << ","
                    }

                    out << attrs.remove('jsParams').collect { k, v ->
                        "\'" +
                                "${k}".encodeAsJavaScript() +
                                "\': \'" +
                                "${v}".encodeAsJavaScript() +
                                "\'"
                    }.join(",")
                }

                out << "}"
            }
        }

        // build url
        def url = attrs.url ? taglib.createLink(attrs.remove('url')) : taglib.createLink(attrs);
        out << ", url:'${url}'"

        // Add callback
        buildCallback(attrs, out)

        // find all onX callback events
        def callbacks = attrs.findAll { k, v ->
            k ==~ /on(\p{Upper}|\d){1}\w+/
        }

        // remove all onX callback events
        callbacks.each { k, v ->
            attrs.remove(k)
        }

        out << "});"

        // Yeah, I know, return is not needed, but I like it
        return out
    }

    /**
     * Helper method to create callback object
     *
     * @param attrs Attributes to use for the callback
     * @param out Variable to attache the output
     */
    private buildCallback(attrs, out) {
        if (out) {
            out << ','
        }

        //*** success
        out << 'success:function(data,textStatus){'

        if (attrs.onLoaded) {
            out << "${attrs.onLoaded};"
        }

        if (attrs.update instanceof Map) {
            if (attrs.update?.success) {
                out << "jQuery('#${attrs.update.success}').html(data);"
            }
        } else if (attrs.update) {
            out << "jQuery('#${attrs.update}').html(data);"
        }

        if (attrs.onSuccess) {
            out << "${attrs.onSuccess};"
        }

        out << '}'

        //*** failure
        out << ',error:function(XMLHttpRequest,textStatus,errorThrown){'

        if (attrs.update instanceof Map) {
            if (attrs.update?.failure) {
                // Applied to GRAILSPLUGINS-1919
                out << "jQuery('#${attrs.update?.failure}').html(XMLHttpRequest.responseText);"
            }
        }

        if (attrs.onFailure) {
            out << "${attrs.onFailure};"
        }

        out << '}'

        if (attrs.onComplete) {
            out << ",complete:function(XMLHttpRequest,textStatus){${attrs.onComplete}}"
        }
    }

    private wrapInListItem(Boolean bootstrapEnabled, val) {
        bootstrapEnabled ? "<li>$val</li>" : val
    }

    private void assertNotNullAttribute(Map attrs, String name, String tagName) {
        if (attrs[name] == null) {
            throwTagError("Tag [$tagName] is missing required attribute [$name]")
        }
    }

    private void assertAttribute(Map attrs, String name, String tagName) {
        if (!attrs[name]) {
            throwTagError("Tag [$tagName] is missing required attribute [$name]")
        }
    }

    private Locale getLocale() {
        RCU.getLocale request
    }

    private getConfig() {
        grailsApplication.config.grails.plugins.remotepagination
    }
}

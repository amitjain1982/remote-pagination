# Installation
Just execute following from your application directory:

<code>grails install-plugin remote-pagination</code>

# Using
If you have used paginate and sortableColumn tag, then you will find its usage very similar to them. The tags needs to reside inside the template, which renders the list of records. Checkout the [Sample Application](https://github.com/IntelliGrape/Grails-Remote-Pagination-SampleApp/) here.
# Config
We can provide default max elements to be displayed for all remote-paginate tags via following configuration. However, it defaults to 10.
<code><pre>
//In Config.groovy
grails.plugins.remotepagination.max=20 
</pre></code>
# Tags
The remote-pagination plugin currently provides the following tags:
* remotePaginate
* remoteSortableColumn
* remotePageScroll
* remoteNonStopPageScroll

## util:remotePaginate
### Purpose

Creates next/previous buttons and a breadcrumb trail to allow pagination of results, without a page refresh using ajax calls.

<code>
 <pre>
 //Example domain class
  class Book {
         String title
         String author
  }
  //Example controller used for  all tags:
  class BookController {
    def list = {
       [books: Book.list(params)]
    }
    def filter ={
      render(template:"listTemplate" ,model:[ bookInstanceList: Book.list(params )])
    }        
  }
  </pre>
</code><br/>
remotePaginate code written inside _listTemplate.gsp:
<code><pre>
&lt;util:remotePaginate controller="book" action="filter" total="${Book.count()}"
                                   update="listTemplateDivId" max="20" pageSizes="[10, 20, 50,100]"/&gt;
&lt;util:remotePaginate controller="book" action="filter" total="${Book.count()}" update="listTemplateDivId"
                  max="20" pageSizes="[10:'10 Per Page', 20: '20 Per Page', 50:'50 Per Page',100:'100 Per Page']"/&gt;
</pre></code>
### Description
Attributes update, action and total are required. 
### Attributes
* total (required) - The total number of results to paginate 
* action (required) - The name of the action to use in the link
* update (required) - The id of the div/span which contains the tag to render the template, which displays the list.
* controller (optional) - The name of the controller to use in the link, if not specified the current controller will be linked 
* id (optional) - The id to use in the link 
* params (optional) - A map containing request parameters 
* prev (optional) - The text to display for the previous link (defaults to "Previous" as defined by default.paginate.prev property in I18n messages.properties) 
* next (optional) - The text to display for the next link (defaults to "Next" as defined by default.paginate.next property in I18n messages.properties) 
* max (optional) - The number of records displayed per page (defaults to 10). Used ONLY if params.max is empty 
* maxsteps (optional) - The number of steps displayed for pagination (defaults to 10). Used ONLY if params.maxsteps is empty 
* offset (optional) - Used ONLY if params.offset is empty
* pageSizes(optional) - The list containing different page sizes user can select from(defaults to max attribute or the first value given in the list). Provide Map instead of list to display text other than pageSize in a select box.
* alwaysShowPageSizes(optional) - A boolean value, either to show pageSizes select box irrespective of total records (defaults to false). Use only when pageSizes list or map is provided.

#### Events
  * onSuccess (optional) - The javascript function to call if successful
  * onFailure (optional) - The javascript function to call if the call failed
  * on_ERROR_CODE (optional) - The javascript function to call to handle specified error codes (eg on404="alert('not found!')"). With Prototype, this prevents execution of onSuccess and onFailure.
  * onUninitialized (optional) - The javascript function to call the a ajax engine failed to initialise
  * onLoading (optional) - The javascript function to call when the remote function is loading the response
  * onLoaded (optional) - The javascript function to call when the remote function is completed loading the response
  * onComplete (optional) - The javascript function to call when the remote function is complete, including any updates
## util:remoteSortableColumn

### Purpose

Renders a remote sortable column to support sorting in tables , without a page refresh using ajax calls.

Examples
<code><pre>
&lt;util:remoteSortableColumn property="title" title="Title" update="listTemplateDivId"/&gt;
&lt;util:remoteSortableColumn property="title" title="Title" style="width: 200px" update="listTemplateDivId"/&gt;
&lt;util:remoteSortableColumn property="author" defaultOrder="desc" title="author" 
                                    titleKey="book.author" update="listTemplateDivId"/&gt;
</pre></code>
### Description
Attributes update, action and either title or titleKey are required. When title or titleKey attributes are specified then titleKey takes precedence, resulting in the title caption to be resolved against the message source. In case when the message could not be resolved, the title will be used as title caption.

### Attributes 
* property - name of the property relating to the field 
* defaultOrder (optional) - default order for the property; choose between asc (default if not provided) and desc 
* title (optional) - title caption for the column 
* titleKey (optional) - title key to use for the column, resolved against the message source 
* params (optional) - a map containing request parameters. For ex. To set max number of records displayed as params="\[max: 10\]" 
* action (required) - the name of the action to use in the link
* update (required) â€“ the id of the div/span which contains the tag to render the template, which displays the list.
* controller (optional) - the name of the controller to use in the link, if not specified the current controller will be linked 

### Events
  * onSuccess (optional) - The javascript function to call if successful
  * onFailure (optional) - The javascript function to call if the call failed
  * on_ERROR_CODE (optional) - The javascript function to call to handle specified error codes (eg on404="alert('not found!')"). With Prototype, this prevents execution of onSuccess and onFailure.
  * onUninitialized (optional) - The javascript function to call the a ajax engine failed to initialise
  * onLoading (optional) - The javascript function to call when the remote function is loading the response
  * onLoaded (optional) - The javascript function to call when the remote function is completed loading the response
  * onComplete (optional) - The javascript function to call when the remote function is complete, including any updates

## util:remotePageScroll

### Purpose

Renders more records lazily on a click of link, appends them to the existing list of records without a page refresh using ajax calls. This tag needs to be inside a template for example "_listTemplate.gsp". 
<br/>Examples
<code><pre>
//This tag is supported only if application's grails javascript library is set to 'jQuery'.
&lt;util:remotePageScroll action="filter" total="${total}" update="listTemplateDivId"/&gt;
&lt;util:remotePageScroll action="filter" total="${total}" update="listTemplateDivId"
title="Show More Records..." max="5" class="anyCSSClass"/&gt;
</pre></code>
### Description
Attributes update, action and total are required. This should reside in a template, which loop through the records to be displayed iteratively.
### Attributes
  * total (required) - The total number of results to paginate
  * action (required) - The name of the action to use in the link
  * update (required) - The id of the div/span which contains the tag to render the template, which displays the list.
  * controller (optional) - the name of the controller to use in the link, if not specified the current controller will be linked
  * id (optional) - The id to use in the link
  * params (optional) - A map containing request parameters
  * title (optional) - The text to display for the link (defaults to "Show more...", precedence is given to 'paginate.more.title' property if found in I18n messages.properties).
  * max (optional) - The number of records displayed per page (defaults to 10). Used ONLY if params.max is empty
  * offset (optional) - Used ONLY if params.offset is empty 

### Events
  * onSuccess (optional) - The javascript function to call if successful
  * onFailure (optional) - The javascript function to call if the call failed
  * on_ERROR_CODE (optional) - The javascript function to call to handle specified error codes (eg on404="alert('not found!')"). With Prototype, this prevents execution of onSuccess and onFailure.
  * onUninitialized (optional) - The javascript function to call the a ajax engine failed to initialise
  * onLoading (optional) - The javascript function to call when the remote function is loading the response
  * onLoaded (optional) - The javascript function to call when the remote function is completed loading the response
  * onComplete (optional) - The javascript function to call when the remote function is complete, including any updates

## util:remoteNonStopPageScroll

### Purpose
Renders more records lazily on page scroll, appends them to the existing list of records without a page refresh using ajax calls. This tag needs to be inside a template for example "_listTemplate.gsp". 

Examples
<code><pre>
//This tag is supported only if application's grails javascript library is set to 'jQuery'.
//We need to include remoteNonStopPageScroll.js file provided by the plugin for this tag to work.
&lt;g:javascript plugin="remote-pagination" library="remoteNonStopPageScroll"/&gt;<br/>
&lt;util:remoteNonStopPageScroll action='filter' total="${total}" update="listTemplateDivId" /&gt;
&lt;util:remoteNonStopPageScroll action='filter' controller="book"  total="${total}" 
update="listTemplateDivId" heightOffset="10" loadingHtml="loadingGifDivId" /&gt;
</pre></code>
### Description
Attributes update, action and total are required. This tag should reside in a template for example "_listTemplate.gsp", which loops through the records to be displayed iteratively. 
### Attributes
 * total (required) - The total number of results to paginate
 * action (required) - The name of the action to use in the link
 * update (required) - The id of the div/span which contains the tag to render the template, which displays the list.
 * controller (optional) - the name of the controller to use in the link, if not specified the current controller will be linked
 * id (optional) - The id to use in the link
 * params (optional) - A map containing request parameters    
 * max (optional) - The number of records displayed per page (defaults to 10). Used ONLY if params.max is empty
 * offset (optional) - Used ONLY if params.offset is empty
 * loadingHTML (optional) - The id of div/span to be displayed while waiting for response from the server. 
 * heightOffset (optional) : Request for more records when scroll is <heightOffset> pixels before the page ends. Defaults to 10. 

### Events
 * onSuccess (optional) - The javascript function to call if successful
 * onFailure (optional) - The javascript function to call if the call failed
 * onLoading (optional) - The javascript function to call when the remote function is loading the response
 * onComplete (optional) -The javascript function to call when the remote function is complete, including any updates

## Version History
  * O.4.1 : Added loadingHTML property to remoteNonStopPageScroll tag.
  * 0.4 :- Added remoteNonStopPageScroll tag. Also the default max elements for all remote pagination tags can be provided in the config.groovy. This plugin has been upgraded to work with grails versions >= 2.0.3.
  * 0.3 :- Added remotePageScroll tag, In remotePaginateTag, pageSizes now accept both Map & List and also added new option alwaysShowPageSizes.
  * 0.2.8 :- Fixed jira [issue](http://jira.grails.org/browse/GPREMOTEPAGINATION-9/)
  * 0.2.7 :- Fixed two jira issues i.e [bug](http://jira.grails.org/browse/GPREMOTEPAGINATION-7/) and [improvement](http://jira.grails.org/browse/GPREMOTEPAGINATION-8/)
    

### Roadmap

Issues and improvements for this plugin are [maintained here](http://jira.codehaus.org/browse/GRAILSPLUGINS/component/14053/) on Codehaus JIRA.





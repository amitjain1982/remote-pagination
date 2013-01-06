/*
 **	This plugin was originally developed by Anderson Ferminiano (contato@andersonferminiano.com)
 ** I have updated it to suit my requirements.
 ** Amit Jain
 ** amitjain1982@gmail.com
 */

(function ($) {
    var SCROLL_INITIALIZED = 'remote-pagination-initialized';
    var UPDATED_OPTIONS = 'remote-pagination-updatedOptions';
    var SCROLLING = 'remote-pagination';
    var STATUS = 'data-remote-pagination-status';
    var LOAD_SEMAPHORE = true;

    $.fn.remoteNonStopPageScroll = function (options) {
        if ($(this).data(SCROLL_INITIALIZED)!=true) {
            $(this).data(SCROLL_INITIALIZED,true);
            var opts = $.extend($.fn.remoteNonStopPageScroll.defaults, options);
            var target = opts.scrollTarget;
            if (target == null) {
                target = $(this);
            }
            opts.scrollTarget = target;

            return this.each(function () {
                $.fn.remoteNonStopPageScroll.init($(this), opts);
            });
        }
    };

    $.fn.stopRemotePaginateOnScroll = function () {
        return this.each(function () {
            $(this).data(SCROLLING, 'disabled');
        });
    };

    $.fn.remoteNonStopPageScroll.loadContent = function (obj, opts) {
        opts = $.extend(opts, $(obj).data(UPDATED_OPTIONS) || {});
        var target = $(opts.scrollTarget);
        var mayLoadContent = (target.scrollTop() + opts.heightOffset) >= ($(document).height() - target.height());
        if (mayLoadContent && LOAD_SEMAPHORE) {
            if (opts.onLoading != null) {
                opts.onLoading();
            }

            $(obj).children().attr(STATUS, 'loaded');
            LOAD_SEMAPHORE =false;
            $.ajax({
                type:'POST',
                url:opts.url,
                data:{},
                success:function (data) {
                    $(obj).append(data);
                    var objectsRendered = $(obj).children('['+STATUS+'!=loaded]');
                    if (opts.onSuccess != null) {
                        opts.onSuccess(objectsRendered);
                    }
                }, failure:function (data) {
                    if (opts.onFailure != null) {
                        opts.onFailure();
                    }
                    LOAD_SEMAPHORE = true;
                }, complete:function (data) {
                    if (opts.onComplete != null) {
                        opts.onComplete();
                    }
                    LOAD_SEMAPHORE = true;
                },
                dataType:'html'
            });
        }

    };

    $.fn.remoteNonStopPageScroll.init = function (obj, opts) {
        opts = $.extend(opts, $(obj).data(UPDATED_OPTIONS) || {});
        var target = opts.scrollTarget;
        $(obj).data(SCROLLING, 'enabled');

        $(target).scroll(function (event) {
            if ($(obj).data(SCROLLING) == 'enabled') {
                $.fn.remoteNonStopPageScroll.loadContent(obj, opts);
            }
            else {
                event.stopPropagation();
            }
        });

        $.fn.remoteNonStopPageScroll.loadContent(obj, opts);
    };

    $.fn.remoteNonStopPageScroll.defaults = {
        'url':null,
        'onLoading':null,
        'onSuccess':null,
        'onFailure':null,
        'onComplete':null,
        'scrollTarget':null,
        'heightOffset':0
    };
})(jQuery);

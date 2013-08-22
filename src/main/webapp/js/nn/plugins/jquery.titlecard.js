/* predefine global variables here: jQuery nn window */
/*jslint eqeq: true, regexp: true, sloppy: true, vars: true */
/*!
 * jQuery Title Card Player plugin
 *
 * @requires    jQuery Core v1.8 or later
 * @requires    jQuery UI v1.8.23 or later
 * @requires    9x9 SDK (nn-sdk.js)
 * @author      Chih-Wen Yang <chihwen@doubleservice.com>
 * @version     1.6.0
 *
 * - Change Log:
 *      1.6.0:  2012/10/24 - 1. Detect both width and height then decide calculate title card size according to width or to height.
 *                           2. Title card size will be 16:9, so there might be black area on the left & right, or top & bottom.
 *                           3. Improved background image and background color switch rule due to system default background image.
 *      1.5.0:  2012/10/18 - 1. Added private strip_tags() method to strip HTML tags (html/script injection, XSS).
 *                           2. Added private destroy() method to encapsulate destroy (or cancel) feature.
 *                           3. Added optional widescreen option [boolean], default is true to keep 16:9 aspect ratio (easter egg option).
 *                           4. Added optional height option [integer], default is null (easter egg option).
 *                           5. Added {BR} transfer to html br tag.
 *                           6. Listened resize event to automaticly react with the canvas width (without reload plugin).
 *      1.4.0:  2012/10/01 - 1. Adjusted html structure (added wrapper-middle div) for effect improvement.
 *                           2. Setup all nn.log() to debug level for turn off console log by default.
 *      1.3.2:  2012/09/13 - 1. Adjusted duration distribution feature by effect kind (drop).
 *                           2. Adjusted effect hide behavior (immediate disappear no animate).
 *      1.3.1:  2012/09/13 - 1. Improved cancel (stop) feature.
 *                           2. Some minor cleanup.
 *      1.3.0:  2012/09/12 - 1. Added startStandbySec and endingStandbySec feature.
 *                           2. Adjusted duration distribution feature by effect kind (blind, clip).
 *                           3. Added none, bounce, drop, explode, highlight, puff, pulsate, scale, shake, slide effect.
 *                           4. Some minor improvements and cleanup.
 *      1.2.1:  2012/09/12 - 1. Some minor cleanup to pass JSLint coding quality tool check (http://www.jslint.com/).
 *                           2. predefine global variables: jQuery nn.
 *                           3. JSLint Directive Options: jslint eqeq: true, sloppy: true, vars: true.
 *      1.2.0:  2012/09/12 - 1. Adjusted effect feature only act on font, not on background color and image.
 *                           2. Added text on textarea manual line feed feature.
 *                           3. Added optional width option (easter egg option).
 *                           4. Some minor improvements and cleanup.
 *      1.1.0:  2012/09/11 - 1. Added font radix feature, fontSize value between 6 and 48,
 *                              and default is 20. (player screen keep 16:9 ratio.)
 *                           2. Added effect feature, default is fade. (fade, blind, clip, fold.)
 *                           3. Added duration feature, duration value between 5 and 20,
 *                              and default is 7. (start 1.5 sec. + delay + ending 1.5 sec.)
 *                           4. Added played callback feature.
 *                           5. Added cancel callback feature.
 *                           6. Added param verify and normalize feature.
 *                           7. Added background image feature.
 *      1.0.0:  2012/09/10 - Initial release.
 *
 * -------------------------------------------
 * Example:
 * -------------------------------------------
 *
 * <script src="jquery.titlecard.js"></script>
 * <div id="canvas"></div>
 * <script>
 * $('#canvas').titlecard({
 *     text: 'My video',
 *     align: 'center',
 *     effect: 'clip',
 *     duration: 10,
 *     fontSize: 20,
 *     fontColor: 'white',
 *     fontStyle: 'italic',
 *     fontWeight: 'bold',
 *     backgroundColor: 'black',
 *     backgroundImage: 'http://ecample.com/sample.jpg'
 * }, function () {
 *     // call back after title card played
 * });
 * // cancel playing title card and release resources
 * $('#canvas').titlecard('cancel', function () {});
 * </script>
 *
 * -------------------------------------------
 * Lists of possible parameter combinations:
 * -------------------------------------------
 *
 * play with default values
 * $('#canvas').titlecard();
 *
 * play with parameters
 * $('#canvas').titlecard(parameters);
 *
 * play with parameters and calls callback function
 * $('#canvas').titlecard(parameters, callback);
 *
 * play with default values and calls callback function
 * $('#canvas').titlecard(callback);
 * $('#canvas').titlecard(null, callback);  // easter egg method overloading
 *
 * cancel playing title card and release resources
 * $('#canvas').titlecard('cancel');
 *
 * cancel playing title card, release resources, and calls callback function
 * $('#canvas').titlecard('cancel', callback);
 */

(function ($) {
    $.fn.titlecard = function (options, callback) {
        // nn.log({
            // options: options,
            // callback: callback
        // }, 'debug');

        var strip_tags = function (input, allowed) {
            // version: 1109.2015
            allowed = allowed || '';
            allowed += '';
            allowed = (allowed.toLowerCase().match(/<[a-z][a-z0-9]*>/g) || []).join(''); // making sure the allowed arg is a string containing only tags in lowercase (<a><b><c>)
            var tags = /<\/?([a-z][a-z0-9]*)\b[^>]*>/gi,
                commentsAndPhpTags = /<!--[\s\S]*?-->|<\?(?:php)?[\s\S]*?\?>/gi;
            return input.replace(commentsAndPhpTags, '').replace(tags, function ($0, $1) {
                return allowed.indexOf('<' + $1.toLowerCase() + '>') > -1 ? $0 : '';
            });
        };

        var destroy = function (element, func) {
            var callback = null;
            if ('function' === typeof func) {
                callback = func;
            }
            element
                .clearQueue()
                .stop()
                .children()                                 // wrapper-canvas
                    .clearQueue()
                    .stop()
                    .children()                             // wrapper-outer
                        .clearQueue()
                        .stop()
                        .children()                         // wrapper-middle and img
                            .clearQueue()
                            .stop()
                        .end()                              // wrapper-outer
                    .end()                                  // wrapper-canvas
                    .hide('fast', callback);
        };

        // param overloading check
        var playedCallback = null,
            cancelCallback = null;
        if ('undefined' === typeof options) {
            //nn.log('play with default values', 'debug');
            playedCallback = null;
        } else {
            var isPassParamCheck = false;
            if ('string' === typeof options) {
                if ('cancel' === options) {
                    isPassParamCheck = true;
                    if ('function' === typeof callback) {
                        //nn.log('setup cancel callback', 'debug');
                        cancelCallback = callback;
                    }
                    //nn.log('cancel playing title card, release resources', 'debug');
                    destroy($(this), cancelCallback);
                } else {
                    //nn.log('param error nothing to do', 'error');
                }
                return;
            }
            if ('function' === typeof options || 'function' === typeof callback) {
                //nn.log('setup played callback', 'debug');
                if ('function' === typeof options) {
                    isPassParamCheck = true;
                    playedCallback = options;
                    options = {};
                } else {
                    playedCallback = callback;
                }
            }
            if ('object' === typeof options) {
                // include null object
                isPassParamCheck = true;
                //nn.log('setup options', 'debug');
            }
            if (!isPassParamCheck) {
                //nn.log('param error nothing to do', 'error');
                return;
            }
        }

        // setup options
        var opts = $.extend({}, $.fn.titlecard.defaults, options || {});
        // nn.log({
            // opts: opts,
            // playedCallback: playedCallback
        // }, 'debug');

        return this.each(function () {
            var $this = $(this),
                width = (opts.width && opts.width <= $this.width()) ? parseInt(opts.width, 10) : $this.width(),
                height = (opts.height && opts.height <= $this.height()) ? parseInt(opts.height, 10) : $this.height(),
                heightTemp = 0,
                outerFloat = 'none',
                outerMarginTop = 0,
                text = strip_tags($.trim(opts.text)).replace(/(\n|\{BR\})/g, '<br />'),
                align = opts.align,
                fontRadix = parseInt(opts.fontSize, 10),
                fontSize = 0,
                fontStyle = opts.fontStyle,
                fontWeight = opts.fontWeight,
                wrapperId = (this.id) ? (this.id + '-') : '',
                wrapperCanvas = wrapperId + 'wrapper-canvas',
                wrapperOuter = wrapperId + 'wrapper-outer',
                wrapperMiddle = wrapperId + 'wrapper-middle',
                wrapperInner = wrapperId + 'wrapper-inner',
                wrapperHtml = '';

            if (opts.widescreen) {
                heightTemp = Math.round((width / 16) * 9);
                if (heightTemp > height) {
                    width = Math.round((height / 9) * 16);
                } else {
                    height = heightTemp;
                }
            }
            if ($this.height() > height) {
                outerFloat = 'left';
                outerMarginTop = (($this.height() - height) / 2) + 'px';
            }

            // basic options normalize
            if (fontRadix < $.fn.titlecard.allows.fontSize.min
                    || fontRadix > $.fn.titlecard.allows.fontSize.max) {
                fontRadix = $.fn.titlecard.defaults.fontSize;
            }
            fontSize = Math.round(width / fontRadix);
            if (-1 === $.inArray(align, $.fn.titlecard.allows.align)) {
                align = $.fn.titlecard.defaults.align;
            }
            if (-1 === $.inArray(fontStyle, $.fn.titlecard.allows.fontStyle)) {
                fontStyle = $.fn.titlecard.defaults.fontStyle;
            }
            if (-1 === $.inArray(fontWeight, $.fn.titlecard.allows.fontWeight)) {
                fontWeight = $.fn.titlecard.defaults.fontWeight;
            }

            // basic html structure and css style
            wrapperHtml  = '<div class="' + wrapperCanvas + '"><div class="' + wrapperOuter + '">';
            wrapperHtml += '<div class="' + wrapperMiddle + '"><div class="' + wrapperInner + '"></div></div>';
            if (('' != opts.backgroundImage && '' == opts.backgroundColor)
                    || ('' != opts.backgroundImage && '' != opts.backgroundColor && $.fn.titlecard.system.backgroundImage != opts.backgroundImage)) {
                wrapperHtml += '<img src="' + opts.backgroundImage + '" style="width: 100%; height: 100%; border: none;" />';
            }
            wrapperHtml += '</div></div>';
            $this.show().wrapInner(wrapperHtml).children('.' + wrapperCanvas).hide().css({
                display: 'block',
                width: '100%',
                height: '100%',
                backgroundColor: '#000'
            }).children('.' + wrapperOuter).hide().css({
                display: 'block',
                position: 'relative',
                overflow: 'hidden',
                zIndex: 50,
                "float": outerFloat,
                width: width + 'px',
                height: height + 'px',
                margin: outerMarginTop + ' auto',
                backgroundColor: opts.backgroundColor
            }).children('.' + wrapperMiddle).hide().css({
                display: 'block',
                position: 'absolute',
                width: '100%',
                height: '100%'
            }).children('.' + wrapperInner).hide().html(text).css({
                display: 'block',
                position: 'absolute',
                left: '1%',
                width: '98%',
                height: 'auto',
                textAlign: align,
                fontSize: fontSize + 'pt',
                color: opts.fontColor,
                fontStyle: fontStyle,
                fontWeight: fontWeight
            });

            // vertical align
            var selfWidth = $this.children().children('.' + wrapperOuter).children('.' + wrapperMiddle).children('.' + wrapperInner).width(),
                selfHeight = $this.children().children('.' + wrapperOuter).children('.' + wrapperMiddle).children('.' + wrapperInner).height(),
                selfLeft = 0,
                selfTop = 0;
            if (width > selfWidth) {
                selfLeft = (width - selfWidth) / 2;
            }
            if (height > selfHeight) {
                selfTop = (height - selfHeight) / 2;
            }
            $this.children().children('.' + wrapperOuter).children('.' + wrapperMiddle).children('.' + wrapperInner).css({
                top: selfTop + 'px',
                left: selfLeft + 'px'
            });

            // effect (play callback) and duration (start, delay, ending)
            var effect = opts.effect,
                duration = parseInt(opts.duration, 10),
                startStandbySec = 500,
                endingStandbySec = 500,
                startSec = 1500,
                endingSec = 1500,
                delaySec = 3000;
            if (-1 === $.inArray(effect, $.fn.titlecard.allows.effect)) {
                effect = $.fn.titlecard.defaults.effect;
            }
            if (duration < $.fn.titlecard.allows.duration.min
                    || duration > $.fn.titlecard.allows.duration.max) {
                duration = $.fn.titlecard.defaults.duration;
            }
            duration *= 1000;
            if (-1 !== $.inArray(effect, ['blind', 'clip', 'drop'])) {
                startSec = endingSec = 1000;
            }
            if ('none' === effect) {
                startStandbySec = endingStandbySec = startSec = endingSec = 0;
            }
            delaySec = (duration - startStandbySec - startSec - endingSec - endingStandbySec);

            switch (effect) {
            case 'blind':
            case 'clip':
            case 'fold':
            case 'drop':
            case 'bounce':
            case 'explode':
            case 'highlight':
            case 'puff':
            case 'pulsate':
            case 'scale':
            case 'shake':
            case 'slide':
                $this.children().children().show(startStandbySec).children('.' + wrapperMiddle).hide().show(effect, {}, startSec).delay(delaySec).hide(effect, {}, endingSec, function () {
                    $this.children().delay(endingStandbySec).hide(0, playedCallback);
                });
                break;
            case 'fade':
                $this.children().children().show(startStandbySec).children('.' + wrapperMiddle).hide().fadeIn(startSec).delay(delaySec).fadeOut(endingSec, function () {
                    $this.children().delay(endingStandbySec).hide(0, playedCallback);
                });
                break;
            default:
                // none
                $this.children().children().children().show(0).delay(duration).hide(0, function () {
                    $this.children().hide(0, playedCallback);
                });
                break;
            }

            $(window).bind('resize', function () {
                var width = $this.width(),
                    height = $this.height(),
                    heightTemp = 0,
                    outerFloat = 'none',
                    outerMarginTop = 0;

                if (opts.widescreen) {
                    heightTemp = Math.round((width / 16) * 9);
                    if (heightTemp > height) {
                        width = Math.round((height / 9) * 16);
                    } else {
                        height = heightTemp;
                    }
                }
                if ($this.height() > height) {
                    outerFloat = 'left';
                    outerMarginTop = (($this.height() - height) / 2) + 'px';
                }

                $this.children().children('.' + wrapperOuter).css({
                    "float": outerFloat,
                    width: width,
                    height: height,
                    margin: outerMarginTop + ' auto'
                }).children('.' + wrapperMiddle).children('.' + wrapperInner).css({
                    fontSize: Math.round(width / fontRadix) + 'pt'
                });
                var selfWidth = $this.children().children('.' + wrapperOuter).children('.' + wrapperMiddle).children('.' + wrapperInner).width(),
                    selfHeight = $this.children().children('.' + wrapperOuter).children('.' + wrapperMiddle).children('.' + wrapperInner).height(),
                    selfLeft = 0,
                    selfTop = 0;
                if (width > selfWidth) {
                    selfLeft = (width - selfWidth) / 2;
                }
                if (height > selfHeight) {
                    selfTop = (height - selfHeight) / 2;
                }
                $this.children().children('.' + wrapperOuter).children('.' + wrapperMiddle).children('.' + wrapperInner).css({
                    top: selfTop + 'px',
                    left: selfLeft + 'px'
                });
            });
        });
    };

    // default options
    $.fn.titlecard.defaults = {
        widescreen: true,           // easter egg option
        width: null,                // easter egg option
        height: null,               // easter egg option
        text: 'My video',
        align: 'center',
        effect: 'none',
        duration: 7,
        fontSize: 20,               // NOTE: actually not font size, but font radix to scale with canvas width (canvas width / font radix = scale font size)
        fontColor: 'white',
        fontStyle: 'normal',
        fontWeight: 'normal',
        backgroundColor: 'black',
        backgroundImage: ''
    };

    // allow options
    $.fn.titlecard.allows = {
        align: ['left', 'center', 'right'],
        effect: ['none', 'fade', 'blind', 'clip', 'fold', 'drop', 'bounce', 'explode', 'highlight', 'puff', 'pulsate', 'scale', 'shake', 'slide'],
        duration: { min: 5, max: 20 },
        fontSize: { min: 6, max: 48 },
        fontStyle: ['normal', 'italic'],
        fontWeight: ['normal', 'bold', 'bolder']
    };

    // system options
    $.fn.titlecard.system = {
        backgroundImage: 'http://9x9ui.s3.amazonaws.com/war/v0/images/titlecard-default.png'
    };
}(jQuery));
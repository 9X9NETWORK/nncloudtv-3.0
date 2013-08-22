/**
 * @author <a href="mailto:yifan.9x9@gmail.com">Yi Fan Liao</a>
 */

/**
 * @function
 * @param {string} name Object name.
 * @param {string} domElement DOM element selector string.
 * @param {object} options Options.
 */

function Navigatable(name, domElement, options) {

    var self = this;

    this.name = name;
    this.currentIndex = 0;
    this.context = null;

    this.domElement = domElement;
    this.$domElement = $(domElement);

    if ( !! options) {
        this.upNode = options.upNode || null;
        this.bottomNode = options.bottomNode || null;
        this.leftNode = options.leftNode || null;
        this.rightNode = options.rightNode || null;

        this.onSelected = options.onSelected || function() {};
        this.onFocus = options.onFocus || this.onFocus;
        this.onBlur = options.onBlur || this.onBlur;
        this.onExit = options.onExit || this.onExit;
        this.onOpen = options.onOpen || this.onOpen;
        this.onClose = options.onClose || this.onClose;

        this.parentNode = options.parentNode || null;
    }
    this.childNodes = [];

    this.focusedNode = null;

    this.$domElement.on('mouseup', function() {
        if ( !! self.parentNode) {
            self.parentNode.focusedNode = self;
            self.onSelected();
        }
    });
}

Navigatable.prototype.selectLeft = function() {
    if ( !! this.focusedNode.leftNode) {
        this.focusedNode.onBlur();
        this.focusedNode.leftNode.rightNode = this.focusedNode;
        this.focusedNode = this.focusedNode.leftNode;
        this.focusedNode.onFocus();
    }
};

Navigatable.prototype.selectRight = function() {
    if ( !! this.focusedNode.rightNode) {
        this.focusedNode.onBlur();
        this.focusedNode.rightNode.leftNode = this.focusedNode;
        this.focusedNode = this.focusedNode.rightNode;
        this.focusedNode.onFocus();
    }
};

Navigatable.prototype.selectUp = function() {
    if ( !! this.focusedNode.upNode) {
        this.focusedNode.onBlur();
        this.focusedNode.upNode.bottomNode = this.focusedNode;
        this.focusedNode = this.focusedNode.upNode;
        this.focusedNode.onFocus();
    }
};

Navigatable.prototype.selectDown = function() {
    if ( !! this.focusedNode.bottomNode) {
        this.focusedNode.onBlur();
        this.focusedNode.bottomNode.upNode = this.focusedNode;
        this.focusedNode = this.focusedNode.bottomNode;
        this.focusedNode.onFocus();
    }
};

Navigatable.prototype.select = function() {
    this.focusedNode.onBlur();
    this.focusedNode.onSelected();
    return this;
};

Navigatable.prototype.hide = function() {
    // var $parent = this.$domElement.parent();
    // $parent.hide();
    // $parent.parent().hide();
    this.onClose();
};

Navigatable.prototype.show = function(onExitCallback, options) {
    var $parent = this.$domElement.parent();
    $parent.parent().show();
    this.onOpen(options);
    // $parent.css('left', 'calc(50% - ' + $parent.width() / 2 + 'px)');
    // $parent.css('top', 'calc(50% - ' + $parent.height() / 2 + 'px)');
    $parent.css('left', '50%');
    $parent.css('top', '50%');
    $parent.css('margin-left', $parent.width() / 2 * -1 + 'px');
    $parent.css('margin-top', $parent.height() / 2 * -1 + 'px');

    $(".overlay-bg").stop().fadeIn(500);

    if (typeof onExitCallback === 'function') {
        this.onExitCallback = onExitCallback;
    } else if ( !! onExitCallback) {
        throw 'Argument is not a function.';
    } else {
        onExitCallback = function() {};
    }
};

Navigatable.prototype.exit = function(option) {

    this.hide();

    $(".overlay-bg").stop().fadeOut(500);

    if (typeof this.onExit === 'function') {
        this.onExit(option);
    }

    if (typeof this.onExitCallback === 'function') {
        this.onExitCallback(option);
        this.onExitCallback = function() {};
    }
};

Navigatable.prototype.init = function(onExitCallback) {
    if (typeof onExitCallback === 'function') {
        this.onExitCallback = onExitCallback;
    } else if ( !! onExitCallback) {
        throw 'Argument is not a function.';
    }
};

// Runs on mouse clicked enter pressed.
Navigatable.prototype.onSelected = function() {};

Navigatable.prototype.onFocus = function() {
    this.$domElement.addClass('on');
    this.$domElement.focus();
    this.$domElement.select();

    this.$domElement.find('input').focus();
    this.$domElement.find('input').select();
    this.$domElement.find('textarea').focus();
    this.$domElement.find('textarea').select();
};

Navigatable.prototype.onBlur = function() {
    this.$domElement.removeClass('on');
    this.$domElement.blur();

    this.$domElement.find('input').blur();
    this.$domElement.find('textarea').blur();
};

Navigatable.prototype.onOpen = function(options) {
    this.$domElement.show();
};

Navigatable.prototype.onClose = function() {
    this.$domElement.hide();
};

Navigatable.prototype.onExit = function() {};

Navigatable.prototype.onExitCallback = function() {};

Navigatable.prototype.constructor = Navigatable;

var NavigatableEmail = function(name, domElement, options) {

    Navigatable.call(this, name, domElement, options);

    var self = this;

    this.onSelected = function() {
        // console.log('email selected');

        // Open email popup.
        // Global: currentComponent, emailSharing.
        // These codes should be redesigned with something like pop up window manager.
        var callback;

        if ( !! currentComponent) {
            self.parentNode.hide();

            $(popEvents).trigger('emailSharing');

            // callback = function () {
            //  self.parentNode.show();
            //  currentComponent = self.parentNode;
            // };

            // if (!emailSharing) {
            //  emailSharing = new EmailSharing();
            // }

            // emailSharing.show(callback);
            // currentComponent = emailSharing;
        } else {
            throw 'Global currentComponent missing';
        }
        // --------
    };
};

NavigatableEmail.prototype = Object.create(Navigatable.prototype);

var NavigatableFacebook = function(name, domElement, options) {

    Navigatable.call(this, name, domElement, options);

    var self = this;

    this.onSelected = function() {
        // console.log('email selected');

        // Share to Facebook.
        if ( !! currentComponent) {
            self.parentNode.hide();

            $(popEvents).trigger('fbSharing');

        } else {
            throw 'Global currentComponent missing';
        }
        // --------
    };
};

NavigatableFacebook.prototype = Object.create(Navigatable.prototype);

var NavigatableFollow = function(name, domElement, options) {

    Navigatable.call(this, name, domElement, options);

    var self = this;

    // Is displaying 'Follow' or not.
    this.isFollow = options.isFollow;

    this.onSelected = function() {
        // console.log('email selected');

        // Open email popup.
        // Global: currentComponent, overlayNotice.
        // These codes should be redesigned with something like pop up window manager.
        var callback;

        if ( !! currentComponent) {


            self.parentNode.hide();

            // callback = function (isChanged) {
            //  if (isChanged) {
            //      self.update(!self.isFollow);
            //  }
            //  self.parentNode.show();
            //  currentComponent = self.parentNode;
            // };

            // if (!overlayNotice) {
            //  overlayNotice = new OverlayNotice();
            // }

            if (self.isFollow) {

                $(popEvents).trigger('follow');

            } else {

                $(popEvents).trigger('unfollow');

            }

        } else {
            throw 'Global currentComponent missing';
        }
        // --------
    };

    this.update = function(isFollow) {

        self.isFollow = isFollow;

        var text = isFollow ? 'Follow' : 'Unfollow';
        self.$domElement.html('<i class="icon-follow"></i>' + text);

        if (!isFollow) {
            self.$domElement.addClass('unfollow');
        } else {
            self.$domElement.removeClass('unfollow');
        }

    };

    this.update(this.isFollow);
};

NavigatableFollow.prototype = Object.create(Navigatable.prototype);

var Preview = function(domElement, options) {

    domElement = domElement ? domElement : '.subscribe-preview';

    Navigatable.call(this, 'Preview', domElement, options);

    var self = this;
    self.options = {};
    // var url = options.url || '';

    // Setup navigatable elements.
    var facebook, email, channelUrl, close, follow;

    facebook = new NavigatableFacebook('facebook', self.domElement + ' .fb-sharing-btn.facebook-button', {
        parentNode: self
    });
    this.childNodes.push(facebook);

    email = new NavigatableEmail('email', self.domElement + ' .mail-sharing-btn.icecolor-button', {
        parentNode: self
    });
    this.childNodes.push(email);

    channelUrl = new Navigatable('channelUrl', self.domElement + ' .sharing-wrap input', {
        parentNode: self,
        onSelected: function() {
            // console.log('channelUrl selected');
        }
    });
    this.childNodes.push(channelUrl);
    // channelUrl.$domElement.html(url);

    close = new Navigatable('close', self.domElement + ' .overlay-button-wrap.bottom .black-button', {
        parentNode: self,
        onSelected: function() {
            // console.log('close selected');
            self.exit();
        }
    });
    this.childNodes.push(close);

    follow = new NavigatableFollow('follow', self.domElement + ' .overlay-button-wrap .follow-button', {
        parentNode: self
    });
    this.childNodes.push(follow);

    this.onOpen = function(options) {
        self.options = options;
        self.$domElement.parent().show();
        self.$domElement.show();
        follow.update(options.isFollow);
        channelUrl.$domElement.val(window.location.origin + "/view?ch=" + options.channel.id + "&mso=" + options.mso);
        self.$domElement.find('div[class$="info"] span[class$="title"]').html(options.channel.name);
        self.$domElement.find('div[class$="info"] span[class$="desc"]').html(options.channel.description);
        self.$domElement.find('figure div img').attr('src', options.channel.images[1]);
        // Setup default focusing element.
        self.focusedNode = follow;
        follow.onFocus();
    };

    this.onClose = function() {
        self.$domElement.hide();
        self.$domElement.parent().hide();
    };

    // Setup default navigatable element relative nodes.
    facebook.rightNode = email;
    facebook.bottomNode = channelUrl;
    email.leftNode = facebook;
    email.bottomNode = channelUrl;
    channelUrl.upNode = facebook;
    channelUrl.bottomNode = close;
    close.upNode = channelUrl;
    close.rightNode = follow;
    follow.upNode = channelUrl;
    follow.leftNode = close;

};

Preview.prototype = Object.create(Navigatable.prototype);

var EmailSharing = function(domElement, options) {

    domElement = domElement ? domElement : '.email-sharing-wrap';

    Navigatable.call(this, 'EmailSharing', domElement, options);

    var self = this;

    // Setup navigatable elements.
    var to, message, word, verification, close, send;

    var userToken, captcha;

    to = new Navigatable('to', self.domElement + ' .form-group:eq(0)', {
        parentNode: self,
        onSelected: function() {
            // console.log('to selected');
        }
        // onFocus: function () {
        //  to.$domElement.addClass('on');
        //  to.$domElement.find('input').focus();
        // },
        // onBlur: function () {
        //  to.$domElement.removeClass('on');
        //  to.$domElement.find('input').blur();
        // }
    });
    this.childNodes.push(to);

    message = new Navigatable('message', self.domElement + ' .form-group:eq(1)', {
        parentNode: self,
        onSelected: function() {
            // console.log('message selected');
        }
        // onFocus: function () {
        //  message.$domElement.addClass('on');
        //  message.$domElement.find('textarea').focus();
        // },
        // onBlur: function () {
        //  message.$domElement.removeClass('on');
        //  message.$domElement.find('textarea').blur();
        // }
    });
    this.childNodes.push(message);

    word = new Navigatable('word', self.domElement + ' .word-verification', {
        parentNode: self,
        onSelected: function() {
            // console.log('word selected');

            // Get captcha.
            getCaptcha(userToken);
            // --------
        },
        onFocus: function() {
            word.$domElement.addClass('on');
            word.$domElement.parent('.form-group').addClass('on');
            // word.$domElement.find('textarea').focus();
        },
        onBlur: function() {
            word.$domElement.removeClass('on');
            word.$domElement.parent('.form-group').removeClass('on');
            // word.$domElement.find('textarea').blur();
        }
    });
    this.childNodes.push(word);

    verification = new Navigatable('verification', self.domElement + ' #verification-input', {
        parentNode: self,
        onSelected: function() {
            // console.log('verification selected');
        },
        onFocus: function() {
            verification.$domElement.addClass('on');
            verification.$domElement.parent('.form-group').addClass('on');
            verification.$domElement.focus();
            verification.$domElement.select();
        },
        onBlur: function() {
            verification.$domElement.removeClass('on');
            verification.$domElement.parent('.form-group').removeClass('on');
            verification.$domElement.blur();
        }
    });
    this.childNodes.push(verification);

    close = new Navigatable('close', self.domElement + ' .overlay-button-wrap .black-button:eq(0)', {
        parentNode: self,
        onSelected: function() {
            // console.log('close selected');
            self.exit();
        }
    });
    this.childNodes.push(close);

    send = new Navigatable('send', self.domElement + ' .overlay-button-wrap .black-button:eq(1)', {
        parentNode: self,
        onSelected: function() {
            // console.log('send selected');

            // Check if fields are filled, send email and close pop up.
            $(popEvents).trigger('emailSubmit', {
                user: userToken,
                toEmail: to.$domElement.find('input').val(),
                // subject: 
                content: message.$domElement.find('textarea').val(),
                captcha: captcha,
                text: verification.$domElement.val()
            });

            self.exit();
            // --------
        }
    });
    this.childNodes.push(send);

    this.select = function() {
        self.focusedNode.onSelected();
    };

    this.onOpen = function(options) {
        // self.clear();

        self.$domElement.parent().show();
        self.$domElement.show();

        // Get captcha.
        userToken = options.userToken;
        getCaptcha(options.userToken);

        // Setup default focusing element.
        self.focusedNode = to;
        to.onFocus();
    };

    this.onClose = function() {
        self.$domElement.hide();
        self.$domElement.parent().hide();
    };

    // Clear inputs.
    this.clear = function() {
        self.$domElement.find('input').each(function() {
            $(this).val('');
        });
        self.$domElement.find('textarea').each(function() {
            $(this).val('');
        });
    };

    function getCaptcha(userToken) {
        $.ajax({
            url: "/playerAPI/requestCaptcha",
            data: {
                user: userToken,
                action: 2
            }
        }).done(function(data) {
            // console.log(data);
            captcha = data.split('--\n')[1];
            word.$domElement.find('img').attr('src', 'http://9x9ui.s3.amazonaws.com/captchas/' + captcha);
        });
    }

    // Setup default navigatable element relative nodes.
    to.bottomNode = message;
    message.upNode = to;
    message.bottomNode = word;
    word.upNode = message;
    word.bottomNode = verification;
    verification.upNode = word;
    verification.bottomNode = close;
    close.upNode = verification;
    close.rightNode = send;
    send.upNode = verification;
    send.leftNode = close;

};

EmailSharing.prototype = Object.create(Navigatable.prototype);

var ForgotPassword = function(domElement, options) {

    domElement = domElement ? domElement : '.forgot-pw-wrap';

    Navigatable.call(this, 'ForgotPassword', domElement, options);

    var self = this;

    // Setup navigatable elements.
    var email, close, reset;

    email = new Navigatable('email', self.domElement + ' .form-group input', {
        parentNode: self,
        onSelected: function() {
            // console.log('email selected');
        }
    });
    this.childNodes.push(email);

    close = new Navigatable('close', self.domElement + ' .overlay-button-wrap .black-button:eq(0)', {
        parentNode: self,
        onSelected: function() {
            // console.log('close selected');
            self.exit();
        }
    });
    this.childNodes.push(close);

    reset = new Navigatable('reset', self.domElement + ' .overlay-button-wrap .black-button:eq(1)', {
        parentNode: self,
        onSelected: function() {
            // console.log('reset selected');

            // reset the channel.




            // --------
        }
    });
    this.childNodes.push(reset);

    this.onOpen = function() {
        self.$domElement.parent().show();
        self.$domElement.show();

        // Setup default focusing element.
        self.focusedNode = email;
        email.onFocus();
    };

    this.onClose = function() {
        self.$domElement.hide();
        self.$domElement.parent().hide();
    };

    // Setup default navigatable element relative nodes.
    email.bottomNode = close;
    close.upNode = email;
    close.rightNode = reset;
    reset.upNode = email;
    reset.leftNode = close;

};

ForgotPassword.prototype = Object.create(Navigatable.prototype);

var GuideShortCut = function(domElement, options) {

    domElement = domElement ? domElement : '.guide-short-cut';

    Preview.call(this, domElement, options);

    var self = this;

    // Setup navigatable elements.
    var groupName, save, facebook, email, move, channelUrl, close, follow;

    // Retreive elements from super class.
    for (var i = this.childNodes.length - 1; i >= 0; i--) {
        switch (this.childNodes[i].name) {
            case 'facebook':
                facebook = this.childNodes[i];
                break;
            case 'email':
                email = this.childNodes[i];
                break;
            case 'channelUrl':
                channelUrl = this.childNodes[i];
                break;
            case 'close':
                close = this.childNodes[i];
                break;
            case 'follow':
                follow = this.childNodes[i];
                break;
        }
    }

    groupName = new Navigatable('groupName', self.domElement + ' .group-name-wrap input', {
        parentNode: self,
        onSelected: function() {
            // console.log('groupName selected');
        }
    });
    this.childNodes.push(groupName);

    save = new Navigatable('save', self.domElement + ' .group-name-wrap a', {
        parentNode: self,
        onSelected: function() {
            // console.log('save selected');

            // Save.
            $(popEvents).trigger("saveTitle", self.$domElement.find('.group-name-wrap input').val());

            self.exit();

            // --------
        }
    });
    this.childNodes.push(save);

    move = new Navigatable('move', self.domElement + ' .overlay-button-wrap.right-top .black-button', {
        parentNode: self,
        onSelected: function() {
            // console.log('move selected');

            // Move.




            // --------
        }
    });
    this.childNodes.push(move);

    this.onOpen = function(options) {
        self.$domElement.parent().show();
        self.$domElement.show();
        follow.update(options.isFollow);
        channelUrl.$domElement.val(window.location.origin + "/view?ch=" + options.channel.id + "&mso=" + options.mso);
        self.$domElement.find('div[class$="info"] span[class$="title"]').html(options.channel.name);
        self.$domElement.find('div[class$="info"] span[class$="desc"]').html(options.channel.description);
        self.$domElement.find('figure div img').attr('src', options.channel.images[1]);

        self.$domElement.find('.group-name-wrap input').val(options.title);
        // Setup default focusing element.
        self.focusedNode = follow;
        follow.onFocus();
        save.onBlur();
    };

    // Setup default navigatable element relative nodes.
    groupName.rightNode = save;
    groupName.bottomNode = facebook;
    save.leftNode = groupName;
    save.bottomNode = move;
    facebook.upNode = groupName;
    // facebook.rightNode = email;
    // facebook.bottomNode = channelUrl;
    email.upNode = groupName;
    // email.leftNode = facebook;
    // email.bottomNode = channelUrl;
    email.rightNode = move;
    move.upNode = save;
    move.leftNode = email;
    move.bottomNode = channelUrl;
    // channelUrl.upNode = facebook;
    // channelUrl.bottomNode = close;
    // close.upNode = channelUrl;
    // close.rightNode = unfollow;
    // follow.upNode = channelUrl;
    // follow.leftNode = close;

};

GuideShortCut.prototype = Object.create(Preview.prototype);

var OverlayNotice = function(domElement, options) {

    domElement = domElement ? domElement : '.overlay-notice';

    Navigatable.call(this, 'OverlayNotice', domElement, options);

    var self = this;
    self.yes = {};
    self.no = {};

    // Setup navigatable elements.
    var yes, no, close;

    // Anti-pattern here.
    self.$domElement.find('.notice-desc-wrap').append('<span class="notice-desc" style="display:none"></span>');

    var mode = options && options.mode ? options.mode : 'YesNo';
    var notice = {
        followed: ' .notice-desc:eq(0)',
        confirmUnfollow: ' .notice-desc:eq(1)',
        unfollowed: ' .notice-desc:eq(2)',
        noMoreEpisode: ' .notice-desc:eq(3)',
        success: ' .notice-desc:eq(4)',
        change: ' .notice-desc:eq(5)',
        error: ' .notice-desc:eq(6)',
        custom: ' .notice-desc:last-child'
    };
    var currentNotice;

    yes = new Navigatable('yes', self.domElement + ' .overlay-button-wrap .black-button:eq(0)', {
        parentNode: self,
        onSelected: function() {
            // console.log('yes selected');
            var callback = function(isSuccess) {
                var isChanged = true;
                if (isSuccess) {

                } else {
                    isChanged = false;
                }
                self.exit(isChanged);
            };

            // Make change according to current notice.
            if (self.currentNotice === 'confirmUnfollow') {
                // Unfollow channel.

                $(popEvents).trigger('confirmUnfollow', [true]);

                // --------
            } else if (self.currentNotice === 'change') {
                // Close pop up.
                self.exit();
            } else if (self.currentNotice === 'custom') {
                self.exit();
                if (typeof self.yes.click === 'function') {
                    self.yes.click();
                }
            } else {
                throw 'Request not successful.';
            }
        }
    });
    this.childNodes.push(yes);

    no = new Navigatable('no', self.domElement + ' .overlay-button-wrap .black-button:eq(1)', {
        parentNode: self,
        onSelected: function() {
            // console.log('no selected');
            // var isChanged = false;
            // self.exit(isChanged);

            if (self.currentNotice === 'confirmUnfollow') {
                $(popEvents).trigger('confirmUnfollow', [false]);
            } else if (self.currentNotice === 'custom') {
                self.exit();
                if (typeof self.no.click === 'function') {
                    self.no.click();
                }
            }
        }
    });
    this.childNodes.push(no);

    close = new Navigatable('close', self.domElement + ' .overlay-button-wrap .black-button:eq(2)', {
        parentNode: self,
        onSelected: function() {
            // console.log('close selected');
            self.exit();
        }
    });
    this.childNodes.push(close);

    this.onOpen = function(options) {
        // var closeOverlayLayer = true;

        self.$domElement.parent().show();
        self.$domElement.show();

        // Setup default focusing element.
        if (mode === 'YesNo') {
            yes.$domElement.show();
            no.$domElement.show();
            close.$domElement.hide();
            self.focusedNode = no;
        } else if (mode === 'Close') {
            yes.$domElement.hide();
            no.$domElement.hide();
            close.$domElement.show();
            self.focusedNode = close;
        }
        self.focusedNode.onFocus();

        if (options) {
            self.$domElement.find(notice.custom).html(options.message);
            // if (!options.closeOverlayLayer) {
            //  self.onClose = function () {
            //      self.$domElement.hide();    
            //  };
            // }
        }
    };

    this.hide = function() {
        self.onClose();
    };

    this.onClose = function() {
        self.$domElement.hide();
        self.$domElement.parent().hide();
    };

    this.setMode = function(newMode) {
        mode = newMode;
    };

    this.setNotice = function(newNotice) {
        switch (newNotice) {
            case 'confirmUnfollow':
            case 'change':
                self.setMode('YesNo');
                break;
            default:
                self.setMode('Close');
                break;
        }
        // if (newNotice === 'confirmUnfollow' || newNotice === 'change') {
        //  self.setMode('YesNo');
        // } else {
        //  self.setMode('Close');
        // }

        for (var prop in notice) {
            if (prop !== newNotice) {
                self.$domElement.find(notice[prop]).hide();
            } else {
                self.$domElement.find(notice[prop]).show();
            }
        }

        self.currentNotice = newNotice;
    };

    // Setup default navigatable element relative nodes.
    no.leftNode = yes;
    yes.rightNode = no;

};

OverlayNotice.prototype = Object.create(Navigatable.prototype);

var PlaybackSubscribePreview = function(domElement, options) {

    domElement = domElement ? domElement : '.playback-subscribe-preview';

    Preview.call(this, domElement, options);

    var self = this;

    // Setup navigatable elements.
    var facebook, email, channelUrl, close, follow;
    var channel, episode;

    // Retreive elements from super class.
    for (var i = this.childNodes.length - 1; i >= 0; i--) {
        switch (this.childNodes[i].name) {
            case 'facebook':
                facebook = this.childNodes[i];
                break;
            case 'email':
                email = this.childNodes[i];
                break;
            case 'channelUrl':
                channelUrl = this.childNodes[i];
                break;
            case 'close':
                close = this.childNodes[i];
                break;
            case 'follow':
                follow = this.childNodes[i];
                break;
        }
    }

    channel = new Navigatable('channel', self.domElement + ' .sharing-wrap .black-button:eq(0)', {
        parentNode: self,
        onSelected: function() {
            // console.log('channel selected');
            channel.$domElement.addClass('active');
            episode.$domElement.removeClass('active');
            channel.onFocus();

            // Change url to channel.




            // --------
        }
        // onFocus: function() {
        //     this.$domElement.addClass('on');
        //     this.$domElement.focus();
        //     this.$domElement.select();

        //     this.$domElement.find('input').focus();
        //     this.$domElement.find('input').select();
        //     this.$domElement.find('textarea').focus();
        //     this.$domElement.find('textarea').select();

        //     channelUrl.$domElement.val(window.location.origin + "/view?ch=" + options.channel.id);
        // }
    });
    this.childNodes.push(channel);
    channel.onFocus = function() {
        this.$domElement.addClass('on');
        this.$domElement.focus();
        this.$domElement.select();

        this.$domElement.find('input').focus();
        this.$domElement.find('input').select();
        this.$domElement.find('textarea').focus();
        this.$domElement.find('textarea').select();

        channelUrl.$domElement.val(window.location.origin + "/view?ch=" + self.options.channel.id + "&mso=" + self.options.mso);
    };

    episode = new Navigatable('episode', self.domElement + ' .sharing-wrap .black-button:eq(1)', {
        parentNode: self,
        onSelected: function() {
            // console.log('episode selected');
            episode.$domElement.addClass('active');
            channel.$domElement.removeClass('active');
            episode.onFocus();

            // Change url to episode.




            // --------
        }
        // onFocus: function() {
        //     this.$domElement.addClass('on');
        //     this.$domElement.focus();
        //     this.$domElement.select();

        //     this.$domElement.find('input').focus();
        //     this.$domElement.find('input').select();
        //     this.$domElement.find('textarea').focus();
        //     this.$domElement.find('textarea').select();
            
        //     channelUrl.$domElement.val(window.location.origin + "/view?ch=" + options.channel.id + "&ep=" + options.channel.episodeId);
        // }
    });
    this.childNodes.push(episode);
    episode.onFocus = function() {
        this.$domElement.addClass('on');
        this.$domElement.focus();
        this.$domElement.select();

        this.$domElement.find('input').focus();
        this.$domElement.find('input').select();
        this.$domElement.find('textarea').focus();
        this.$domElement.find('textarea').select();
        
        var link = window.location.href,
            episodeId = link.split('/').pop();
        episodeId = episodeId.length > 4 ? episodeId : self.options.channel.episodes[0].id;
        channelUrl.$domElement.val(window.location.origin + "/view?ch=" + self.options.channel.id + "&mso=" + self.options.mso + "&ep=" + episodeId);
    };

    // Setup default navigatable element relative nodes.
    channel.upNode = facebook;
    channel.rightNode = episode;
    channel.bottomNode = close;
    episode.upNode = facebook;
    episode.rightNode = channelUrl;
    episode.leftNode = channel;
    episode.bottomNode = close;
    channelUrl.leftNode = episode;

    // Activate channel url by default.
    // channel.#domElement.addClass('active');

};

PlaybackSubscribePreview.prototype = Object.create(Preview.prototype);

var StoreItemPreview = function(domElement, options) {

    domElement = domElement ? domElement : '.store-item-preview';

    Preview.call(this, domElement, options);

    // var self = this;

    // // Setup navigatable elements.

    // // Setup default navigatable element relative nodes.

};

StoreItemPreview.prototype = Object.create(Preview.prototype);

var TermsAndPrivacy = function(domElement, options) {

    domElement = domElement ? domElement : '.terms-service';

    Navigatable.call(this, 'TermsAndPrivacy', domElement, options);

    var self = this;

    // Setup navigatable elements.
    var back, previous, next;
    var $container = $(domElement + ' .terms-privacy-outline');
    var $content = $(domElement + ' .term-privacy-content');
    var containerHeight = $container.height();
    var contentTop = 0;

    back = new Navigatable('back', self.domElement + ' .black-button:eq(0)', {
        parentNode: self,
        onSelected: function() {
            // console.log('back selected');
            self.exit();
        }
    });
    this.childNodes.push(back);

    previous = new Navigatable('previous', self.domElement + ' .black-button:eq(2)', {
        parentNode: self,
        onSelected: function() {
            // console.log('previous selected');
            // console.log('top: ' + $content.css('top'));
            // console.log('saved: ' + contentTop);
            // Scroll up one page height.
            if (contentTop > -containerHeight) {
                $content.animate({
                    'top': '0'
                }, 'slow');
                contentTop = 0;
            } else {
                $content.animate({
                    'top': '+=' + containerHeight + 'px'
                }, 'slow');
                contentTop += containerHeight;
            }
            // --------
        }
    });
    this.childNodes.push(previous);

    next = new Navigatable('next', self.domElement + ' .black-button:eq(1)', {
        parentNode: self,
        onSelected: function() {
            // console.log('previous selected');
            // console.log('top: ' + $content.css('top'));
            // console.log('saved: ' + contentTop);
            var top = -$content.height() + containerHeight;
            // Scroll down one page height.
            if (contentTop + $content.height() < 2 * containerHeight) {
                $content.animate({
                    'top': top
                }, 'slow');
                contentTop = top;
            } else {
                $content.animate({
                    'top': '-=' + containerHeight + 'px'
                }, 'slow');
                contentTop -= containerHeight;
            }
            // --------
        }
    });
    this.childNodes.push(next);

    this.select = function() {
        this.focusedNode.onSelected();
    };

    this.onOpen = function() {
        self.$domElement.parent().show();
        self.$domElement.show();

        // Setup default focusing element.
        self.focusedNode = next;
        self.focusedNode.onFocus();
    };

    this.onClose = function() {
        self.$domElement.hide();
        self.$domElement.parent().hide();
    };

    // Setup default navigatable element relative nodes.
    back.rightNode = previous;
    previous.leftNode = back;
    previous.rightNode = next;
    next.leftNode = previous;

};

TermsAndPrivacy.prototype = Object.create(Navigatable.prototype);
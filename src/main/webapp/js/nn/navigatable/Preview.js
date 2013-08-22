/**
 * @author <a href="mailto:yifan.9x9@gmail.com">Yi Fan Liao</a>
 */

/**
 * @function
 * @param {string} domElement DOM element selector string.
 * @param {object} options Options.
 */
var Preview = function(domElement, options) {

  domElement = domElement ? domElement : '.subscribe-preview';

  Navigatable.call(this, 'Preview', domElement, options);

  var self = this;

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

  close = new Navigatable('close', self.domElement + ' .overlay-button-wrap .black-button', {
    parentNode: self,
    onSelected: function() {
      // console.log('close selected');
      self.exit();
    }
  });
  this.childNodes.push(close);

  follow = new NavigatableFollow('follow', self.domElement + ' .overlay-button-wrap .follow-button', {
    parentNode: self,
    isFollow: true
  });
  this.childNodes.push(follow);

  this.onOpen = function() {
    self.$domElement.parent().show();
    self.$domElement.show();

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

Preview.prototype = Object.create( Navigatable.prototype ); 
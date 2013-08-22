/**
 * @author <a href="mailto:yifan.9x9@gmail.com">Yi Fan Liao</a>
 */

/**
 * @function
 * @param {string} domElement DOM element selector string.
 * @param {object} options Options.
 */
var GuideShortCut = function(domElement, options) {

  domElement = domElement ? domElement : '.guide-short-cut';

  Navigatable.call(this, 'GuideShortCut', domElement, options);

  var self = this;

  // Setup navigatable elements.
  var groupName, save, facebook, email, move, channelUrl, close, unfollow;

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




      // --------
    }
  });
  this.childNodes.push(save);

  facebook = new NavigatableFacebook('facebook', self.domElement + ' .fb-sharing-btn.facebook-button', {
    parentNode: self
  });
  this.childNodes.push(facebook);

  email = new NavigatableEmail('email', self.domElement + ' .mail-sharing-btn.icecolor-button', {
    parentNode: self
  });
  this.childNodes.push(email);

  move = new Navigatable('move', self.domElement + ' .overlay-button-wrap.right-top .black-button', {
    parentNode: self,
    onSelected: function() {
      // console.log('move selected');

      // Move.




      // --------
    }
  });
  this.childNodes.push(move);

  channelUrl = new Navigatable('channelUrl', self.domElement + ' .sharing-wrap input', {
    parentNode: self,
    onSelected: function() {
      // console.log('channelUrl selected');
    }
  });
  this.childNodes.push(channelUrl);

  close = new Navigatable('close', self.domElement + ' .overlay-button-wrap.bottom .black-button', {
    parentNode: self,
    onSelected: function() {
      // console.log('close selected');
      self.exit();
    }
  });
  this.childNodes.push(close);

  unfollow = new NavigatableFollow('unfollow', self.domElement + ' .overlay-button-wrap .follow-button', {
    parentNode: self,
    isFollow: false
  });
  this.childNodes.push(unfollow);

  this.onOpen = function() {
    self.$domElement.parent().show();
    self.$domElement.show();

    // Setup default focusing element.
    self.focusedNode = save;
    save.onFocus();
  };

  this.onClose = function() {
    self.$domElement.hide();
    self.$domElement.parent().hide();
  };

  // Setup default navigatable element relative nodes.
  groupName.rightNode = save;
  groupName.bottomNode = facebook;
  save.leftNode = groupName;
  save.bottomNode = move;
  facebook.upNode = groupName;
  facebook.rightNode = email;
  facebook.bottomNode = channelUrl;
  email.upNode = groupName;
  email.leftNode = facebook;
  email.bottomNode = channelUrl;
  email.rightNode = move;
  move.upNode = save;
  move.leftNode = email;
  move.bottomNode = channelUrl;
  channelUrl.upNode = facebook;
  channelUrl.bottomNode = close;
  close.upNode = channelUrl;
  close.rightNode = unfollow;
  unfollow.upNode = channelUrl;
  unfollow.leftNode = close;

};

GuideShortCut.prototype = Object.create( Navigatable.prototype ); 
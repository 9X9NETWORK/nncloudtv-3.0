/**
 * @author <a href="mailto:yifan.9x9@gmail.com">Yi Fan Liao</a>
 */

/**
 * @function
 * @param {string} domElement DOM element selector string.
 * @param {object} options Options.
 */
var OverlayNotice = function(domElement, options) {

  domElement = domElement ? domElement : '.overlay-notice';

  Navigatable.call(this, 'OverlayNotice', domElement, options);

  var self = this;

  // Setup navigatable elements.
  var yes, no, close;

  var mode = options && options.mode ? options.mode : 'YesNo';
  var notice = {
    followed: ' .notice-desc:eq(0)',
    unfollow: ' .notice-desc:eq(1)',
    unfollowed: ' .notice-desc:eq(2)',
    noMoreEpisode: ' .notice-desc:eq(3)',
    change: ' .notice-desc:eq(4)',
  };
  var currentNotice;

  yes = new Navigatable('yes', self.domElement + ' .overlay-button-wrap .black-button:eq(0)', {
    parentNode: self,
    onSelected: function() {
      // console.log('yes selected');
      var callback = function(isSuccess) {
        var isChanged = true;
        if (isSuccess) {

        }
        else {
          isChanged = false;
        }
        self.exit(isChanged);
      };

      // Make change according to current notice.
      if (self.currentNotice==='unfollow') {
        // Unfollow channel.



        // --------
      }
      else if (self.currentNotice==='change') {
        // Close pop up.
        self.exit();
      }
      else {
        throw 'Request not successful.'
      }
    }
  });
  this.childNodes.push(yes);

  no = new Navigatable('no', self.domElement + ' .overlay-button-wrap .black-button:eq(1)', {
    parentNode: self,
    onSelected: function() {
      // console.log('no selected');
      var isChanged = false;
      self.exit(isChanged);
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

  this.onOpen = function() {
    self.$domElement.parent().show();
    self.$domElement.show();

    // Setup default focusing element.
    if (mode==='YesNo') {
      yes.show();
      no.show();
      close.hide();
      self.focusedNode = no;
    }
    else if (mode==='Close') {
      yes.hide();
      no.hide();
      close.show();
      self.focusedNode = close;
    }
    self.focusedNode.onFocus();
  };

  this.onClose = function() {
    self.$domElement.hide();
    self.$domElement.parent().hide();
  };

  this.setMode = function(newMode) {
    mode = newMode;
  };

  this.setNotice = function(newNotice) {
    if (newNotice==='unfollow' || newNotice==='change') {
      self.setMode('YesNo');
    }
    else {
      self.setMode('Close');
    }

    for (var prop in notice) {
      if (prop !== newNotice) {
        self.$domElement.find(notice[prop]).hide();
      }
      else {
        self.$domElement.find(notice[prop]).show();
      }
    }

    self.currentNotice = newNotice;
  };

  // Setup default navigatable element relative nodes.
  no.leftNode = yes;
  yes.rightNode = no;

};

OverlayNotice.prototype = Object.create( Navigatable.prototype ); 
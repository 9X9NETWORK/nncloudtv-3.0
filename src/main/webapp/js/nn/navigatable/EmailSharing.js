/**
 * @author <a href="mailto:yifan.9x9@gmail.com">Yi Fan Liao</a>
 */

/**
 * @function
 * @param {string} domElement DOM element selector string.
 * @param {object} options Options.
 */
var EmailSharing = function(domElement, options) {

  domElement = domElement ? domElement : '.email-sharing-wrap';

  Navigatable.call(this, 'EmailSharing', domElement, options);

  var self = this;

  // Setup navigatable elements.
  var to, message, word, verification, close, send;

  to = new Navigatable('to', self.domElement + ' .form-group:eq(0)', {
    parentNode: self,
    onSelected: function() {
      // console.log('to selected');
    },
    onFocus: function() {
      to.$domElement.addClass('on');
      to.$domElement.find('input').focus();
    },
    onBlur: function() {
      to.$domElement.removeClass('on');
      to.$domElement.find('input').blur();
    }
  });
  this.childNodes.push(to);

  message = new Navigatable('message', self.domElement + ' .form-group:eq(1)', {
    parentNode: self,
    onSelected: function() {
      // console.log('message selected');
    },
    onFocus: function() {
      message.$domElement.addClass('on');
      message.$domElement.find('textarea').focus();
    },
    onBlur: function() {
      message.$domElement.removeClass('on');
      message.$domElement.find('textarea').blur();
    }
  });
  this.childNodes.push(message);

  word = new Navigatable('word', self.domElement + ' .word-verification', {
    parentNode: self,
    onSelected: function() {
      // console.log('word selected');

      // Change verification code.




      // --------
    },
    onFocus: function() {
      word.$domElement.addClass('on');
      word.$domElement.parent('.form-group').addClass('on');
      word.$domElement.find('textarea').focus();
    },
    onBlur: function() {
      word.$domElement.removeClass('on');
      word.$domElement.parent('.form-group').removeClass('on');
      word.$domElement.find('textarea').blur();
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




      // --------
    }
  });
  this.childNodes.push(send);

  this.onOpen = function() {
    self.$domElement.parent().show();
    self.$domElement.show();

    // Setup default focusing element.
    self.focusedNode = to;
    to.onFocus();
  };

  this.onClose = function() {
    self.$domElement.hide();
    self.$domElement.parent().hide();
  };

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

EmailSharing.prototype = Object.create( Navigatable.prototype ); 
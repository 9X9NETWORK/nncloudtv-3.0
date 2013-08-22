/**
 * @author <a href="mailto:yifan.9x9@gmail.com">Yi Fan Liao</a>
 */

/**
 * @function
 * @param {string} domElement DOM element selector string.
 * @param {object} options Options.
 */
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

ForgotPassword.prototype = Object.create( Navigatable.prototype ); 
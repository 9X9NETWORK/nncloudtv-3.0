/**
 * @author <a href="mailto:yifan.9x9@gmail.com">Yi Fan Liao</a>
 */

/**
 * @function
 * @param {string} name Object name.
 * @param {string} domElement DOM element selector string.
 * @param {object} options Options.
 */
var NavigatableEmail = function (name, domElement, options) {
  
  Navigatable.call(this, name, domElement, options);

  var self = this;

  this.onSelected = function() {
    // console.log('email selected');

    // Open email popup.
    // Global: currentComponent, emailSharing.
    // These codes should be redesigned with something like pop up window manager.
    var callback;

    if (!!currentComponent) {
      self.parentNode.hide();

      callback = function() {
        self.parentNode.show();
        currentComponent = self.parentNode;
      };

      if (!emailSharing) {
        emailSharing = new EmailSharing();
      }

      emailSharing.show(callback);
      currentComponent = emailSharing;
    }
    else {
      throw 'Global currentComponent missing';
    }
    // --------
  };
};

NavigatableEmail.prototype = Object.create( Navigatable.prototype );
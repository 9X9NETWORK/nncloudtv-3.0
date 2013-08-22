/**
 * @author <a href="mailto:yifan.9x9@gmail.com">Yi Fan Liao</a>
 */

/**
 * @function
 * @param {string} name Object name.
 * @param {string} domElement DOM element selector string.
 * @param {object} options Options.
 */
var NavigatableFollow = function (name, domElement, options) {
  
  Navigatable.call(this, name, domElement, options);

  var self = this;

  this.isFollow = options.isFollow;

  this.onSelected = function() {
    // console.log('email selected');

    // Open email popup.
    // Global: currentComponent, overlayNotice.
    // These codes should be redesigned with something like pop up window manager.
    var callback;

    if (!!currentComponent) {
      self.parentNode.hide();

      callback = function(isChanged) {
        if (isChanged) {
          self.update(!self.isFollow);
        }
        self.parentNode.show();
        currentComponent = self.parentNode;
      };

      if (!overlayNotice) {
        overlayNotice = new OverlayNotice();
      }

      if (self.isFollow) {
        overlayNotice.setNotice('unfollow');
      }
      else {
        overlayNotice.setNotice('followed');

        // Follow channel.




        // --------
      }
      overlayNotice.show(callback);
      currentComponent = overlayNotice;
    }
    else {
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
    }
    else {
      self.$domElement.removeClass('unfollow');
    }

  };

  this.update(this.isFollow);
};

NavigatableFollow.prototype = Object.create( Navigatable.prototype );
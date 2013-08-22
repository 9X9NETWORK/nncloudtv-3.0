/**
 * @author <a href="mailto:yifan.9x9@gmail.com">Yi Fan Liao</a>
 */

/**
 * @function
 * @param {string} name Object name.
 * @param {string} domElement DOM element selector string.
 * @param {object} options Options.
 */
var NavigatableFacebook = function (name, domElement, options) {
  
  Navigatable.call(this, name, domElement, options);

  var self = this;

  this.onSelected = function() {
    // console.log('email selected');

    // Share to Facebook.
    




    // --------
  };
};

NavigatableFacebook.prototype = Object.create( Navigatable.prototype );
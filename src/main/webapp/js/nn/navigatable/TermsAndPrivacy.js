/**
 * @author <a href="mailto:yifan.9x9@gmail.com">Yi Fan Liao</a>
 */

/**
 * @function
 * @param {string} domElement DOM element selector string.
 * @param {object} options Options.
 */
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
      if (contentTop>-containerHeight) {
        $content.animate({'top':'0'}, 'slow');
        contentTop = 0;
      }
      else {
        $content.animate({'top':'+='+containerHeight+'px'}, 'slow');
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
      var top = -$content.height()+containerHeight;
      // Scroll down one page height.
      if (contentTop+$content.height()<2*containerHeight) {
        $content.animate({'top':top}, 'slow');
        contentTop = top;
      }
      else {
        $content.animate({'top':'-='+containerHeight+'px'}, 'slow');
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
    self.focusedNode = back;
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

TermsAndPrivacy.prototype = Object.create( Navigatable.prototype ); 
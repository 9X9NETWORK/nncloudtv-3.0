/**
 * @author <a href="mailto:yifan.9x9@gmail.com">Yi Fan Liao</a>
 */

/**
 * @function
 * @param {string} domElement DOM element selector string.
 * @param {object} options Options.
 */
var PlaybackSubscribePreview = function(domElement, options) {

  domElement = domElement ? domElement : '.playback-subscribe-preview';

  Preview.call(this, domElement, options);

  var self = this;

  // Setup navigatable elements.
  var facebook, email, channelUrl, close, follow;
  var channel, episode;

  // Retreive elements from super class.
  for (var i = this.childNodes.length - 1; i >= 0; i--) {
    switch(this.childNodes[i].name) {
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
  });
  this.childNodes.push(channel);

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
  });
  this.childNodes.push(episode);

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

PlaybackSubscribePreview.prototype = Object.create( Preview.prototype ); 
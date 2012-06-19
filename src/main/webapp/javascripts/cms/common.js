/**
 * 
 */

var log = function(text) {
  if (window.console && console.log)
    console.log(text);
};

var cms = {
  debug: false,
  videoTypes: '*.m4v;*.mp4;*.mpg;*.mpeg;*.mov;*.webm;*.rm;*.rmvb;*.avi;*.wmv;*.flv;*.ogg',
  audioTypes: '*.mp3',//;*.wma;*.wav;*.aac;*.aif;*.aiff',
  imageTypes: '*.jpeg;*.jpg;*.png;*.gif',
  isFacebookLoaded: false,
  loadFacebook: function(callback) {
    if (this.isFacebookLoaded) {
      return (typeof callback == 'function') ? callback() : null;
    }
    cms.isFacebookLoaded = true;
    $('<div id="fb-root"></div>').appendTo('body');
    window.fbAsyncInit = function() {
      var fb_xd_file = 'http://' + location.host + '/channel.html';
      log('fb cross domain file: ' + fb_xd_file);
      FB.init({
        appId:     '110847978946712',
        status:     true, // check login status
        cookie:     true, // enable cookies to allow the server to access the session
        xfbml:      true, // parse XFBML
        oauth:      true, // enable OAth 2.0
        channelUrl: fb_xd_file
      });
      return (typeof callback == 'function') ? callback() : null;
    };
    require(['http://connect.beta.facebook.net/en_US/all.js']);
  },
  isNN: function() {
    return ($('#msoType').val() == '1');
  },
  isEnterprise: function() {
    return ($('#msoType').val() == '5');
  },
  isGeneric: function() {
    return ($('#msoType').val() == '4');
  },
  is3x3: function() {
    return ($('#msoType').val() == '3');
  },
  initPlusone: function(callback) {
    if (callback)
      require(['https://apis.google.com/js/plusone.js'], callback);
    else
      require(['https://apis.google.com/js/plusone.js']);
  },
  initAddthis: function() {
    window.addthis = null;
    require(['http://s7.addthis.com/js/250/addthis_widget.js'], function() {
      addthis.init();
    });
  },
  formatDate: function(timestamp, short) {
    var updateDate = new Date(timestamp);
    var year = updateDate.getFullYear();
    var month = updateDate.getMonth() + 1;
    var date = updateDate.getDate();
    var hour = updateDate.getHours();
    var minute = updateDate.getMinutes().toString();
    var second = updateDate.getSeconds().toString();
    if (minute.length < 2)
      minute = "0" + minute;
    if (second.length < 2)
      second = "0" + second;
    if (short) {
      return year + '/' + month + '/' + date;
    }
    return year + '/' + month + '/' + date + ' ' + hour + ':' + minute + ':' + second;
  },
  bubblePopupProperties: { /* need initialize */ },
  escapeHtml: function(text) {
    return text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  },
  getChannelUrl: function(channelId, programId) {
    var url = 'http://' + location.host + '/view?channel=' + channelId;
    if (programId) {
      url += '&episode=' + programId;
    }
    return url;
  },
  getFileTypeByName: function(name) {
    if (typeof name == 'undefined' || name == null || name == '' || name.indexOf('.') <= 0) {
      return '';
    }
    return name.substr(name.indexOf('.'));
  },
  getContentTypeByFileExtention: function(ext) {
    
    switch(ext) {
      
      case '.gif':
      return 'image/gif';
      break;
      
      case '.png':
      return 'image/png';
      break;
      
      case '.jpg':
      case '.jpeg':
      return 'image/jpeg';
      break;
      
      case '.aac':
      return 'audio/aac';
      break;
      
      case '.aif':
      case '.aiff':
      return 'audio/aiff';
      break;
      
      case '.wma':
      return 'audio/x-ms-wma';
      break;
      
      case '.wav':
      return 'audio/wav';
      break;
      
      case '.mp3':
      return 'audio/mpeg';
      break;
      
      case '.mpg':
      case '.mpeg':
      return 'video/mpeg';
      break;
      
      case '.m4v':
      case '.mp4':
      return 'video/mp4';
      break;
      
      case '.ogg':
      return 'video/ogg';
      break;
      
      case '.webm':
      return 'video/webm';
      break;
      
      case '.mov':
      return 'video/quicktime';
      break;
      
      case '.wmv':
      return 'video/x-ms-wmv';
      break;
      
      case '.flv':
      return 'video/x-flv';
      break;
      
      case '.avi':
      return 'video/avi';
      break;
      
      case '.rmvb':
      return 'application/vnd.rn-realmedia-vbr';
      break;
      
      case 'rm':
      return 'video/rm';
      break;
      
      default:
      return 'application/octet-stream';
    }
  },
  getS3UploadBucket: function() {
    return $('#s3_upload_bucket').text();
  },
  getExternalRootPath: function() {
    return $('#external_root_path').text();
  },
  get: function(url, data, success, dataType, error) {
    try {
      $.ajax({
        type:     'GET',
        url:      url,
        data:     data,
        success:  success,
        error:    error,
        dataType: dataType,
        statusCode: {
          404: error,
          400: error
        }
      });
    } catch (e) {
      log('ajax exception! GET');
      if (typeof error == 'function')
        error(null, null, e);
    }
  },
  post: function(url, data, success, dataType, error) {
    try {
      $.ajax({
        type:     'POST',
        url:      url,
        data:     data,
        success:  success,
        error:    error,
        dataType: dataType,
        statusCode: {
          404: error,
          400: error
        }
      });
    } catch (e) {
      log('ajax exception! POST');
      if (typeof error == 'function')
        error(null, null, e);
    }
  },
  loadJSON: function(url, callback, data) {
    $.ajax({
      url: url,
      dataType: 'json',
      data: data,
      async: true,
      cache: true,
      success: callback
    });
  },
  initSetupButton: function() {
    $('#setup_page').jqm({
      zIndex:  1000,
      toTop:   true,
      modal:   true,
      ajax:    'setup',
      trigger: '#setup',
      onLoad: function() {
        require(['setup'], function() {
          pageSetup.init();
          if (cms.isGeneric()) {
            pageSetup.initGenericOne();
          } else if (cms.isEnterprise()) {
            pageSetup.initEnterpriseOne();
          }
        });
      },
      onHide: function() {
        location.reload();
      }
    });
  },
  enableChannelManagement: function() {
    $('.menuA').attr('href', 'channelManagement');
    $('<style> .menuA:hover { background-position: 0px 0px; } </style>').appendTo('head');
    log('channel management enabled');
  },
  enableChannelSetManagement: function() {
    $('.menuB').attr('href', 'channelSetManagement');
    $('<style> .menuB:hover { background-position: -114px 0px; } </style>').appendTo('head');
    log('set management enabled');
  },
  enablePromotionTools: function() {
    $('.menuD').attr('href', 'promotionTools');
    $('<style> .menuD:hover { background-position: -226px 0px; } </style>').appendTo('head');
    log('promotion tools enabled');
  },
  enableStatistics: function() {
    $('.menuE').attr('href', 'statistics');
    $('<style> .menuE:hover { background-position: -338px 0px; } </style>').appendTo('head');
    log('statistics enabled');
  },
  init3x3One: function() {
    $('#mso_logo').wrapAll('<a href="/' + $('#msoName').val() + '"></a>');
    this.enableChannelManagement();
    this.enableChannelSetManagement();
    this.enablePromotionTools();
    this.enableStatistics();
  },
  initEnterpriseOne: function() {
    $('#mso_logo').wrapAll('<a href="/' + $('#msoName').val() + '"></a>');
    this.enableChannelManagement();
    this.enableChannelSetManagement();
    this.enableStatistics();
  },
  initNNOne: function() {
    $('#mso_logo').wrapAll('<a href="/9x9"></a>');
    this.enableChannelManagement();
    this.enableChannelSetManagement();
    this.enablePromotionTools();
    this.enableStatistics();
  },
  initGenericOne: function() {
    $('#blog_link').show();
    $('#mso_logo').wrapAll('<a href="/9x9"></a>');
    this.enableChannelManagement();
    this.enablePromotionTools();
    this.enableStatistics();
  },
  init: function() {
    $('.header .logout').css('background', 'url(' + $('#image_header_logout').text() + ') no-repeat');
    $('.header .setup').css('background', 'url(' + $('#image_header_setup').text() + ') no-repeat');
    $('.header .sg').css('background', 'url(' + $('#image_header_sg').text() + ') no-repeat');
    
    var style = '<style> ' +
      '.menuA, /*.menuA:hover,*/ .menuA_active, ' +
      '.menuB, /*.menuB:hover,*/ .menuB_active, ' +
      '.menuC, /*.menuC:hover,*/ .menuC_active, ' +
      '.menuD, /*.menuD:hover,*/ .menuD_active, ' +
      '.menuE, /*.menuE:hover,*/ .menuE_active {' +
      '  background-image: url(' + $('#image_menu').text() + '); ' +
      '} ' +
      '</style>';
    $(style).appendTo('head'); // IE compatible
    
    var css = '<style> .chPublic { background:url(' + $('#image_ch_public').text() + ') no-repeat; }\n.chUnPublic { background:url(' + $('#image_ch_unpublic').text() + ') no-repeat; } </style>';
    $(css).appendTo('head');
    
    $.ajaxSetup ({
      cache: false // Disable caching of AJAX responses
    });
    
    require(['../plugins/jquery.jqModal'], cms.initSetupButton);
    
  }
};

var addthis_config = { };

// everything is start from here
$(function() {
  
  addthis_config = {
    'data_use_cookies':     false,
    'data_use_flash':       false,
    'data_track_clickback': false,
    'services_expanded':    'email,facebook,twitter,tumblr,sinaweibo,funp,plusone.google.com',
    'pubid':                'ra-4dcccc98718a5dbe',
    'services_custom': [
      {
        name: "Google+",
        url:  "https://plusone.google.com/_/+1/confirm?hl=en&url={{URL}}",
        icon: cms.getExternalRootPath() + "/images/cms/google-plus.png"
      }
    ]
  };
  
  cms.bubblePopupProperties = {
    'position':  'top',
    'align':     'center',
    'innerHtmlStyle':
    {
      'color':      '#292929',
      'text-align': 'left',
      'font-size':  '0.8em'
    },
    'themeName': 'all-black',
    'themePath': cms.getExternalRootPath() + '/images/cms'
  },
  
  addthis_config['ui_language'] = $('#locale').val();
  log("locale: " + $('#locale').val());
  log("msoId: " + $('#msoId').val());
  log("msoType: " + $('#msoType').val());
  
  if (cms.debug) {
    require(['http://www.netgrow.com.au/assets/files/jquery_plugins/jquery.dump.js']);
  }
  require(['../plugins/jquery.blockUI'], function() {
    
    $.blockUI.defaults.message = $('#warning_please_wait').html();
    $(document).ajaxStart($.blockUI).ajaxStop($.unblockUI);
    $.blockUI();
    
    cms.init();
    
    if (cms.isGeneric()) {
      cms.initGenericOne();
    } else if (cms.isEnterprise()) {
      cms.initEnterpriseOne();
    } else if (cms.isNN()) {
      cms.initNNOne();
    } else if (cms.is3x3()) {
      cms.init3x3One();
    }
    
    if (typeof (page$) != 'undefined' && typeof (page$.init) == 'function') {
      page$.init();
      if (cms.isGeneric() && typeof (page$.initGenericOne) == 'function') {
        page$.initGenericOne();
      } else if (cms.isEnterprise() && typeof (page$.initEnterpriseOne) == 'function') {
        page$.initEnterpriseOne();
      } else if (cms.isNN() && typeof (page$.initNNOne) == 'function') {
        page$.initNNOne();
      } else if (cms.is3x3() && typeof (page$.init3x3One) == 'function') {
        page$.init3x3One();
      }
    }
    
  });
  
});


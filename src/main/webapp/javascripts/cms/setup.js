/**
 * this comment is test for EGIT pull
 */

var pageSetup = {
  
  sFacebookPermissions: 'email,read_stream,publish_stream,manage_pages,offline_access,user_birthday,friends_birthday,user_checkins,user_education_history,user_likes,friends_likes,user_location,user_photo_video_tags,friends_photo_video_tags,user_photos,friends_photos,user_videos,friends_videos,user_work_history,read_friendlists',
  initGenericOne: function() {
  },
  initEnterpriseOne: function() {
    $('#tabA').hide();
    $('.setupContent').hide();
    $('#setPs').show();
  },
  showFacebookConnect: function() {
    $('#facebook_connect').show();
    $('#facebook_disconnect').hide();
    $('#fb_field').hide();
  },
  showTwitterConnect: function() {
    $('#twitter_connect').show();
    $('#twitter_field').hide();
    $('#twitter_disconnect').hide();
    $('#tw_switch').hide();
  },
  showFacebookDisconnect: function(enabled) {
    $('#facebook_connect').hide();
    $('#facebook_disconnect').show();
    $('#fb_switch').unbind();
    if (enabled) {
      $('#fb_switch').text($('#lang_button_disable_autosharing').text()).click(function() {
        $.post('/CMSAPI/setSnsAuth', { 'msoId': $('#msoId').val(), 'enabled': false, 'type': 1 }, function(response) {
          if (response != "OK") {
            log('response: ' + response);
            alert($('#lang_warning_error_occurs').text());
            return;
          }
          alert($('#lang_update_successfully').text());
          pageSetup.showFacebookDisconnect(false);
        }, 'text');
      });
    } else {
      $('#fb_switch').text($('#lang_button_enable_autosharing').text()).click(function() {
        $.post('/CMSAPI/setSnsAuth', { 'msoId': $('#msoId').val(), 'enabled': true, 'type': 1 }, function(response) {
          if (response != "OK") {
            log('response: ' + response);
            alert($('#lang_warning_error_occurs').text());
            return;
          }
          alert($('#lang_update_successfully').text());
          pageSetup.showFacebookDisconnect(true);
        }, 'text');
      });
    }
    $('#fb_field').show();
  },
  showTwitterDisconnect: function(enabled) {
	$('#twitter_connect').hide();
	$('#twitter_field').show();
	$('#twitter_disconnect').show();
	$('#tw_switch').show();
	$('#tw_switch').unbind();
	if (enabled) {
	  $('#tw_switch').text($('#lang_button_disable_autosharing').text()).click(function() {
	    $.post('/CMSAPI/setSnsAuth', { 'msoId': $('#msoId').val(), 'enabled': false, 'type': 2 }, function(response) {
	      if (response != "OK") {
	        log('response: ' + response);
	        alert($('#lang_warning_error_occurs').text());
	        return;
	      }
	      alert($('#lang_update_successfully').text());
	      pageSetup.showTwitterDisconnect(false);
	    }, 'text');
	  });
	} else {
	  $('#tw_switch').text($('#lang_button_enable_autosharing').text()).click(function() {
	    $.post('/CMSAPI/setSnsAuth', { 'msoId': $('#msoId').val(), 'enabled': true, 'type': 2 }, function(response) {
	      if (response != "OK") {
	        log('response: ' + response);
	        alert($('#lang_warning_error_occurs').text());
	        return;
	      }
	      alert($('#lang_update_successfully').text());
	      pageSetup.showTwitterDisconnect(true);
	    }, 'text');
	  });
	}
  },
  init: function() {
    $('#tabB').hide();
    $('.setup_context').css('background', 'url(' + $('#image_bg_setup').text() + ') no-repeat');
    $('#twitter_connect').show();
    
    $.post('/CMSAPI/listSnsAuth', { 'msoId': $('#msoId').val() }, function(snsList) {
      for (i in snsList) {
        var sns = snsList[i];
        if (sns.type == 1) {
          pageSetup.showFacebookDisconnect(sns.enabled);
        }
        if (sns.type == 2) {
          pageSetup.showTwitterDisconnect(sns.enabled);
        }
      }
    });
    
    $('.syncInfo').unbind().hover(function() {
      $('#fb_hint').show();
    },function() {
      $('#fb_hint').hide();
    });
    
    $('.setupTabItem').unbind().click(function() {
      $('.setupTabItem').removeClass('link_Act').addClass('link_Normal');
      $('.setupContent').hide();
      $(this).removeClass('link_Normal').addClass('link_Act');
      
      var tabId = $(this).attr('id');
      if (tabId == 'tabA') {
        $('#syncAcc').show();
      } else if (tabId == 'tabB') {
        $('#addAcc').show();
      } else if (tabId == 'tabC') {
        $('#setPs').show();
      }
    });
    
    cms.loadFacebook(function() {
      $('#facebook_connect').unbind().click(function() {
        FB.login(function(response) {
          if (response.status != 'connected' || response.authResponse == null) {
            log('not connected');
            return;
          }
          var token = response.authResponse.userID;
          var secrete = response.authResponse.accessToken;
          log('userID: ' + token);
          log('accessToken: ' + secrete);
          if (token == null || secrete == null || token == "" || secrete == "") {
            alert($('#lang_warning_error_occurs').text());
            return;
          }
          var parameters = {
            'msoId':  $('#msoId').val(),
            'type':   1,
            'token':  token,
            'secrete': secrete
          };
          $.post('/CMSAPI/createSnsAuth', parameters, function(response) {
            if (response != "OK") {
              log('response: ' + response);
              alert($('#lang_warning_error_occurs').text());
              return;
            }
            pageSetup.showFacebookDisconnect(true);
          }, 'text');
        }, { 'scope': pageSetup.sFacebookPermissions });
      });
    });
    
    $('#facebook_disconnect').unbind().click(function() {
      if (confirm($('#lang_confirm_disconnect_with_facebook').text())) {
        var parameters = {
          'msoId': $('#msoId').val(),
          'type': 1
        };
        $.post('/CMSAPI/removeSnsAuth', parameters, function(response) {
          if (response != "OK") {
            alert($('#lang_warning_error_occurs').text());
            return;
          }
          pageSetup.showFacebookConnect();
        });
      }
    });
    
    $('#twitter_connect').unbind().click(function() {
    	window.open("http://"+location.host+"/cms/twitter/authorization?msoId="+$('#msoId').val(),"twitter authorization","width=800,height=600");
    });
    
    $('#twitter_disconnect').unbind().click(function() {
      if (confirm($('#lang_confirm_disconnect_with_twitter').text())) {
        var parameters = {
          'msoId': $('#msoId').val(),
          'type': 2
        };
        $.post('/CMSAPI/removeSnsAuth', parameters, function(response) {
          if (response != "OK") {
            alert($('#lang_warning_error_occurs').text());
            return;
          }
          pageSetup.showTwitterConnect();
        });
      }
    });
    
    $('#password').unbind().change(function() {
      if ($('#password').val() == '')
        $('#save_password_button').attr('disabled', true);
      else
        $('#save_password_button').attr('disabled', false);
    });
    
    $('#save_password_button').unbind().click(function() {
      if ($('#password').val() == '') { return; }
      if ($('#password').val() != $('#retype').val()) {
        alert($('#lang_warning_retype_not_match').text());
        return;
      }
      var parameters = {
        'msoId': $('#msoId').val(),
        'newPassword': $('#password').val()
      };
      $.post('/CMSAPI/changePassword', parameters, function(response) {
        if (response != "OK") {
          alert($('#lang_warning_error_occurs').text());
        } else {
          alert($('#lang_update_successfully').text());
          $('[type="password"]').val('');
        }
      });
    });
    
    $('#cancel_password_button').unbind().click(function() {
      $('[type="password"]').val('');
    });
  }
};




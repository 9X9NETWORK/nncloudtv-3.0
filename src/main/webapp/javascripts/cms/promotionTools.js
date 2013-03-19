/**
 * 
 */

var page$ = {
  autosharingSettings: {
    initChannelSet: function(channelSetId, channelSetName) {
      $('.sns_checkbox').attr('checked', false);
      // load auto-sharing info
      var parameters = {
        'setId': channelSetId,
        'msoId': $('#msoId').val()
      };
      cms.post('/CMSAPI/listChannelSetAutosharing', parameters, function(autosharings) {
        for (i in autosharings) {
          switch(autosharings[i].type) {
            case 1:
            $('input[name="sns_facebook"]').attr('checked', true);
            break;
            case 2:
            $('input[name="sns_twitter"]').attr('checked', true);
            break;
            case 3:
            $('input[name="sns_plurk"]').attr('checked', true);
            break;
            case 4:
            $('input[name="sns_sina"]').attr('checked', true);
            break;
            default:
          }
        }
        if ($('input[name="sns_facebook"]').attr('disabled') != 'disabled') {
          $('input[name="sns_facebook"]').unbind('change').change(function() {
            var parameters = {
              'msoId': $('#msoId').val(),
              'setId': channelSetId,
              'type': 1
            };
            if ($(this).is(':checked')) {
              cms.post('/CMSAPI/createChannelSetAutosharing', parameters, function() {
                alert($('#lang_update_successfully').text());
              });
            } else {
              cms.post('/CMSAPI/removeNnSetAutosharing', parameters, function() {
                alert($('#lang_update_successfully').text());
              });
            }
          });
        }
        if ($('input[name="sns_twitter"]').attr('disabled') != 'disabled') {
          $('input[name="sns_twitter"]').unbind('change').change(function() {
            var parameters = {
              'msoId': $('#msoId').val(),
              'setId': channelSetId,
              'type': 2
            };
            if ($(this).is(':checked')) {
              cms.post('/CMSAPI/createChannelSetAutosharing', parameters, function() {
                alert($('#lang_update_successfully').text());
              });
            } else {
              cms.post('/CMSAPI/removeNnSetAutosharing', parameters, function() {
                alert($('#lang_update_successfully').text());
              });
            }
          });
        }
        if ($('input[name="sns_plurk"]').unbind('change').attr('disabled') != 'disabled') {
          $('input[name="sns_plurk"]').change(function() {
            var parameters = {
              'msoId': $('#msoId').val(),
              'setId': channelSetId,
              'type': 3
            };
            if ($(this).is(':checked')) {
              cms.post('/CMSAPI/createChannelSetAutosharing', parameters, function() {
                alert($('#lang_update_successfully').text());
              });
            } else {
              cms.post('/CMSAPI/removeNnSetAutosharing', parameters, function() {
                alert($('#lang_update_successfully').text());
              });
            }
          });
        }
        if ($('input[name="sns_sina"]').unbind('change').attr('disabled') != 'disabled') {
          $('input[name="sns_sina"]').change(function() {
            var parameters = {
              'msoId': $('#msoId').val(),
              'setId': channelSetId,
              'type': 3
            };
            if ($(this).is(':checked')) {
              cms.post('/CMSAPI/createChannelSetAutosharing', parameters, function() {
                alert($('#lang_update_successfully').text());
              });
            } else {
              cms.post('/CMSAPI/removeNnSetAutosharing', parameters, function() {
                alert($('#lang_update_successfully').text());
              });
            }
          });
        }
        $('.right_title > div').text(channelSetName);
        $('#promotion_content').show();
      }, 'json');
    },
    initChannel: function(channel) {
      var channelId = channel.id;
      $('.right_title > div').text(channel.name);
      if (channel.contentType != 6) {
        $('#no_promotion').show();
        return;
      }
      $('.sns_checkbox').attr('checked', false);
      $('.facebook_select').val(0);
      // load auto-sharing info
      var parameters = {
        'channelId': channelId,
        'msoId': $('#msoId').val()
      };
      cms.post('/CMSAPI/listChannelAutosharing', parameters, function(autosharings) {
        for (i in autosharings) {
          var autosharing = autosharings[i];
          switch(autosharing.type) {
            case 1:
            $('input[name="sns_facebook"]').attr('checked', true);
            var target = autosharing.target;
            if (typeof target != 'undefined' && target != null && page$.facebookPages[target]) {
              $('.facebook_select').val(target);
            }
            break;
            case 2:
            $('input[name="sns_twitter"]').attr('checked', true);
            break;
            case 3:
            $('input[name="sns_plurk"]').attr('checked', true);
            break;
            case 4:
            $('input[name="sns_sina"]').attr('checked', true);
            break;
            default:
          }
        }
        if ($('input[name="sns_facebook"]').attr('disabled') != 'disabled') {
          $('.facebook_select').unbind('change').change(function() {
            var parameters = {
              'msoId': $('#msoId').val(),
              'channelId': channelId,
              'type': 1
            };
            var fb_page_id = $('.facebook_select').val();
            if (fb_page_id != null && fb_page_id != 0 && page$.facebookPages[fb_page_id]) {
              parameters['target'] = fb_page_id;
              parameters['parameter'] = page$.facebookPages[fb_page_id].access_token;
            }
            if ($('input[name="sns_facebook"]').is(':checked') == false) {
              return;
            }
            cms.post('/CMSAPI/createChannelAutosharing', parameters, function(response) {
              if (response == 'OK')
                alert($('#lang_update_successfully').text());
              else
                alert($('#lang_warning_error_occurs').text());
            }, 'text');
          });
          $('input[name="sns_facebook"]').unbind('change').change(function() {
            var parameters = {
              'msoId': $('#msoId').val(),
              'channelId': channelId,
              'type': 1
            };
            var fb_page_id = $('.facebook_select').val();
            if (fb_page_id != null && fb_page_id != 0 && page$.facebookPages[fb_page_id]) {
              parameters['target'] = fb_page_id;
              parameters['parameter'] = page$.facebookPages[fb_page_id].access_token;
            }
            if ($('input[name="sns_facebook"]').is(':checked')) {
              cms.post('/CMSAPI/createChannelAutosharing', parameters, function(response) {
                if (response == 'OK')
                  alert($('#lang_update_successfully').text());
                else
                  alert($('#lang_warning_error_occurs').text());
              }, 'text');
            } else {
              cms.post('/CMSAPI/removeChannelAutosharing', parameters, function(response) {
                if (response == 'OK')
                  alert($('#lang_update_successfully').text());
                else
                  alert($('#lang_warning_error_occurs').text());
              }, 'text');
            }
          });
        }
        if ($('input[name="sns_twitter"]').attr('disabled') != 'disabled') {
          $('input[name="sns_twitter"]').unbind('change').change(function() {
            var parameters = {
              'msoId': $('#msoId').val(),
              'channelId': channelId,
              'type': 2
            };
            if ($(this).is(':checked')) {
              cms.post('/CMSAPI/createChannelAutosharing', parameters, function() {
                alert($('#lang_update_successfully').text());
              });
            } else {
              cms.post('/CMSAPI/removeChannelAutosharing', parameters, function() {
                alert($('#lang_update_successfully').text());
              });
            }
          });
        }
        if ($('input[name="sns_plurk"]').attr('disabled') != 'disabled') {
          $('input[name="sns_plurk"]').unbind('change').change(function() {
            var parameters = {
              'msoId': $('#msoId').val(),
              'channelId': channelId,
              'type': 3
            };
            if ($(this).is(':checked')) {
              cms.post('/CMSAPI/createChannelAutosharing', parameters, function() {
                alert($('#lang_update_successfully').text());
              });
            } else {
              cms.post('/CMSAPI/removeChannelAutosharing', parameters, function() {
                alert($('#lang_update_successfully').text());
              });
            }
          });
        }
        if ($('input[name="sns_sina"]').attr('disabled') != 'disabled') {
          $('input[name="sns_sina"]').unbind('change').change(function() {
            var parameters = {
              'msoId': $('#msoId').val(),
              'channelId': channelId,
              'type': 3
            };
            if ($(this).is(':checked')) {
              cms.post('/CMSAPI/createChannelAutosharing', parameters, function() {
                alert($('#lang_update_successfully').text());
              });
            } else {
              cms.post('/CMSAPI/removeChannelAutosharing', parameters, function() {
                alert($('#lang_update_successfully').text());
              });
            }
          });
        }
        $('#promotion_content').show();
      }, 'json');
    }
  },
  channelAndChannelSetList: {
    init: function() {
      // load channels
      cms.loadJSON('/CMSAPI/listOwnedChannels?msoId=' + $('#msoId').val(), function(channels) {
        for (i in channels) {
          var channelInfoBlock = $('#channel_info_block').clone(true).removeAttr('id').addClass('channel_info_block_cloned');
          var channelId = channels[i].id;
          
          channelInfoBlock.find('.iconStatistics').attr('title', $('#lang_view_statistics').text());
          channelInfoBlock.find('.channel_info_title div').text(channels[i].name);
          $('<img/>').attr('src', channels[i].imageUrl).appendTo(channelInfoBlock.find('.channel_info_image'));
          var contentType = 'Unknown';
          switch(channels[i].contentType) {
            case 1:
            contentType = 'System';
            break;
            case 2:
            contentType = 'Podcast';
            break;
            case 3:
            contentType = 'YouTube Channel';
            break;
            case 4:
            contentType = 'YouTube Playlist';
            break;
            case 5:
            contentType = 'Facebook';
            break;
            case 6:
            contentType = '9x9';
            break;
            default:
          }
          channelInfoBlock.find('.channel_info_contenttype span').text(contentType);
          channelInfoBlock.find('.channel_info_programcount span').text(channels[i].programCnt);
          channelInfoBlock.find('.channel_info_subscribers span').text(channels[i].subscriptionCnt);
          channelInfoBlock.find('.channel_info_updatedate span').text(cms.formatDate(channels[i].updateDate));
          // add this
          var promoteUrl = 'http://' + location.host + '/view?channel=' + channelId;
          channelInfoBlock.find('.addthis_button_expanded').attr('addthis:url', promoteUrl + '&_=' + channels[i].updateDate);
          var switchObject = channelInfoBlock.find('.channel_info_publish');
          if (channels[i]['public']) {
            switchObject.removeClass('chUnPublic').addClass('chPublic');
          } else {
            switchObject.removeClass('chPublic').addClass('chUnPublic');
          }
          var promoteUrlTruncated = promoteUrl;
          if (promoteUrl.length > 40) {
            promoteUrlTruncated = promoteUrl.substring(0, 36) + '...';
          }
          channelInfoBlock.find('.channel_info_promoteurl').text(promoteUrlTruncated).attr('href', promoteUrl);
          $('<li></li>').append(channelInfoBlock).appendTo('#channel_list_ul');
          // channel info block click event
          channelInfoBlock.click({ 'channel': channels[i] }, function(event) {
            
            var channel = event.data.channel;
            
            $('.channel_info_block').removeClass('chFocus').addClass('chUnFocus');
            $('.channel_info_title').removeClass('chFocusTitle').addClass('chUnFocusTitle');
            $('.channel_info_image').removeClass('chFocusImg').addClass('chUnFocusImg');
            $(this).removeClass('chUnFocus').addClass('chFocus');
            $('.channel_info_title', this).removeClass('chUnFocusTitle').addClass('chFocusTitle');
            $('.channel_info_image', this).removeClass('chUnFocusImg').addClass('chFocusImg');
            $('#promotion_content').hide();
            $('#no_promotion').hide();
            
            // load auto-sharing info
            page$.autosharingSettings.initChannel(channel);
          });
        }
        //$('#channel_list').show();
        cms.initAddthis();
        var pivot = $('.channel_info_block_cloned').get(0);
        $(pivot).click();
      });
      /*
      // load channel sets
      cms.loadJSON('/CMSAPI/listOwnedChannelSets?msoId=' + $('#msoId').val(), function(channelSets) {
        for (i in channelSets) {
          var channelSetInfoBlock = $('#channel_info_block').clone(true).removeAttr('id').addClass('channel_info_block_cloned');
          var channelSetId = channelSets[i].id;
          
          channelSetInfoBlock.find('.iconStatistics').attr('title', $('#lang_view_statistics').text());
          channelSetInfoBlock.find('.channel_info_title div').text(channelSets[i].name);
          $('<img/>').attr('src', channelSets[i].imageUrl).appendTo(channelSetInfoBlock.find('.channel_info_image'));
          channelSetInfoBlock.find('.channel_info_contenttype span').text($('#lang_label_channel_set').text());
          channelSetInfoBlock.find('.channel_info_programcount span').text('N/A');
          channelSetInfoBlock.find('.channel_info_subscribers span').text(channelSets[i].subscriptionCnt);
          channelSetInfoBlock.find('.channel_info_updatedate span').text(cms.formatDate(channelSets[i].updateDate));
          // add this
          var promoteUrl = 'http://' + location.host + '/';
          promoteUrl += ((channelSets[i].beautifulUrl != null) ? channelSets[i].beautifulUrl : channelSets[i].defaultUrl);
          channelSetInfoBlock.find('.addthis_button_expanded').attr('addthis:url', promoteUrl);
          var switchObject = channelSetInfoBlock.find('.channel_info_publish');
          if (channelSets[i]['public']) {
            switchObject.removeClass('chUnPublic').addClass('chPublic');
          } else {
            switchObject.removeClass('chPublic').addClass('chUnPublic');
          }
          channelSetInfoBlock.find('.channel_info_promoteurl').text(promoteUrl).attr('href', promoteUrl);
          $('<li></li>').append(channelSetInfoBlock).appendTo('#channel_set_list_ul');
          // channel info block click event
          channelSetInfoBlock.click({ 'channelSetId': channelSetId, 'channelSetName': channelSets[i].name}, function(event)
          {
            $('.channel_info_block').removeClass('chFocus').addClass('chUnFocus');
            $('.channel_info_title').removeClass('chFocusTitle').addClass('chUnFocusTitle');
            $('.channel_info_image').removeClass('chFocusImg').addClass('chUnFocusImg');
            $(this).removeClass('chUnFocus').addClass('chFocus');
            $('.channel_info_title', this).removeClass('chUnFocusTitle').addClass('chFocusTitle');
            $('.channel_info_image', this).removeClass('chUnFocusImg').addClass('chFocusImg');
            
            page$.autosharingSettings.initChannelSet(event.data.channelSetId, event.data.channelSetName);
            
          });
        }
        $('#channel_set_list_ul').append('<div class="clear"/>');
      });
      */
    }
  },
  facebookPages: { },
  init: function(){
    
    $('.pro_check').click(function() {
      var confirmMessage = $('#lang_confirm_goto_setting_page').text();
      if (confirm(confirmMessage.replace('{sns}', $(this).text())) == true) {
        $('#setup').click();
      }
    });
    $('#9x9_rss_tutorial').click(function() {
      alert('9x9 rss feed was not provided');
    });
    $('.promoteInfo').hover(function() {
      $('#pro_hint').show();
    }, function() {
      $('#pro_hint').hide();
    });
    cms.post('/CMSAPI/listSnsAuth', { 'msoId': $('#msoId').val() }, function(snsAuths) {
      for (i in snsAuths) {
        var sns = snsAuths[i];
        switch (snsAuths[i].type) {
          case 1:
          $('input[name="sns_facebook"]').attr('disabled', false).parent().unbind('click').css('color', 'black');
          $('.facebook_label').css('color', 'black');
          if (typeof sns.pages != 'undefined' && sns.pages != null && sns.pages.length > 0) {
            log('pages count: ' + sns.pages.length);
            for (i in sns.pages) {
              var page = sns.pages[i];
              $('<option/>').val(page.id).text(page.name).appendTo('.facebook_select');
              page$.facebookPages[page.id] = page;
            }
            $('.facebook_select').attr('disabled', false);
          }
          break;
          case 2:
          $('input[name="sns_twitter"]').attr('disabled', false).parent().unbind('click').css('color', 'black');
          $('.twitter_label').css('color', 'black');
          break;
          case 3:
          $('input[name="sns_plurk"]').attr('disabled', false).parent().unbind('click').css('color', 'black');
          break;
          case 4:
          $('input[name="sns_sina"]').attr('disabled', false).parent().unbind('click').css('color', 'black');
          break;
          default:
        }
      }
    }, 'json');
    page$.channelAndChannelSetList.init();
  }
};


/**
 * pageStatistics
 */

var page$ = {
  statisticsReport: {
    destroy: function() {
      $('#channel_statistics').hide();
      $('#channel_set_statistics').hide();
      $('.stastics_empty').hide();
      $('.stastics_iframe').hide();
    },
    initChannel: function(channel, statistics) {
      page$.statisticsReport.destroy();
      $('.right_title > div').text(channel.name);
      
      if (channel.piwik == null) {
        $('.stastics_empty').show();
      } else {
        var site = channel.piwik;
        log('site: ' + site);
        var params = {
          module:      'Widgetize',
          action:      'iframe',
          idSite:      site,
          period:      'day',
          date:        'today',
          disableLink: 1
        };
        switch (statistics) {
        case 'overview_with_graph':
          params.moduleToWidgetize = 'VisitsSummary';
          params.actionToWidgetize = 'index';
          break;
        case 'pages_per_visit':
          params.moduleToWidgetize = 'VisitorInterest';
          params.actionToWidgetize = 'getNumberOfVisitsPerPage';
          break;
        case 'page_titles':
          params.moduleToWidgetize = 'Actions';
          params.actionToWidgetize = 'getPageTitles';
          break;
        case 'length_of_visits':
          params.moduleToWidgetize = 'VisitorInterest';
          params.actionToWidgetize = 'getNumberOfVisitsPerVisitDuration';
          break;
        default:
          log('unknown statistics!');
          return;
        }
        var widget_url = piwik_get_widget_url(params);
        log('widget url: ' + widget_url);
        $('.stastics_iframe').attr('src', widget_url).show();
      }
      
      $('#channel_statistics').show();
    }
  },
  channelAndChannelSetList: {
    init: function() {
      // load channels
      var method = 'listOwnedChannels';
      if (cms.isEnterprise()) {
        method = 'defaultNnSetChannels';
      }
      cms.loadJSON('/CMSAPI/' + method + '?msoId=' + $('#msoId').val(), function(channels) {
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
            case 7:
            contentType = 'Slide';
            break;
            case 8:
            contentType = 'Maple Variety';
            break;
            case 9:
            contentType = 'Maple Soap';
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
          channelInfoBlock.find('.channel_info_promoteurl').text(promoteUrl).attr('href', promoteUrl);
          $('<li></li>').append(channelInfoBlock).appendTo('#channel_list_ul');
          // channel info block click event
          channelInfoBlock.click({ 'channel': channels[i] }, function(event) {
            $('.channel_info_block').removeClass('chFocus').addClass('chUnFocus');
            $('.channel_info_title').removeClass('chFocusTitle').addClass('chUnFocusTitle');
            $('.channel_info_image').removeClass('chFocusImg').addClass('chUnFocusImg');
            $(this).removeClass('chUnFocus').addClass('chFocus');
            $('.channel_info_title', this).removeClass('chUnFocusTitle').addClass('chFocusTitle');
            $('.channel_info_image', this).removeClass('chUnFocusImg').addClass('chFocusImg');
            
            $('.channel_info_statistics', this).change();
          });
          channelInfoBlock.find('.channel_info_statistics').change({ 'channel': channels[i] }, function(event) {
            page$.statisticsReport.initChannel(event.data.channel, $(this).val());
          });
        }
        cms.initAddthis();
      });
      // load channel sets
      cms.loadJSON('/CMSAPI/listOwnedChannelSets?msoId=' + $('#msoId').val(), function(channelSets) {
        for (i in channelSets) {
          var channelSetInfoBlock = $('#channel_info_block').clone(true).removeAttr('id').addClass('channel_info_block_cloned');
          
          channelSetInfoBlock.find('.iconStatistics').attr('title', $('#lang_view_statistics').text());
          channelSetInfoBlock.find('.channel_info_title div').text(channelSets[i].name);
          $('<img/>').attr('src', channelSets[i].imageUrl).appendTo(channelSetInfoBlock.find('.channel_info_image'));
          channelSetInfoBlock.find('.channel_info_contenttype span').text($('#lang_label_channel_set').text());
          channelSetInfoBlock.find('.channel_info_programcount span').text('N/A');
          channelSetInfoBlock.find('.channel_info_subscribers span').text(channelSets[i].subscriptionCnt);
          channelSetInfoBlock.find('.channel_info_updatedate span').text(cms.formatDate(channelSets[i].updateDate));
          channelSetInfoBlock.find('.channel_info_subscribers').hide();
          // add this
          var promoteUrl = 'http://' + location.host + '/';
          promoteUrl += ((channelSets[i].beautifulUrl != null) ? channelSets[i].beautifulUrl : channelSets[i].defaultUrl);
          channelSetInfoBlock.find('.addthis_button_expanded').attr('addthis:url', promoteUrl + '?_=' + channelSets[i].updateDate);
          var switchObject = channelSetInfoBlock.find('.channel_info_publish');
          if (channelSets[i]['public']) {
            switchObject.removeClass('chUnPublic').addClass('chPublic');
          } else {
            switchObject.removeClass('chPublic').addClass('chUnPublic');
          }
          channelSetInfoBlock.find('.channel_info_promoteurl').text(promoteUrl).attr('href', promoteUrl);
          $('<li></li>').append(channelSetInfoBlock).appendTo('#channel_set_list_ul');
          // channel info block click event
          channelSetInfoBlock.click({ 'channelSet': channelSets[i] }, function(event) {
            $('.channel_info_block').removeClass('chFocus').addClass('chUnFocus');
            $('.channel_info_title').removeClass('chFocusTitle').addClass('chUnFocusTitle');
            $('.channel_info_image').removeClass('chFocusImg').addClass('chUnFocusImg');
            $(this).removeClass('chUnFocus').addClass('chFocus');
            $('.channel_info_title', this).removeClass('chUnFocusTitle').addClass('chFocusTitle');
            $('.channel_info_image', this).removeClass('chUnFocusImg').addClass('chFocusImg');
            
            $('.channel_info_statistics', this).change();
          });
          channelSetInfoBlock.find('.channel_info_statistics').change({ 'channelSet': channelSets[i] }, function(event) {
            page$.statisticsReport.initChannel(event.data.channelSet, $(this).val());
          });
        }
        cms.initAddthis();
        $('#channel_set_list_ul').append('<div class="clear"/>');
		var pivot = $('.channel_info_block_cloned').get(0);
		$(pivot).click();
      });
    }
  },
  initGenericOne: function() {
    $('#channel_set_title').hide();
  },
  init: function() {
    var css = '<style> .chPublic { background:url(' + $('#image_ch_public').text() + ') no-repeat; }\n.chUnPublic { background:url(' + $('#image_ch_unpublic').text() + ') no-repeat; } </style>';
    $(css).appendTo('head');
    
    require(['../piwik-analytics'], function() {
      page$.channelAndChannelSetList.init();
    });
  }
};


/**
 *
 */

var page$ = {
  objDirectoryTree: {
    init: function() {
      $('.plus-button').live('click', function() {
        cms.post('/CMSAPI/channelInfo', { 'channelId': $(this).attr('alt') }, function(channel) {
          if (channel != null)
            page$.objChannelSetArea.appendChannel(channel);
        }, 'json');
        return false;
      });
      $('.play-button').live('click', function() {
        window.open('/view?channel=' + $(this).attr('alt'), '_player');
        return false;
      });
      cms.loadJSON('/CMSAPI/systemCategories', function(categories) {
        var nodes = [ ];
        for (var i in categories) {
          var category = categories[i];
          var node = {
            'title':    '&nbsp;' + category.name,
            'key':      category.id,
            'isFolder': true,
            'isLazy':   true,
            'type':     'category'
          };
          if (category.lang == 'en') {
        	  node.icon = 'en.gif';
          } else {
        	  node.icon = 'ch.gif';
          }
          nodes.push(node);
        }
        $('#treeview').dynatree({
          noLink: true,
          children: nodes,
          onLazyRead: function(node) {
            if (node.data.isFolder && node.data.type == 'category') {
              cms.post('/CMSAPI/listCategoryNnSets', { 'categoryId': node.data.key, 'isPublic': true }, function(channelSets) {
                for (var i in channelSets) {
                  var channelSet = channelSets[i];
                  var channelSetId = channelSet.id;
                  if (channelSet.name == null) continue;
                  var child = {
                    'title': '&nbsp;' + cms.escapeHtml(channelSet.name),
                    'key':   channelSetId,
                    'isFolder': true,
                    'isLazy':   true,
                    'type':     'set'
                  };
                  node.addChild(child);
                }
                node.setLazyNodeStatus(DTNodeStatus_Ok);
              }, 'json');
            } else if (node.data.isFolder && node.data.type == 'set') {
              cms.post('/CMSAPI/defaultNnSetChannels', { 'setId': node.data.key, 'isGood': true }, function(channels) {
                for (var i in channels) {
                  var channel = channels[i];
                  var channelId = channel.id;
                  if (channel.name == null) continue;
                  var child = {
                    'title': cms.escapeHtml(channel.name) + ' <img alt="' + channelId + '" class="tiny-button plus-button" src="' + cms.getExternalRootPath() + '/images/cms/plus.png"> <img alt="' + channelId + '" class="tiny-button play-button" src="' + cms.getExternalRootPath() + '/images/cms/play.png">',
                    'key':   channelId,
                    'isFolder': false,
                    'isLazy':   false,
                    'type':     'channel'
                  };
                  node.addChild(child);
                }
                node.setLazyNodeStatus(DTNodeStatus_Ok);
              }, 'json');
            }
          }
        });
      });
    }
  },
  objYouTubeTab: {
    init: function() {
      $('#youtube_button').click(function() {
        var sourceUrl = $('#youtube_input').val();
        if (sourceUrl == "") {
          alert($('#lang_channel_source_is_empty').text());
          return;
        } else if (sourceUrl.indexOf(location.host) > 0) {
          var channelId = sourceUrl.match(/channel=([0-9]+)/)[1];
          cms.post('/CMSAPI/channelInfo', { 'channelId': channelId }, function(channel) {
            if (channel == null) {
              alert($('#lang_channel_source_is_wrong').text());
              return;
            }
            page$.objChannelSetArea.appendChannel(channel);
          }, 'json');
        } else {
          cms.post('/CMSAPI/importChannelByUrl', { 'sourceUrl': sourceUrl }, function(channel) {
            if (channel == null) {
              alert($('#lang_channel_source_is_wrong').text());
              return;
            }
            page$.objChannelSetArea.appendChannel(channel);
          }, 'json');
        }
      });
    }
  },
  objSearchTab: {
    funcPopulateSearchBox: function(channels) {
      
      $('#result_list li').remove(); // clean previous result
      $('#no_search_result').hide();
      
      if (channels.length == 0) {
        $('#no_search_result').show();
        return;
      }
      
      for(var i = 0; i < channels.length; i++) {
        var channel = channels[i];
        
        var item = $('<li class="ch_normal"/>');
        var btnPlay = $('<a/>').attr('target', '_player');
        btnPlay.attr('href', cms.getChannelUrl(channel.id));
        btnPlay.append('<p class="btnPlay"/>');
        btnPlay.appendTo(item);
        
        var btnAdd = $('<p class="btnAdd"/>');
        btnAdd.click({ 'channel': channel }, function(event) {
          page$.objChannelSetArea.appendChannel(event.data.channel);
        });
        btnAdd.appendTo(item);
        
        $('<input name="channelId" type="hidden"/>').val(channel.id.toString());
        //$('<span/>').html(page$.populateBubbleContent(channel)).hide().appendTo();
        
        $('<img/>').attr('src', channel.imageUrl).appendTo(item);
        $('<p/>').text(channel.name).appendTo(item);
        $('#result_list').append(item);
      }
      
      // page$.objSearchTab.initBubbles();
    },
    initBubbles: function() {
      //$('#result_list .ch_normal').each(function() {
      //  cms.bubblePopupProperties['innerHtml'] = $(this).find('span').html();
      //  $(this).CreateBubblePopup(cms.bubblePopupProperties);
      //});
    },
    init: function() {
      var tab$ = page$.objSearchTab;
      
      $('#search_button').click(function() {
        var parameters = {
          'text': $('#search_input').val()
        };
        cms.post('/CMSAPI/searchChannel', parameters, function(channels) {
          tab$.funcPopulateSearchBox(channels);
        }, 'json');
      });
      
      $('#result_list li').remove();
      $('#no_search_result').hide();
    }
  },
  objChannelSetInfo: {
    init: function() {
      cms.loadJSON('/CMSAPI/systemCategories', function(categories) {
        $('#sys_directory option[value!="0"]').remove();
        for (var i = 0; i < categories.length; i++) {
          var icon = cms.getExternalRootPath() + '/javascripts/plugins/dynatree/skin/ch.gif';
          if (categories[i].lang == 'en') {
            icon = cms.getExternalRootPath() + '/javascripts/plugins/dynatree/skin/en.gif';
          }
          $('<option/>')
            .attr('value', categories[i].id)
            .text(categories[i].name)
            .attr('title', icon)
            .appendTo('#sys_directory');
        }
        cms.post('/CMSAPI/defaultNnSetCategory', { 'msoId': $('#msoId').val() }, function(category) {
          if (category != null) {
            $('#sys_directory').val(category.id);
          }
          $('#sys_directory').msDropDown();
        }, 'json');
      });
      cms.post('/CMSAPI/defaultNnSetInfo', { 'msoId': $('#msoId').val() }, function(channelSet) {
        if (channelSet != null) {
          var url = 'http://' + ((location.host == 'www.9x9.tv') ? '9x9.tv' : location.host) + '/';
          url += ((channelSet.beautifulUrl != null) ? channelSet.beautifulUrl : channelSet.defaultUrl);
          if (channelSet.beautifulUrl != null || channelSet.defaultUrl != null) {
            $('#channel_set_promote_url').text(url).attr('href', url);
            $('.addthis_button_expanded').attr('addthis:url', url + '?_=' + channelSet.updateDate);
            cms.initAddthis();
          }
          $('#cc_name').val(channelSet.name);
          $('#cc_tag').val(channelSet.tag);
          $('#cc_language').val(channelSet.lang);
          $('#cc_intro').text(channelSet.intro);
          if (channelSet.imageUrl != null) {
            $('#cc_image').attr('src', channelSet.imageUrl);
          }
          $('#cc_image_updated').val('false');
          $('#cc_id').val(channelSet.id);
          var swfupload_settings = {
            flash_url:              cms.getExternalRootPath() + '/javascripts/swfupload/swfupload.swf',
            upload_url:             cms.getS3UploadBucket(),
            file_size_limit:        '10 MB',
            file_types:             cms.imageTypes,
            file_types_description: 'Image Files',
            file_post_name:         'file',
            button_placeholder:     $('#upload_image').get(0),
            button_image_url:       $('#image_btn_upload').text(),
            button_width:           '95',
            button_height:          '32',
            button_cursor:          SWFUpload.CURSOR.HAND,
            button_window_mode:     SWFUpload.WINDOW_MODE.OPAQUE,
            button_action:          SWFUpload.BUTTON_ACTION.SELECT_FILE,
            debug:                  false,
            http_success:           [ 201 ],
            upload_success_handler: function(file, serverData, recievedResponse) {
              if (!file.type)
                file.type = cms.getFileTypeByName(file.name);
              $('#uploading').hide();
              $('#cc_image').attr('src', cms.getS3UploadBucket() + '/ch_set_logo_' + channelSet.id + '_' + file.size + file.type);
              $('#cc_image_updated').val('true');
            },
            upload_error_handler: function(file, code, message) {
              $('#uploading').hide();
              alert('error: ' + message);
            },
            file_queued_handler: function(file) {
              if (!file.type)
                file.type = cms.getFileTypeByName(file.name);
              var post_params = {
                'AWSAccessKeyId': $('#s3_id').val(),
                'key':            'ch_set_logo_' + channelSet.id + '_' + file.size + file.type,
                'acl':            'public-read',
                'policy':         $('#s3_policy').val(),
                'signature':      $('#s3_signature').val(),
                'content-type':   cms.getContentTypeByFileExtention(file.type),
                'success_action_status': '201'
              };
              this.setPostParams(post_params);
              this.startUpload(file.id);
              $('#uploading').show();
            }
          };
          var swfu = new SWFUpload(swfupload_settings);
        }
      }, 'json');
    }
  },
  objChannelSetArea: {
    adjustWidth: function() {
      var size = $('#set_ch_list li').size();
      $('#set_ch_list').width(size * 112);
    },
    composeChannelSetItem: function(channel) {
      var item = $('<li class="ch_normal"/>');
      var btnRemove = $('<p class="btnRemove"/>');
      btnRemove.click(function() {
        $(this).parent().remove();
        page$.objChannelSetArea.adjustWidth();
        page$.dirty = true;
      });
      btnRemove.appendTo(item);
      $('<img/>').attr('src', channel.imageUrl).appendTo(item);
      $('<p/>').text(channel.name).appendTo(item);
      $('<input type="hidden" name="channelId"/>').val(channel.id).appendTo(item);
      $('<input type="hidden" name="tag"/>').val(channel.tag).appendTo(item);
      return item;
    },
    appendChannel: function(channel, suppress) {
      var arrChannelsInSet = $('#set_ch_list li').toArray();
      for (var i in arrChannelsInSet) {
        var found = $(arrChannelsInSet[i]);
        var channelId = found.find('input[name="channelId"]').val();
        if (channelId == channel.id) {
          alert($('#lang_warning_channel_is_already_in').text());
          $('#set_ch_holder').scrollTo(found, 800);
          found.effect('highlight', { }, 3000);
          return;
        }
      }
      if ($('#set_ch_list li').size() >= 27 && !suppress) {
        alert($('#lang_warning_reached_maximum_amount').text());
        return;
      }
      var item = this.composeChannelSetItem(channel);
      $('#set_ch_list').append(item);
      this.adjustWidth();
      if ($('#set_ch_list li').size() == 1) {
        $('#set_ch_list, #result_list').sortable({
          connectWith: '.connectedSortable',
          placeholder: 'empty',
          talerance:   'pointer',
          update: function() {
            page$.dirty = true;
          }
        }).disableSelection();
      }
      if (!suppress) {
        //alert($('#lang_channel_had_been_added').text());
        $('#set_ch_holder').scrollTo(item, 800);
        item.effect('highlight', { }, 3000);
        page$.dirty = true;
      }
    },
    init: function() {
      $('#set_ch_list .ch_normal').remove();
      cms.post('/CMSAPI/defaultNnSetChannels', { 'msoId': $('#msoId').val() }, function(channels) {
        var area$ = page$.objChannelSetArea;
        if (channels.length == 0) {
          area$.adjustWidth();
          return;
        }
        for (var i in channels) {
          area$.appendChannel(channels[i], true);
        }
        page$.dirty = false;
      }, 'json');
    }
  },
  populateBubbleContent: function(channel) {
    var span = $('<span></span>');
    var h2 = $('<h2 class="popTitle"></h2>').text(channel.name)/*.textTruncate(20, '...')*/.appendTo(span);
    var updateDate = new Date(channel.updateDate);
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
    
    var innerHtml = span.html();
    var label_program_count = $('#lang_label_program_count').text();
    var label_update_time = $('#lang_label_update_time').text();
    innerHtml += '<br/>' + label_program_count + ' : ' + channel.programCnt;
    innerHtml += '<br/>' + label_update_time + ' : ' + year + '/' + month + '/' + date + '&nbsp;' + hour + ':' + minute + ':' + second;
    
    return innerHtml;
  },
  funcPublishChannelSet: function() {
    var categoryId = $('#sys_directory').val();
    if (categoryId == 0) {
      alert($('#lang_warning_select_category').text());
      return;
    }
    var intro = $('#cc_intro').val();
    if (intro.length > 200) {
      alert($('#lang_warning_intro_over_limitation').text());
      return;
    }
    var name = $('#cc_name').val();
    if (name.length > 40) {
      alert($('#lang_warning_name_over_limitation').text());
      return;
    } else if (name.length == 0) {
      alert($('#lang_warning_empty_name').text());
      return;
    }
    var tag = $('#cc_tag').val();
    if (tag.length > 200) {
      alert($('#lang_warning_tag_over_limitation').text());
      return;
    }
    var language = $('#cc_language').val();
    var imageUrl = $('#cc_image').attr('src');
    var imageUpdated = $('#cc_image_updated').val();
    if (imageUrl.length == 0 || imageUrl == cms.getExternalRootPath() + '/images/cms/upload_img.jpg') {
      alert($('#lang_warning_empty_logo').text());
      return;
    }
    var items = $('#set_ch_list li').toArray();
    var channelIds = '';
    var titleNewChannels = '';
    for (var i = 0; i < items.length; i++) {
      if (channelIds != '')
        channelIds += ',';
      var channelId = $(items[i]).find('input[name="channelId"]').val();
      var title = $(items[i]).find('p').text();
      var chTag = $(items[i]).find('input[name="tag"]').val();
      if (chTag == 'NEW_CHANNEL') { // hacks
        if (titleNewChannels != '') {
          titleNewChannels += ', ';
        }
        titleNewChannels += title;
      }
      channelIds += channelId;
    }
    if (channelIds == '') {
      alert($('#lang_warning_set_is_empty').text());
      return;
    }
    log(channelIds);
    
    if (titleNewChannels != '') {
      if (titleNewChannels.indexOf(',') > 0) {
        alert(titleNewChannels + ' ' + $('#lang_warning_new_channels_will_be_reviewed').text());
      } else {
        alert(titleNewChannels + ' ' + $('#lang_warning_new_channel_will_be_reviewed').text());
      }
    }
    
    var parameters = {
      'setId':        $('#cc_id').val(),
      'channelIds':   channelIds,
      'name':         name,
      'intro':        intro,
      'tag':          tag,
      'lang':         language,
      'categoryId':   categoryId
    };
    if (imageUpdated == 'true') {
      parameters['imageUrl'] = imageUrl;
    }
    cms.post('/CMSAPI/saveChannelSet', parameters, function(response) {
      if (response != 'OK')
        alert($('#lang_warning_error_occurs').text());
      else
        alert($('#lang_update_successfully').text());
      page$.objChannelSetArea.init();
      page$.objChannelSetInfo.init();
      page$.dirty = false;
    }, 'text');
  },
  dirty: false,
  init: function() {
    $('.ch_bg').css('background', 'url(' + $('image_bg_album').text() + ') no-repeat;');
    
    page$.objChannelSetInfo.init();
    page$.objSearchTab.init();
    page$.objYouTubeTab.init();
    page$.objChannelSetArea.init();
    page$.objDirectoryTree.init();
    
    $('#publish_button').click(page$.funcPublishChannelSet);
    
    (function(target) { // select first tab
      tab="#"+target;
      $(tab).addClass("on");
      content="#"+target+"_content";
      $(content).addClass("on");
    })('category');
    
    $("#pool_tabs li").click(function() {
      $("#pool_tabs li, .tab_content").removeClass("on");
      $(this).addClass("on");
      target = "#"+$(this).attr("id")+"_content";
      $(target).addClass("on");
    });
    
    $(window).bind('beforeunload', function() {
      if (page$.dirty) {
        return $('#lang_warning_channel_has_not_been_saved').text();
      }
      return;
    });
  }
};


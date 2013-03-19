/**
 * 
 */

var page$ = {
  overallLayout: {
    destroyRightSideContent: function(cleanup) {
      /* not to confirm program creation
      if (cleanup == false) {
        if ($('.program_create_detail_block_cloned').size() > 0) {
          if (confirm($('#lang_confirm_leaving_program_creation').text()) == false) {
            return false;
          }
        }
      }
      */
      // hide right side content
      page$.channelDetail.destroy();
      page$.programDetail.destroy();
      page$.programList.destroy();
      $('#choose_channel_type').hide();
      $('#publish_button').css('background-image', 'url(' + cms.getExternalRootPath() + '/images/cms/graybtn.png)').html('<span>' + $('#image_btn_reorder').text() + '</span>');
      $('.program_info_removebutton').css('background-image', 'url(' + cms.getExternalRootPath() + '/images/cms/btnClose.png)').css('cursor', 'pointer');
      $('.program_info_block_cloned').css('cursor', 'pointer');
      return true;
    }
  },
  skeletonCreation: {
    create9x9Channel: function() {
      cms.post('/CMSAPI/createChannelSkeleton', { }, function(channelId) {
        $('<li/>').append('<div class="chShadow channel_info_block_cloned"><div class="chShadowTitle"></div><div class="chImg"></div></div>')
          .prependTo('#channel_list_ul');
        page$.channelDetail.init(channelId, true);
      }, 'json');
    },
    create9x9Program: function(channelId, channelName) {
      cms.post('/CMSAPI/createProgramSkeleton', { }, function(programId) {
        log('new program: ' + programId);
        if (programId != null)
          page$.programDetail.programCreation(programId, channelId, channelName);
      }, 'json');
    },
    createRadioProgram: function(channelId, channelName) {
      cms.post('/CMSAPI/createProgramSkeleton', { contentType: 3 }, function(programId) {
        log('new program: ' + programId);
        if (programId != null)
          page$.programDetail.radioProgramCreation(programId, channelId, channelName);
      }, 'json');
    }
  },
  programDetail: {
    swfObjectImage: [],
    swfObjectVideo: [],
    destroy: function() {
      for (i in this.swfObjectImage) {
        this.swfObjectImage[i].destroy();
      }
      for (i in this.swfObjectVideo) {
        this.swfObjectVideo[i].destroy();
      }
      this.swfObjectVideo = [];
      this.swfObjectImage = [];
      $('#program_detail').hide();
      $('#program_detail_readonly').hide();
      $('#program_create_detail').hide();
      $('.program_create_detail_block_cloned').each(function() {
        $(this).parent().remove();
      });
    },
    init: function(programId, readonly) {
      cms.post('/CMSAPI/programInfo', { 'programId': programId }, function(program) {
        if (readonly) {
          $('#program_list_readonly').hide();
          page$.programDetail.displayProgramReadonly(program);
        } else {
          $('#program_list').hide();
          page$.programDetail.displayProgram(program);
        }
      }, 'json');
    },
    initYouTube: function(url, channelId) {
      cms.get(url, { 'alt': 'json-in-script' }, function(data) {
        if (typeof data.entry != 'undefined') {
          $('#program_list').hide();
          page$.programDetail.displayYouTube(data.entry, channelId);
        }
      }, 'json');
    },
    displayYouTube: function(entry, channelId) {
      var programDetailBlock = $('#program_detail_readonly');
      var title = entry.media$group.media$title.$t;
      if (title.length > 20) {
        title = title.substring(0, 20) + '...';
      }
      programDetailBlock.find('.right_title div').text(title);
      programDetailBlock.find('.ep_name').text(entry.media$group.media$title.$t);
      programDetailBlock.find('.ep_intro').text(entry.media$group.media$description.$t);
      var promoteUrl = 'http://' + location.host + '/view?channel=' + channelId + '&episode=' + entry.media$group.yt$videoid.$t;
      programDetailBlock.find('.ep_url').attr('href', promoteUrl).text(promoteUrl);
      programDetailBlock.find('.ep_image').attr('src', entry.media$group.media$thumbnail[1].url);
      programDetailBlock.find('.ep_createdate').text(entry.published.$t.substring(0, 19).replace('T', ' ').replace(/-/g, '/'));
      programDetailBlock.find('.ep_return').unbind().click(function() {
        page$.programDetail.destroy();
        $('#program_list_readonly').show();
      });
      programDetailBlock.find('.ep_source').text('YouTube').attr('href', entry.link[0].href);
      $('#program_list_readonly').hide();
      $('#program_detail_readonly').show();
    },
    displayProgram: function(program) {
      var programDetailBlock = $('#program_detail');
      var programId = program.id;
      var title = program.name;
      if (title.length > 20) {
        title = title.substring(0, 20) + '...';
      }
      var source = "javascript:";
      if (program.fileUrl != null)
        source = program.fileUrl;
      else if (program.audioFileUrl != null)
        source = program.audioFileUrl;
      programDetailBlock.find('.ep_source a').attr('href', source);
      programDetailBlock.find('.ep_name').attr('disabled', false);
      programDetailBlock.find('.ep_intro').attr('disabled', false);
      programDetailBlock.find('.ep_comment_block').hide();
      switch(program.contentType) {
        case 0:
        programDetailBlock.find('.ep_source a').text('9x9');
        break;
        case 1:
        programDetailBlock.find('.ep_source a').text('YouTube');
        // youtube comment
        programDetailBlock.find('.ep_name').attr('disabled', true);
        programDetailBlock.find('.ep_intro').attr('disabled', true);
        programDetailBlock.find('.ep_comment_block').show();
        break;
        case 2:
        programDetailBlock.find('.ep_source a').text('SlideShow');
        break;
        case 3:
        programDetailBlock.find('.ep_source a').text('Audio');
        break;
        default:
        programDetailBlock.find('.ep_source a').text('Unknown');
      }
      programDetailBlock.find('.right_title div').text(title);
      programDetailBlock.find('.ep_name').val(program.name);
      programDetailBlock.find('.ep_intro').val(program.intro);
      programDetailBlock.find('.ep_comment').val(program.comment);
      var promoteUrl = 'http://' + location.host + '/view?channel=' + program.channelId + '&episode=' + programId;
      programDetailBlock.find('.ep_url').attr('href', promoteUrl).text(promoteUrl);
      programDetailBlock.find('.ep_image').attr('src', program.imageUrl);
      programDetailBlock.find('.ep_image_updated').val('false');
      programDetailBlock.find('.ep_createdate').text(cms.formatDate(program.createDate));
      programDetailBlock.find('.ep_return').unbind().click(function() {
        page$.programDetail.destroy();
        $('#program_list').show();
      });
      programDetailBlock.find('.ep_cancel').unbind().click(function() {
        page$.programDetail.destroy();
        $('#program_list').show();
      });
      programDetailBlock.find('.ep_savebutton').css('width', 80).unbind().click(function() {
        if (programDetailBlock.find('.ep_name').val() == "") {
          alert($('#lang_warning_empty_name').text());
          return;
        }
        var parameters = {
          'programId': programId,
          'name':      programDetailBlock.find('.ep_name').val(),
          'intro':     programDetailBlock.find('.ep_intro').val(),
          'comment':   programDetailBlock.find('.ep_comment').val()
        };
        if (programDetailBlock.find('.ep_image_updated').val() == 'true') {
          parameters['imageUrl'] = programDetailBlock.find('.ep_image').attr('src');
        }
        cms.post('/CMSAPI/saveProgram', parameters, function(response) {
          if (response != 'OK') {
            alert($('#lang_warning_error_occurs').text());
          } else {
            alert($('#lang_update_successfully').text());
            page$.programList.init(program.channelId, false, $('#program_list .right_title div').text());
          }
        }, 'text');
      });
      $('<span/>').addClass('ep_upload_image').appendTo(programDetailBlock.find('.upload_button_place'));
      var swfupload_settings = {
        flash_url:              cms.getExternalRootPath() + '/javascripts/swfupload/swfupload.swf',
        upload_url:             cms.getS3UploadBucket(),
        file_size_limit:        '10240',
        file_types:             cms.imageTypes,
        file_types_description: 'Image Files',
        file_post_name:         'file',
        button_placeholder:     programDetailBlock.find('.ep_upload_image').get(0),
        button_action:          SWFUpload.BUTTON_ACTION.SELECT_FILE,
        button_image_url:       $('#image_btn_upload').text(),
        button_width:           '95',
        button_height:          '32',
        button_cursor:          SWFUpload.CURSOR.HAND,
        button_window_mode :    SWFUpload.WINDOW_MODE.OPAQUE,
        debug:                  false,
        http_success :          [ 201 ],
        upload_success_handler: function(file, serverData, recievedResponse) {
          if (!file.type)
            file.type = cms.getFileTypeByName(file.name);
          programDetailBlock.find('.ep_uploading_image').hide();
          programDetailBlock.find('.ep_image').attr('src', cms.getS3UploadBucket() + '/prog_logo_' + programId + '_' + file.size + file.type);
          programDetailBlock.find('.ep_image_updated').val('true');
        },
        upload_error_handler: function(file, code, message) {
          programDetailBlock.find('.ep_uploading_image').hide();
          alert(message);
        },
        file_queued_handler: function(file) {
          if (!file.type)
            file.type = cms.getFileTypeByName(file.name);
          var post_params = {
            "AWSAccessKeyId": $('#s3_id').val(),
            "key":            'prog_logo_' + programId + '_' + file.size + file.type,
            "acl":            "public-read",
            "policy":         $('#s3_policy').val(),
            "signature":      $('#s3_signature').val(),
            "content-type":   cms.getContentTypeByFileExtention(file.type),
            "success_action_status": "201"
          };
          this.setPostParams(post_params);
          this.startUpload(file.id);
          programDetailBlock.find('.ep_uploading_image').show();
        }
      };
      var swfObjectImage = new SWFUpload(swfupload_settings);
      page$.programDetail.swfObjectImage.push(swfObjectImage);
      $('#program_detail').show();
    },
    displayProgramReadonly: function(program) {
      var programDetailBlock = $('#program_detail_readonly');
      var programId = program.id;
      var title = program.name;
      if (title.length > 20) {
        title = title.substring(0, 20) + '...';
      }
      programDetailBlock.find('.right_title div').text(title);
      programDetailBlock.find('.ep_name').text(program.name);
      programDetailBlock.find('.ep_intro').text(program.intro);
      var promoteUrl = 'http://' + location.host + '/view?channel=' + program.channelId + '&episode=' + programId;
      programDetailBlock.find('.ep_url').attr('href', promoteUrl).text(promoteUrl);
      programDetailBlock.find('.ep_image').attr('src', program.imageUrl);
      programDetailBlock.find('.ep_createdate').text(cms.formatDate(program.createDate));
      programDetailBlock.find('.ep_return').unbind().click(function() {
        page$.programDetail.destroy();
        $('#program_list_readonly').show();
      });
      var source = "N/A";
      if (program.fileUrl != null)
        source = program.fileUrl;
      else if (program.audioFileUrl != null)
        source = program.audioFileUrl;
      var sourceName = 'Unknown';
      switch(program.contentType) {
        case 0:
        sourceName = 'Podcast';
        break;
        case 1:
        sourceName = 'YouTube';
        break;
        default:
      }
      programDetailBlock.find('.ep_source').text(sourceName).attr('href', source);
      $('#program_list_readonly').hide();
      $('#program_detail_readonly').show();
    },
    radioProgramCreation: function(programId, channelId, channelName) {
      $('#program_create_detail .right_title div').text(channelName);
      $('#program_create_detail .ep_return').unbind().click(function() {
        page$.programList.init(channelId, false, channelName);
      });
      cms.post('/CMSAPI/programInfo', { 'programId': programId }, function(program) {
        var programDetailBlock = $('#radio_program_create_detail_block')
                                   .clone(true)
                                   .removeAttr('id')
                                   .addClass('program_create_detail_block_cloned');
        var programId = program.id;
        log(program);
        
        programDetailBlock.find('.ep_urlbutton').click(function() {
          programDetailBlock.find('.ep_audio_cancel a').click();
          programDetailBlock.find('.ep_url_block').show();
          programDetailBlock.find('.ep_localdrive_block').hide();
        });
        
        programDetailBlock.find('.ep_localdrive').click(function() {
          programDetailBlock.find('.ep_url_block').hide();
          programDetailBlock.find('.ep_localdrive_block').show();
        });
        
        programDetailBlock.find('.ep_image_urlradio').click(function() {
          programDetailBlock.find('.best_resolution_from_url').show();
          programDetailBlock.find('.best_resolution_from_disk').hide();
          programDetailBlock.find('.ep_image_url_block').show();
          programDetailBlock.find('.ep_image_localdrive_block').hide();
        });
        
        programDetailBlock.find('.ep_image_localdrive').click(function() {
          programDetailBlock.find('.best_resolution_from_url').hide();
          programDetailBlock.find('.best_resolution_from_disk').show();
          programDetailBlock.find('.ep_image_url_block').hide();
          programDetailBlock.find('.ep_image_localdrive_block').show();
        });
        
        programDetailBlock.find('.ep_name').val(program.name);
        programDetailBlock.find('.ep_image').attr('src', program.imageUrl);
        programDetailBlock.find('.ep_image_updated').val('false');
        programDetailBlock.find('.ep_intro').val(program.intro);
        
        programDetailBlock.find('.ep_url_input').val($('#lang_audio_url_example').text()).css('color', '#999');
        programDetailBlock.find('.ep_url_input').focus(function() {
          programDetailBlock.find('.ep_url_input').css('color', '#000');
          var audioUrl = programDetailBlock.find('.ep_url_input').val();
          if (audioUrl == $('#lang_audio_url_example').text()) {
            programDetailBlock.find('.ep_url_input').val('');
          }
        });
        programDetailBlock.find('.ep_url_input').blur(function() {
          var audioUrl = programDetailBlock.find('.ep_url_input').val();
          if (audioUrl == '') {
            programDetailBlock.find('.ep_url_input').val($('#lang_audio_url_example').text()).css('color', '#999');
          }
        });
        
        programDetailBlock.find('.ep_image_url_input').val($('#lang_image_url_example').text()).css('color', '#999');
        programDetailBlock.find('.ep_image_url_input').focus(function() {
          programDetailBlock.find('.ep_image_url_input').css('color', '#000');
          var imageUrl = programDetailBlock.find('.ep_image_url_input').val();
          if (imageUrl == $('#lang_image_url_example').text()) {
            programDetailBlock.find('.ep_image_url_input').val('');
          }
        });
        programDetailBlock.find('.ep_image_url_input').blur(function() {
          var imageUrl = programDetailBlock.find('.ep_image_url_input').val();
          if (imageUrl == '') {
            programDetailBlock.find('.ep_image_url_input').val($('#lang_image_url_example').text()).css('color', '#999');
          } else {
            programDetailBlock.find('.ep_image').attr('src', imageUrl);
          }
        });
        
        $('#program_create_detail').show();
        $('<li></li>').append(programDetailBlock).appendTo('#program_create_ul');
        
        // audio uploading
        var swfupload_settings = {
          flash_url:              cms.getExternalRootPath() + '/javascripts/swfupload/swfupload.swf',
          upload_url:             cms.getS3UploadBucket(),
          file_size_limit:        '300 MB',
          file_types:             cms.audioTypes,
          file_types_description: 'Audio Files',
          file_post_name:         'file',
          button_placeholder:     programDetailBlock.find('.ep_upload_audio').get(0),
          button_action:          SWFUpload.BUTTON_ACTION.SELECT_FILE,
          button_image_url:       $('#image_btn_from_disk').text(),
          button_width:           '95',
          button_height:          '32',
          button_cursor:          SWFUpload.CURSOR.HAND,
          button_window_mode :    SWFUpload.WINDOW_MODE.OPAQUE,
          debug:                  false,
          http_success :          [ 201 ],
          upload_progress_handler: function(file, completed, total) {
            if (completed % 10 > 1 && completed != total) {
              return;
            }
            var remaining = file.timeRemaining.toString();
            var eta;
            if (remaining == 0) {
              eta = '--';
            } else {
              eta = remaining.substring(0, remaining.indexOf('.')) + ' s';
            }
            programDetailBlock.find('.ep_uploading_audio .progress').progressBar((completed * 100) / total);
            programDetailBlock.find('.ep_uploading_audio .eta').html('&nbsp;&nbsp;ETA: ' + eta).show();
          },
          upload_success_handler: function(file, serverData, recievedResponse) {
            if (!file.type)
              file.type = cms.getFileTypeByName(file.name);
            programDetailBlock.find('.ep_url_input').val(cms.getS3UploadBucket() + '/prog_audio_' + programId + file.type);
            programDetailBlock.find('.ep_audio_cancel').remove();
            this.setButtonDisabled(false);
          },
          upload_error_handler: function(file, code, message) {
            log('upload audio error: ' + code + ', message: ' + message);
            programDetailBlock.find('.ep_audio_cancel').remove();
            if (code == -280) {
              programDetailBlock.find('.ep_uploading_audio .progress').text('').hide();
              programDetailBlock.find('.ep_uploading_audio .eta').text('').hide();
              programDetailBlock.find('.ep_uploading_audio .filename').text('').hide();
              try {
                this.setButtonDisabled(false);
              } catch(e) { }
              programDetailBlock.find('.audio_localdrive_hint').show();
              return;
            }
            this.setButtonDisabled(false);
            programDetailBlock.find('.ep_uploading_audio .eta').html('&nbsp;' + $('#lang_upload_failed').text()).show();
            alert(message);
          },
          file_queued_handler: function(file) {
            if (!file.type)
              file.type = cms.getFileTypeByName(file.name);
            var post_params = {
              "AWSAccessKeyId": $('#s3_id').val(),
              "key":            'prog_audio_' + programId + file.type,
              "acl":            "public-read",
              "policy":         $('#s3_policy').val(),
              "signature":      $('#s3_signature').val(),
              "content-type":   cms.getContentTypeByFileExtention(file.type),
              "success_action_status": "201"
            };
            log(file);
            log('start upload audio');
            log(post_params);
            this.setPostParams(post_params);
            this.startUpload(file.id);
            this.setButtonDisabled(true);
            programDetailBlock.find('.ep_uploading_audio .progress').text('').progressBar({
              barImage: cms.getExternalRootPath() + '/images/cms/progressbg_black.gif'
            }).show();
            programDetailBlock.find('.ep_url_input').val('').css('color', '#000');
            programDetailBlock.find('.ep_url_block').hide();
            programDetailBlock.find('.audio_localdrive_hint').hide();
            $('<span class="floatL ep_audio_cancel"><a onclick="return false;" href="javascript:" class="ui-icon ui-icon-cancel"></a></span>')
              .click({ 'swfupload': this, 'file': file }, function(event) {
                event.data.swfupload.cancelUpload(event.data.file.id);
              }).prependTo(programDetailBlock.find('.ep_uploading_audio'));
            programDetailBlock.find('.ep_uploading_audio .filename').text(file.name).show();
            
         }
        };
        var swfObject = new SWFUpload(swfupload_settings);
        page$.programDetail.swfObjectVideo.push(swfObject);
        
        // logo uploading
        var swfupload_settings = {
          flash_url:              cms.getExternalRootPath() + '/javascripts/swfupload/swfupload.swf',
          upload_url:             cms.getS3UploadBucket(),
          file_size_limit:        '10 MB',
          file_types:             cms.imageTypes,
          file_types_description: 'Image Files',
          file_post_name:         'file',
          button_placeholder:     programDetailBlock.find('.ep_upload_image').get(0),
          button_action:          SWFUpload.BUTTON_ACTION.SELECT_FILE,
          button_image_url:       $('#image_btn_upload').text(),
          button_width:           '95',
          button_height:          '32',
          button_cursor:          SWFUpload.CURSOR.HAND,
          button_window_mode :    SWFUpload.WINDOW_MODE.OPAQUE,
          debug:                  false,
          http_success :          [ 201 ],
          upload_success_handler: function(file, serverData, recievedResponse) {
            if (!file.type)
              file.type = cms.getFileTypeByName(file.name);
            var imageUrl = cms.getS3UploadBucket() + '/prog_logo_' + programId + '_' + file.size + file.type;
            log('imageUrl: ' + imageUrl);
            programDetailBlock.find('.ep_uploading_image').hide();
            programDetailBlock.find('.ep_image').attr('src', imageUrl);
            programDetailBlock.find('.ep_image_url_input').val(imageUrl).css('color', '#000');
            programDetailBlock.find('.ep_image_updated').val('true');
          },
          upload_error_handler: function(file, code, message) {
            programDetailBlock.find('.ep_uploading_image').hide();
            alert(message);
          },
          file_queued_handler: function(file) {
            if (!file.type)
              file.type = cms.getFileTypeByName(file.name);
            var post_params = {
              "AWSAccessKeyId": $('#s3_id').val(),
              "key":            'prog_logo_' + programId + '_' + file.size + file.type,
              "acl":            "public-read",
              "policy":         $('#s3_policy').val(),
              "signature":      $('#s3_signature').val(),
              "content-type":   cms.getContentTypeByFileExtention(file.type),
              "success_action_status": "201"
            };
            this.setPostParams(post_params);
            this.startUpload(file.id);
            programDetailBlock.find('.ep_uploading_image').show();
          }
        };
        var swfObjectImage = new SWFUpload(swfupload_settings);
        page$.programDetail.swfObjectImage.push(swfObjectImage);
        
        programDetailBlock.find('.ep_cancelbutton').click(function() {
          if (confirm($('#lang_confirm_cancel').text()) == false) return;
          swfObjectImage.destroy();
          swfObject.destroy();
          programDetailBlock.parent().remove();
          if ($('.program_create_detail_block_cloned').size() == 0) {
            page$.programList.init(channelId, false, channelName);
          }
        });
        
        var btnSave = programDetailBlock.find('.ep_savebutton').css('width', 80).removeClass('btnDisable').addClass('btnSave');
        btnSave.unbind().click(function() {
          if (programDetailBlock.find('.ep_name').val() == '') {
            alert($('#lang_warning_empty_name').text());
            return;
          }
          var audioUrl = programDetailBlock.find('.ep_url_input').val();
          if (audioUrl == '' || audioUrl == $('#lang_audio_url_example').text()) {
            alert($('#lang_warning_import_program_source').text());
            return;
          }
          var parameters = {
            'channelId': channelId,
            'programId': programId,
            'sourceUrl': audioUrl,
            'name':      programDetailBlock.find('.ep_name').val(),
            'intro':     programDetailBlock.find('.ep_intro').val()
          };
          var imageUrl = programDetailBlock.find('.ep_image_url_input').val();
          if (imageUrl != '' && imageUrl != $('#lang_image_url_example').text()) {
            parameters['imageUrl'] = imageUrl;
          }
          cms.post('/CMSAPI/saveNewProgram', parameters, function(response) {
            if (response == 'OK') {
              alert($('#lang_update_successfully').text());
              programDetailBlock.removeClass('program_create_detail_block_cloned').parent().hide(); // IE compatible
              var size = $('.program_create_detail_block_cloned').size();
              if ($('.program_create_detail_block_cloned').size() == 0) {
                page$.programList.init(channelId, false, channelName);
              }
            } else {
              alert($('#lang_warning_error_occurs').text());
            }
          }, 'text');
        });
      }, 'json');
    },
    programCreation: function(programId, channelId, channelName) {
      $('#program_create_detail .right_title div').text(channelName);
      $('#program_create_detail .ep_return').unbind().click(function() {
        page$.programList.init(channelId, false, channelName);
      });
      $('#continue_add_new_program_button').unbind().click(function() { // this button is not showing for now
        page$.skeletonCreation.create9x9Program(channelId, channelName);
      });
      cms.post('/CMSAPI/programInfo', { 'programId': programId }, function(program) {
        var programDetailBlock = $('#program_create_detail_block')
                                   .clone(true)
                                   .removeAttr('id')
                                   .addClass('program_create_detail_block_cloned');
        var programId = program.id;
        
        if (program.imageUrl.match('processing.png')) {
          program.imageUrl = 'http://www.youtube.com/img/pic_youtubelogo_123x63.gif';
        }
        programDetailBlock.find('.ep_name').val(program.name).attr('disabled', true);
        programDetailBlock.find('.ep_image').attr('src', program.imageUrl);
        programDetailBlock.find('.ep_image_updated').val('false');
        programDetailBlock.find('.ep_intro').val(program.intro).attr('disabled', true);
        programDetailBlock.find('.ep_upload_button_place').hide();
        programDetailBlock.find('.ep_urlbutton, .ep_ytbutton').css('width', 90).click(function() {
          
          programDetailBlock.find('.ep_name').attr('disabled', true);
          programDetailBlock.find('.ep_intro').attr('disabled', true);
          programDetailBlock.find('.ep_upload_button_place').hide();
          programDetailBlock.find('.ep_comment_block').hide();
          programDetailBlock.find('.ep_savebutton').unbind().addClass('btnDisable').removeClass('btnSave');
          
          programDetailBlock.find('.ep_uploading_video').hide();
          programDetailBlock.find('.ep_url_block').show();
          programDetailBlock.find('.video_url_hint, .youtube_hint').hide();
          if ($(this).hasClass('ep_urlbutton'))
            programDetailBlock.find('.video_url_hint').show();
          if ($(this).hasClass('ep_ytbutton'))
            programDetailBlock.find('.youtube_hint').show();
        });
        programDetailBlock.find('.ep_url_import').css('width', 60).unbind().click(function() {
          programDetailBlock.find('.ep_savebutton').unbind().addClass('btnDisable').removeClass('btnSave');
          programDetailBlock.find('.ep_name').attr('disabled', true);
          programDetailBlock.find('.ep_intro').attr('disabled', true);
          programDetailBlock.find('.ep_upload_button_place').hide();
          programDetailBlock.find('.ep_comment_block').hide();
          if (programDetailBlock.find('.ep_url_input').val().length > 0) {
            
            var inputUrl = programDetailBlock.find('.ep_url_input').val();
            var videoId = null;
            
            if (inputUrl.indexOf('youtube.com/watch') >= 0) {
              videoId = inputUrl.match(/(\?|&)v=([^\/&]+)/)[2];
            } else if (inputUrl.indexOf('/youtu.be/') >= 0) {
              videoId = inputUrl.match(/youtu\.be\/([^\/&]+)/)[1];
            }
            
            if (!videoId) {
              alert($('#lang_program_source_invalid_format').text());
              return false;
            }
            log('videoId: ' + videoId);
            
            var parameters = {
              'alt': 'json-in-script',
              'v':   2
            };
            cms.get('http://gdata.youtube.com/feeds/api/videos/' + videoId + '?callback=?', parameters, function(data) {
              
              if (!data) {
                alert($('#lang_program_source_invalid_format').text());
                return false;
              }
              
              programDetailBlock.find('.ep_name').val(data.entry.media$group.media$title.$t);
              programDetailBlock.find('.ep_intro').val(data.entry.media$group.media$description.$t);
              programDetailBlock.find('.ep_image').attr('src', data.entry.media$group.media$thumbnail[1].url);
              programDetailBlock.find('.ep_image_updated').val('true');
              programDetailBlock.find('.ep_url_input').val('http://www.youtube.com/watch?v=' + videoId);
              // youtube comment
              programDetailBlock.find('.ep_name').attr('disabled', true);
              programDetailBlock.find('.ep_intro').attr('disabled', true);
              programDetailBlock.find('.ep_upload_button_place').hide();
              programDetailBlock.find('.ep_comment_block').show();
              
              var btnSave = programDetailBlock.find('.ep_savebutton').css('width', 80).removeClass('btnDisable').addClass('btnSave');
              btnSave.unbind().click(function() {
                if (programDetailBlock.find('.ep_name').val() == '') {
                  alert($('#lang_warning_empty_name').text());
                  return;
                }
                if (programDetailBlock.find('.ep_url_input').val() == '') {
                  alert($('#lang_warning_import_program_source').text());
                  return;
                }
                var parameters = {
                  'channelId': channelId,
                  'programId': programId,
                  'sourceUrl': programDetailBlock.find('.ep_url_input').val(),
                  'name':      programDetailBlock.find('.ep_name').val(),
                  'intro':     programDetailBlock.find('.ep_intro').val(),
                  'comment':   programDetailBlock.find('.ep_comment').val()
                };
                if (programDetailBlock.find('.ep_image_updated').val() == 'true') {
                  parameters['imageUrl'] = programDetailBlock.find('.ep_image').attr('src');
                }
                cms.post('/CMSAPI/saveNewProgram', parameters, function(response) {
                  if (response == 'OK') {
                    alert($('#lang_update_successfully').text());
                    programDetailBlock.removeClass('program_create_detail_block_cloned').parent().hide(); // IE compatible
                    var size = $('.program_create_detail_block_cloned').size();
                    if ($('.program_create_detail_block_cloned').size() == 0) {
                      page$.programList.init(channelId, false, channelName);
                    }
                  } else {
                    alert($('#lang_warning_error_occurs').text());
                  }
                }, 'text');
              });
            }, 'json', function() {
              alert($('#lang_program_source_invalid_format').text());
              return false;
            });
            /* server side approach
            var parameters = {
              'videoIdStr': videoId
            };
            cms.post('/CMSAPI/getYouTubeVideoInfo', parameters, function(info) {
              programDetailBlock.find('.ep_name').val(info.title);
              programDetailBlock.find('.ep_intro').val(info.description);
              programDetailBlock.find('.ep_image').attr('src', info.thumbnail);
              programDetailBlock.find('.ep_image_updated').val('true');
            }, 'json');
            */
          } else {
            alert($('#lang_program_source_is_empty').text());
            return;
          }
        });
        $('#program_create_detail').show();
        $('<li></li>').append(programDetailBlock).appendTo('#program_create_ul');
        // video uploading
        var swfupload_settings = {
          flash_url:              cms.getExternalRootPath() + '/javascripts/swfupload/swfupload.swf',
          upload_url:             cms.getS3UploadBucket(),
          file_size_limit:        '1 GB',
          file_types:             cms.videoTypes,
          file_types_description: 'Video Files',
          file_post_name:         'file',
          button_placeholder:     programDetailBlock.find('.ep_upload_video').get(0),
          button_action:          SWFUpload.BUTTON_ACTION.SELECT_FILE,
          button_image_url:       $('#image_btn_from_disk').text(),
          button_width:           '95',
          button_height:          '32',
          button_cursor:          SWFUpload.CURSOR.HAND,
          button_window_mode :    SWFUpload.WINDOW_MODE.OPAQUE,
          debug:                  false,
          http_success :          [ 201 ],
          upload_progress_handler: function(file, completed, total) {
            programDetailBlock.find('.ep_uploading_video div').progressBar((completed * 100) / total);
          },
          upload_success_handler: function(file, serverData, recievedResponse) {
            if (!file.type)
              file.type = cms.getFileTypeByName(file.name);
            programDetailBlock.find('.ep_url_input').val(cms.getS3UploadBucket() + '/prog_video_' + programId + file.type);
            programDetailBlock.find('.ep_url_import').click();
          },
          upload_error_handler: function(file, code, message) {
            programDetailBlock.find('.ep_uploading_video div').text($('#lang_upload_failed').text()).show();
            alert(message);
          },
          file_queued_handler: function(file) {
            if (!file.type)
              file.type = cms.getFileTypeByName(file.name);
            var post_params = {
              "AWSAccessKeyId": $('#s3_id').val(),
              "key":            'prog_video_' + programId + file.type,
              "acl":            "public-read",
              "policy":         $('#s3_policy').val(),
              "signature":      $('#s3_signature').val(),
              "content-type":   cms.getContentTypeByFileExtention(file.type),
              "success_action_status": "201"
            };
            this.setPostParams(post_params);
            this.startUpload(file.id);
            programDetailBlock.find('.ep_uploading_video div').text('').progressBar({
              barImage: cms.getExternalRootPath() + '/images/cms/progressbg_black.gif'
            }).parent().show();
            programDetailBlock.find('.ep_url_input').val('');
            programDetailBlock.find('.ep_url_block').hide();
            programDetailBlock.find('.ep_savebutton').unbind().addClass('btnDisable').removeClass('btnSave');
            
            programDetailBlock.find('.ep_name').attr('disabled', false);
            programDetailBlock.find('.ep_intro').attr('disabled', false);
            programDetailBlock.find('.ep_upload_button_place').show();
            programDetailBlock.find('.ep_comment_block').hide();
         }
        };
        var swfObject = new SWFUpload(swfupload_settings);
        page$.programDetail.swfObjectVideo.push(swfObject);
        // logo uploading
        var swfupload_settings = {
          flash_url:              cms.getExternalRootPath() + '/javascripts/swfupload/swfupload.swf',
          upload_url:             cms.getS3UploadBucket(),
          file_size_limit:        '10 MB',
          file_types:             cms.imageTypes,
          file_types_description: 'Image Files',
          file_post_name:         'file',
          button_placeholder:     programDetailBlock.find('.ep_upload_image').get(0),
          button_action:          SWFUpload.BUTTON_ACTION.SELECT_FILE,
          button_image_url:       $('#image_btn_upload').text(),
          button_width:           '95',
          button_height:          '32',
          button_cursor:          SWFUpload.CURSOR.HAND,
          button_window_mode :    SWFUpload.WINDOW_MODE.OPAQUE,
          debug:                  false,
          http_success :          [ 201 ],
          upload_success_handler: function(file, serverData, recievedResponse) {
            if (!file.type)
              file.type = cms.getFileTypeByName(file.name);
            programDetailBlock.find('.ep_uploading_image').hide();
            programDetailBlock.find('.ep_image').attr('src', cms.getS3UploadBucket() + '/prog_logo_' + programId + '_' + file.size + file.type);
            programDetailBlock.find('.ep_image_updated').val('true');
          },
          upload_error_handler: function(file, code, message) {
            programDetailBlock.find('.ep_uploading_image').hide();
            alert(message);
          },
          file_queued_handler: function(file) {
            if (!file.type)
              file.type = cms.getFileTypeByName(file.name);
            var post_params = {
              "AWSAccessKeyId": $('#s3_id').val(),
              "key":            'prog_logo_' + programId + '_' + file.size + file.type,
              "acl":            "public-read",
              "policy":         $('#s3_policy').val(),
              "signature":      $('#s3_signature').val(),
              "content-type":   cms.getContentTypeByFileExtention(file.type),
              "success_action_status": "201"
            };
            this.setPostParams(post_params);
            this.startUpload(file.id);
            programDetailBlock.find('.ep_uploading_image').show();
          }
        };
        var swfObjectImage = new SWFUpload(swfupload_settings);
        page$.programDetail.swfObjectImage.push(swfObjectImage);
        programDetailBlock.find('.ep_cancelbutton').click(function() {
          // if (confirm($('#lang_confirm_cancel').text()) == false) return;
          swfObjectImage.destroy();
          swfObject.destroy();
          programDetailBlock.parent().remove();
          if ($('.program_create_detail_block_cloned').size() == 0) {
            page$.programList.init(channelId, false, channelName);
          }
        });
        // to hide other than youtube way
        programDetailBlock.find('.ep_ytbutton').click();
        programDetailBlock.find('.ep_select_block').hide();
      }, 'json');
    }
  },
  programList: {
    destroy: function() {
      $('#program_list_empty').hide();
      $('#program_list_empty_readonly').hide();
      $('.program_info_block_cloned').each(function() {
        $(this).parent().remove();
      });
      $('#program_list').hide();
      $('#program_list_readonly').hide();
    },
    init: function(channelId, readonly, channelName) {
      if (page$.overallLayout.destroyRightSideContent(false) == false) return false;
      
      cms.post('/CMSAPI/programList', { 'channelId': channelId }, function(programs) {
        $('.create_program_button').width(175).unbind().click({ 'channelId': channelId, 'channelName': channelName }, function(event) {
          if (page$.overallLayout.destroyRightSideContent(false) == false) return false;
          page$.skeletonCreation.create9x9Program(event.data.channelId, event.data.channelName);
        });
        $('.create_radio_program_button').width(175).unbind().click({ 'channelId': channelId, 'channelName': channelName }, function(event) {
          if (page$.overallLayout.destroyRightSideContent(false) == false) return false;
          page$.skeletonCreation.createRadioProgram(event.data.channelId, event.data.channelName);
        });
        if (programs.length == 0) {
          if (readonly) {
            $('#program_list_empty_readonly .right_title div').text(channelName);
            $('#program_list_empty_readonly').show();
          } else {
            $('#program_list_empty .right_title div').text(channelName);
            $('#program_list_empty').show();
          }
          return;
        }
        $('#publish_button').unbind().click({ 'channelId': channelId, 'channelName': channelName }, function(event) { // actually, its re-order button
          $('.program_info_removebutton').unbind().css('background-image', 'url(' + cms.getExternalRootPath() + '/images/cms/btn_move.png)').css('cursor', 'move');
          $(this).css('background-image', 'url(' + $('#image_btn_save').text() + ')');
          $('#program_list_ul').sortable({
            disabled: false,
            cursor:   'move',
            forcePlaceHolderSize: true,
            update: function() {
            }
          }).disableSelection();
          $('.program_info_block_cloned').css('cursor', 'move');
          $(this).text('').unbind().click(function() {
            var programDoms = $('.program_info_block_cloned').toArray();
            var programIdList = [ ];
            for (var i = 0; i < programDoms.length; i++) {
              var programId = $(programDoms[i]).find('[name="programId"]').val();
              programIdList.push(programId);
            }
            log(programIdList);
            var parameters = {
              channelId:     event.data.channelId,
              programIdList: programIdList.join(',')
            };
            cms.post('/CMSAPI/updateProgramListSeq', parameters, function(response) {
              if (response != 'OK') {
                alert($('#lang_warning_error_occurs').text());
              } else {
                alert($('#lang_update_successfully').text());
                $('#program_list_ul').sortable({ disabled: true });
                page$.overallLayout.destroyRightSideContent(true);
                page$.programList.init(event.data.channelId, false, event.data.channelName);
              }
            }, 'text');
          });
        });
        if (readonly) {
          $('#program_list_readonly .right_title div').text(channelName);
          page$.programList.displayProgramListReadonly(programs);
        } else {
          $('#program_list .right_title div').text(channelName);
          page$.programList.displayProgramList(programs);
        }
      }, 'json');
    },
    initYouTube: function(username, channelName, callback, isPlaylist, channelId) {
      if (page$.overallLayout.destroyRightSideContent(false) == false) return false;
      
      var requestUrl = 'http://gdata.youtube.com/feeds/api/users/' + username + '/uploads?callback=?';
      var parameters = {
        'orderby':     'published',
        'start-index': 1,
        'max-results': 50,
        'alt':         'json-in-script',
        'format':      5,
        'v':           2
      };
      if (isPlaylist) {
        requestUrl = 'http://gdata.youtube.com/feeds/api/playlists/' + username + '?callback=?';
        parameters['orderby'] = 'position';
      }
      cms.get(requestUrl, parameters, function(data) {
        if (data == null) return;
        var feed = data.feed;
        if (typeof feed.entry != 'undefined') {
          if (feed.entry.length == 0) {
            $('#program_list_empty_readonly .right_title div').text(channelName);
            $('#program_list_empty_readonly').show();
            return;
          } else {
            $('#program_list_readonly .right_title div').text(channelName);
            var programCnt = feed.entry.length;
            callback(programCnt);
            page$.programList.displayYouTube(feed.entry, channelId);
          }
        }
      }, 'json');
    },
    displayYouTube: function(entries, channelId) {
      for (i in entries) {
        var programInfoBlock = $('#program_info_block_readonly').clone(true).removeAttr('id').addClass('program_info_block_cloned');
        var entry = entries[i];
        
        programInfoBlock.find('.program_info_title div').text(entry.media$group.media$title.$t);
        $('<img/>').attr('src', entry.media$group.media$thumbnail[1]['url']).appendTo(programInfoBlock.find('.program_info_image'));
        programInfoBlock.find('.program_info_type span').text('YouTube');
        programInfoBlock.find('.program_info_updatedate span').text(entry.updated.$t.substring(0, 19).replace('T', ' ').replace(/-/g, '/'));
        // add this
        var promoteUrl = 'http://' + location.host + '/view?channel=' + channelId + '&episode=' + entry.media$group.yt$videoid.$t;
        programInfoBlock.find('.addthis_button_expanded').attr('addthis:url', promoteUrl);
        programInfoBlock.find('.program_info_detailbutton').click({ 'entry': entry }, function(event) {
          var youtubeUrl = event.data.entry.link[4].href + '&callback=?';
          page$.programDetail.initYouTube(youtubeUrl, channelId);
          return false;
        });
        var promoteUrlTruncated = promoteUrl;
        if (promoteUrl.length > 48) {
          promoteUrlTruncated = promoteUrl.substring(0, 44) + '...';
        }
        programInfoBlock.find('.program_info_promoteurl').text(promoteUrlTruncated).attr('href', promoteUrl);
        $('<li></li>').append(programInfoBlock).appendTo('#program_list_ul_readonly');
        programInfoBlock.click(function() {
          $('.program_info_block').removeClass('epItemFocus').addClass('epItem');
          $('.program_info_title').removeClass('epItemFocusTitle').addClass('epInfoTitle');
          $(this).removeClass('epItem').addClass('epItemFocus');
          $('.program_info_title', this).removeClass('epInfoTitle').addClass('epItemFocusTitle');
        });
      }
      $('#program_list_readonly').show();
      cms.initAddthis();
    },
    displayProgramList: function(programs) {
      for (i in programs) {
        var programInfoBlock = $('#program_info_block').clone(true).removeAttr('id').addClass('program_info_block_cloned');
        var programId = programs[i].id;
        var program = programs[i];
        
        programInfoBlock.find('.iconStatistics').attr('title', $('#lang_view_statistics').text());
        programInfoBlock.find('.program_info_title div').text(programs[i].name);
        $('<img/>').attr('src', programs[i].imageUrl).appendTo(programInfoBlock.find('.program_info_image'));
        var type = 'Unknown';
        switch(programs[i].contentType) {
          case 0:
          type = '9x9';
          break;
          case 1:
          type = 'YouTube';
          break;
          case 2:
          type = 'SlideShow';
          break;
          case 3:
          type = 'Audio';
          break;
          default:
        }
        programInfoBlock.find('.program_info_type span').text(type);
        programInfoBlock.find('.program_info_updatedate span').text(cms.formatDate(programs[i].updateDate));
        // add this
        var promoteUrl = 'http://' + location.host + '/view?channel=' + program.channelId + '&episode=' + programId;
        programInfoBlock.find('.addthis_button_expanded').attr('addthis:url', promoteUrl + '&_=' + program.updateDate);
        var switchObject = programInfoBlock.find('.program_info_publish');
        if (programs[i]['public']) {
          switchObject.removeClass('chUnPublic').addClass('chPublic');
        } else {
          switchObject.removeClass('chPublic').addClass('chUnPublic');
        }
        programInfoBlock.find('.program_info_removebutton').click({ 'programId': programId, 'programName': programs[i].name, 'programInfoBlock': programInfoBlock }, function(event) {
          var confirmation = $('#lang_confirm_removing_program').text().replace('{programName}', event.data.programName);
          if (confirm(confirmation) == false)
            return false;
          var parameters = {
            'programId': event.data.programId
          };
          cms.post('/CMSAPI/removeProgram', parameters, function() {
            event.data.programInfoBlock.parent().remove();
          });
          return false;
        });
        programInfoBlock.find('.program_info_publish').click({ 'programId': programId, 'switchObject': switchObject }, function(event) {
          cms.post('/CMSAPI/switchProgramPublicity', { 'programId': event.data.programId }, function(response) {
            if (response) {
              event.data.switchObject.removeClass('chUnPublic').addClass('chPublic');
            } else {
              event.data.switchObject.removeClass('chPublic').addClass('chUnPublic');
            }
          }, 'json');
          return false;
        });
        programInfoBlock.find('.program_info_detailbutton').click({ 'programId': programId }, function(event) {
          page$.programDetail.init(event.data.programId, false);
          return false;
        });
        var promoteUrlTruncated = promoteUrl;
        if (promoteUrl.length > 48) {
          promoteUrlTruncated = promoteUrl.substring(0, 44) + '...';
        }
        programInfoBlock.find('.program_info_promoteurl').text(promoteUrlTruncated).attr('href', promoteUrl);
        $('<input type="hidden" name="programId"/>').val(programId).appendTo(programInfoBlock);
        $('<li></li>').append(programInfoBlock).appendTo('#program_list_ul');
        programInfoBlock.click({ 'programId': programId }, function(event) {
          $('.program_info_block').removeClass('epItemFocus').addClass('epItem');
          $('.program_info_title').removeClass('epItemFocusTitle').addClass('epInfoTitle');
          $(this).removeClass('epItem').addClass('epItemFocus');
          $('.program_info_title', this).removeClass('epInfoTitle').addClass('epItemFocusTitle');
        });
      }
      $('#program_list').show();
      cms.initAddthis();
    },
    displayProgramListReadonly: function(programs) {
      for (i in programs) {
        var programInfoBlock = $('#program_info_block_readonly').clone(true).removeAttr('id').addClass('program_info_block_cloned');
        var programId = programs[i].id;
        var program = programs[i];
        
        programInfoBlock.find('.iconStatistics').attr('title', $('#lang_view_statistics'));
        programInfoBlock.find('.program_info_title div').text(programs[i].name);
        $('<img/>').attr('src', programs[i].imageUrl).appendTo(programInfoBlock.find('.program_info_image'));
        var type = 'Unknown';
        switch(programs[i].contentType) {
          case 0:
          type = 'Podcast';
          break;
          case 1:
          type = 'YouTube';
          break;
          default:
        }
        programInfoBlock.find('.program_info_type span').text(type);
        programInfoBlock.find('.program_info_updatedate span').text(cms.formatDate(programs[i].updateDate));
        // add this
        var promoteUrl = 'http://' + location.host + '/view?channel=' + program.channelId + '&episode=' + programId;
        programInfoBlock.find('.addthis_button_expanded').attr('addthis:url', promoteUrl + '&_=' + program.updateDate);
        var switchObject = programInfoBlock.find('.program_info_publish');
        if (programs[i]['public']) {
          switchObject.removeClass('chUnPublic').addClass('chPublic');
        } else {
          switchObject.removeClass('chPublic').addClass('chUnPublic');
        }
        programInfoBlock.find('.program_info_detailbutton').click({ 'programId': programId }, function(event) {
          page$.programDetail.init(event.data.programId, true);
          return false;
        });
        var promoteUrlTruncated = promoteUrl;
        if (promoteUrl.length > 48) {
          promoteUrlTruncated = promoteUrl.substring(0, 44) + '...';
        }
        programInfoBlock.find('.program_info_promoteurl').text(promoteUrlTruncated).attr('href', promoteUrl);
        $('<li></li>').append(programInfoBlock).appendTo('#program_list_ul_readonly');
        programInfoBlock.click({ 'programId': programId }, function(event) {
          $('.program_info_block').removeClass('epItemFocus').addClass('epItem');
          $('.program_info_title').removeClass('epItemFocusTitle').addClass('epInfoTitle');
          $(this).removeClass('epItem').addClass('epItemFocus');
          $('.program_info_title', this).removeClass('epInfoTitle').addClass('epItemFocusTitle');
        });
      }
      $('#program_list_readonly').show();
      cms.initAddthis();
    }
  },
  channelDetail: {
    swfObject: null,
    destroy: function() {
      if (page$.channelDetail.swfObject != null) {
        page$.channelDetail.swfObject.destroy();
        page$.channelDetail.swfObject = null;
      }
      $('#channel_detail .right_title').text($('#lang_title_create_channel_info').text());
      $('#channel_detail #ch_language').val('zh');
      $('#channel_detail').hide();
      $('#channel_import_detail').hide();
      $('#channel_import_detail [name="ch_import_url"]').val('');
      $('#channel_import_detail [name="ch_name"]').val('').attr('disabled', true);
      $('#channel_import_detail [name="ch_intro"]').val('').attr('disabled', true);
      $('#channel_import_detail [name="ch_tag"]').val('').attr('disabled', true);
      $('#channel_import_detail [name="ch_language"]').val('zh').attr('disabled', true);
      $('#channel_import_detail [name="ch_savebutton"]').removeClass('btnCreate').addClass('btnDisable');
      $('#channel_import_detail [name="ch_image"]').attr('src', cms.getExternalRootPath() + '/images/cms/upload_img.jpg');
      $('#channel_import_detail [name="ch_image_updated"]').val('false');
      $('#channel_import_detail [name="ch_import_button"]').unbind().css('width', 90);
    },
    displayImportDetail: function() {
      if (page$.overallLayout.destroyRightSideContent(false) == false) return false;
      
      $('#channel_import_detail [name="ch_import_button"]').unbind().click(function() {
        if ($('#channel_import_detail [name="ch_import_url"]').val() == "") {
          alert($('#lang_channel_source_is_empty').text());
          return;
        }
        $('#channel_import_detail [name="ch_image_updated"]').val('false');
        var sourceUrl = $('#channel_import_detail [name="ch_import_url"]').val();
        var parameters = {
          'sourceUrl': sourceUrl
        };
        cms.post('/CMSAPI/importChannelByUrl', parameters, function(channel) {
          if (channel == null) {
            alert($('#lang_channel_source_is_wrong').text());
            $('#channel_import_detail [name="ch_import_url"]').focus();
            return;
          }
          $('#channel_import_detail [name="ch_import_url"]').val(channel.sourceUrl);
          $('#channel_import_detail [name="ch_name"]').val('').attr('disabled', false);
          $('#channel_import_detail [name="ch_intro"]').val('').attr('disabled', false);
          $('#channel_import_detail [name="ch_tag"]').val('').attr('disabled', false);
          $('#channel_import_detail [name="ch_language"]').val('zh').attr('disabled', false);
          $('#channel_import_detail [name="upload_button_place"]').html('').append($('<span/>').attr('name', 'ch_upload_image'));
          if (page$.channelDetail.swfObject != null) {
            page$.channelDetail.swfObject.destroy();
          }
          var swfupload_settings = {
            flash_url:              cms.getExternalRootPath() + '/javascripts/swfupload/swfupload.swf',
            upload_url:             cms.getS3UploadBucket(),
            file_size_limit:        '10 MB',
            file_types:             cms.imageTypes,
            file_types_description: 'Image Files',
            file_post_name:         'file',
            button_placeholder:     $('#channel_import_detail [name="ch_upload_image"]').get(0),
            button_action:          SWFUpload.BUTTON_ACTION.SELECT_FILE,
            button_image_url:       $('#image_btn_upload').text(),
            button_width:           '95',
            button_height:          '32',
            button_cursor:          SWFUpload.CURSOR.HAND,
            button_window_mode :    SWFUpload.WINDOW_MODE.OPAQUE,
            debug:                  false,
            http_success :          [ 201 ],
            upload_success_handler: function(file, serverData, recievedResponse) {
              if (!file.type)
                file.type = cms.getFileTypeByName(file.name);
              $('#channel_import_detail [name="ch_uploading_image"]').hide();
              $('#channel_import_detail [name="ch_image"]').attr('src', cms.getS3UploadBucket() + '/ch_logo_' + channel.id + '_' + file.size + file.type);
              $('#channel_import_detail [name="ch_image_updated"]').val('true');
            },
            upload_error_handler: function(file, code, message) {
              $('#channel_import_detail [name="ch_uploading_image"]').hide();
              alert(message);
            },
            file_queued_handler: function(file) { 
              if (!file.type)
                file.type = cms.getFileTypeByName(file.name);
              var post_params = {
                "AWSAccessKeyId": $('#s3_id').val(),
                "key":            'ch_logo_' + channel.id + '_' + file.size + file.type,
                "acl":            "public-read",
                "policy":         $('#s3_policy').val(),
                "signature":      $('#s3_signature').val(),
                "content-type":   cms.getContentTypeByFileExtention(file.type),
                "success_action_status": "201"
              };
              this.setPostParams(post_params);
              this.startUpload(file.id);
              $('#channel_import_detail [name="ch_uploading_image"]').show();
            }
          };
          page$.channelDetail.swfObject = new SWFUpload(swfupload_settings);
          $('#channel_import_detail [name="ch_name"]').val(channel.name);
          $('#channel_import_detail [name="ch_intro"]').val(channel.intro);
          if (channel.tag == 'NEW_CHANNEL') {
            $('#channel_import_detail [name="ch_tag"]').val('');
          } else {
            $('#channel_import_detail [name="ch_tag"]').val(channel.tag);
          }
          $('#channel_import_detail [name="ch_language"]').val(channel.lang);
          $('#channel_import_detail [name="ch_image"]').attr('src', channel.imageUrl);
          $('#channel_import_detail [name="ch_image_updated"]').val('false');
          if (channel.imageUrl.match('processing.png')) {
            $('#channel_import_detail [name="ch_image"]').attr('src', cms.getExternalRootPath() + '/images/cms/upload_img.jpg');
            $('#channel_import_detail [name="ch_image_updated"]').val('true');
            if (channel.sourceUrl.indexOf('youtube.com') >= 0) {
              var requestUrl;
              if (channel.sourceUrl.indexOf('view_play_list') >= 0) {
                var listId = channel.sourceUrl.match(/\/view_play_list\?p=([^\/]*)/)[1];
                requestUrl = 'http://gdata.youtube.com/feeds/api/playlists/' + listId + '?callback=?';
              } else {
                var username = channel.sourceUrl.match(/\/user\/([^\/]*)/)[1];
                requestUrl = 'http://gdata.youtube.com/feeds/api/users/' + username + '/uploads?callback=?';
              }
              var parameters = {
                'alt':    'json-in-script',
                'format': 5,
                'v':      2
              };
              cms.get(requestUrl, parameters, function(data) {
                if (data == null) return;
                var feed = data.feed;
                $('#channel_import_detail [name="ch_name"]').val(feed.title.$t);
                if (typeof feed.entry != 'undefined')
                  $('#channel_import_detail [name="ch_image"]').attr('src', feed.entry[0].media$group.media$thumbnail[1]['url']);
                else
                  $('#channel_import_detail [name="ch_image"]').attr('src', feed.logo.$t);
                $('#channel_import_detail [name="ch_image_updated"]').val('true');
              }, 'json');
            } else {
              var parameters = {
                'url': channel.sourceUrl
              };
              cms.post('/CMSAPI/getPodcastInfo', parameters, function(feed) {
                $('#channel_import_detail [name="ch_name"]').val(feed.title);
                $('#channel_import_detail [name="ch_intro"]').val(feed.description);
                if (typeof feed.thumbnail != 'undefined')
                  $('#channel_import_detail [name="ch_image"]').attr('src', feed.thumbnail);
                $('#channel_import_detail [name="ch_image_updated"]').val('true');
              }, 'json');
              /* client approach
              var feed = new google.feeds.Feed(channel.sourceUrl);
              feed.load(function(result)
              {
                $.dump(result);
              });
              */
            }
          }
          $('#channel_import_detail [name="ch_savebutton"]').removeClass('btnDisable').addClass('btnCreate');
          $('#channel_import_detail [name="ch_savebutton"]').unbind().click(function() {
            var name = $('#channel_import_detail [name="ch_name"]').val();
            if (name == '') {
              alert($('#lang_warning_empty_name').text());
              return;
            }
            var intro = $('#channel_import_detail [name="ch_intro"]').val();
            var imageUrl = $('#channel_import_detail [name="ch_image"]').attr('src');
            var imageUpdated = $('#channel_import_detail [name="ch_image_updated"]').val();
            var sourceUrl = $('#channel_import_detail [name="ch_import_url"]').val();
            var tag = $('#channel_import_detail [name="ch_tag"]').val();
            var lang = $('#channel_import_detail [name="ch_language"]').val();
            var parameters = {
              'name':       name,
              'intro':      intro,
              'tag':        tag,
              'lang':       lang,
              'msoId':      $('#msoId').val(),
              'sourceUrl':  sourceUrl
            };
            if (imageUpdated == 'true') {
              parameters['imageUrl'] = imageUrl;
            }
            cms.post('/CMSAPI/addChannelByUrl', parameters, function(response) {
              if (response != 'OK') {
                alert($('#lang_warning_error_occurs').text());
              } else {
                alert($('#lang_update_successfully').text());
                page$.overallLayout.destroyRightSideContent(true);
                page$.channelList.init();
              }
            }, 'text');
          });
        }, 'json');
      });
      $('#channel_import_detail [name="ch_savebutton"]').css('width', 80).unbind().click(function() {
        alert($('#lang_warning_import_channel_source').text());
      });
      $('#channel_import_detail [name="ch_cancelbutton"]').unbind().click(function() {
        page$.channelDetail.displayImportDetail();
      });
      $('#channel_import_detail').show();
    },
    init: function(channelId, isNew) {
      if (page$.overallLayout.destroyRightSideContent(false) == false) return false;
      log('is new channel: ' + isNew);
      if (!isNew) {
        $('#ch_title').text($('#lang_title_edit_channel_info').text());
      }
      cms.post('/CMSAPI/channelInfo', { 'channelId': channelId }, function(channel) {
        $('#ch_id').val(channel.id);
        $('#ch_name').val('').val(channel.name);
        $('#ch_image').attr('src', channel.imageUrl);
        $('#ch_image_updated').val('false');
        if ($('#ch_image').attr('src').match('processing.png')) {
          $('#ch_image').attr('src', cms.getExternalRootPath() + '/images/cms/upload_img.jpg');
          $('#ch_image_updated').val('true');
        }
        $('#ch_intro').val('').val(channel.intro);
        $('#ch_tag').val(channel.tag);
        $('#ch_language').val(channel.lang);
        if (page$.channelDetail.swfObject != null) {
          page$.channelDetail.swfObject.destroy();
        }
        $('<span/>').attr('id', 'ch_upload_image').appendTo('#upload_button_place');
        var swfupload_settings = {
          flash_url:              cms.getExternalRootPath() + '/javascripts/swfupload/swfupload.swf',
          upload_url:             cms.getS3UploadBucket(),
          file_size_limit:        '10 MB',
          file_types:             cms.imageTypes,
          file_types_description: 'Image Files',
          file_post_name:         'file',
          button_placeholder:     document.getElementById('ch_upload_image'),
          button_action:          SWFUpload.BUTTON_ACTION.SELECT_FILE,
          button_image_url:       $('#image_btn_upload').text(),
          button_width:           '95',
          button_height:          '32',
          button_cursor:          SWFUpload.CURSOR.HAND,
          button_window_mode :    SWFUpload.WINDOW_MODE.OPAQUE,
          debug:                  false,
          http_success :          [ 201 ],
          upload_success_handler: function(file, serverData, recievedResponse) {
            if (!file.type)
              file.type = cms.getFileTypeByName(file.name);
            $('#ch_uploading').hide();
            var imageUrl = cms.getS3UploadBucket() + '/ch_logo_' + channel.id + '_' + file.size + file.type;
            $('#ch_image').attr('src', imageUrl);
            $('#ch_image_updated').val('true');
          },
          upload_error_handler: function(file, code, message) {
            $('#ch_uploading').hide();
            alert(message);
          },
          file_queued_handler: function(file) {
            if (!file.type)
              file.type = cms.getFileTypeByName(file.name);
            var post_params = {
              "AWSAccessKeyId": $('#s3_id').val(),
              "key":            'ch_logo_' + channel.id + '_' + file.size + file.type,
              "acl":            "public-read",
              "policy":         $('#s3_policy').val(),
              "signature":      $('#s3_signature').val(),
              "content-type":   cms.getContentTypeByFileExtention(file.type),
              "success_action_status": "201"
            };
            //log(post_params);
            this.setPostParams(post_params);
            this.startUpload(file.id);
            $('#ch_uploading').show();
          }
        };
        //log(swfupload_settings);
        page$.channelDetail.swfObject = new SWFUpload(swfupload_settings);
        $('#channel_detail').show();
      }, 'json');
      $('#channel_detail_cancel').unbind().click(function() {
        if (page$.overallLayout.destroyRightSideContent(false) == false) return false;
        page$.channelList.init();
      });
      $('#channel_detail_savebutton').css('width', 80).unbind().click(function() {
        if ($('#ch_name').val() == "") {
          alert($('#lang_warning_empty_name').text());
          return;
        }
        var parameters = {
          'channelId':  $('#ch_id').val(),
          'name':       $('#ch_name').val(),
          'intro':      $('#ch_intro').val(),
          'tag':        $('#ch_tag').val(),
          'lang':       $('#ch_language').val()
        };
        if (isNew) {
          parameters.msoId = $('#msoId').val();
        }
        if ($('#ch_image_updated').val() == 'true') {
          parameters['imageUrl'] = $('#ch_image').attr('src');
        }
        cms.post('/CMSAPI/saveChannel', parameters, function(response) {
          if (response != 'OK') {
            alert($('#lang_warning_error_occurs').text());
          } else {
            alert($('#lang_update_successfully').text());
            page$.channelList.init();
          }
        }, 'text');
      });
    }
  },
  channelList: {
    destroy: function() {
      $('.channel_info_block_cloned').each(function() {
        $(this).parent().remove();
      });
      $('#channel_list_empty').hide();
    },
    init: function() {
      page$.channelList.destroy();
      
      // load channels
      cms.post('/CMSAPI/listOwnedChannels', { 'msoId': $('#msoId').val() }, function(channels) {
        if (channels.length == 0) {
          $('#channel_list_empty').show();
          return;
        }
        for (i in channels) {
          var channelInfoBlock = $('#channel_info_block').clone(true).removeAttr('id').addClass('channel_info_block_cloned');
          var channelId = channels[i].id;
          
          channelInfoBlock.find('.iconStatistics').attr('title', $('#lang_view_statistics'));
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
          channelInfoBlock.find('.channel_info_updatedate span').text(cms.formatDate(channels[i].updateDate, true));
          // add this
          var promoteUrl = 'http://' + location.host + '/view?channel=' + channelId;
          channelInfoBlock.find('.addthis_button_expanded').attr('addthis:url', promoteUrl + '&_=' + channels[i].updateDate);
          var switchObject = channelInfoBlock.find('.channel_info_publish');
          if (channels[i]['public']) {
            switchObject.removeClass('chUnPublic').addClass('chPublic');
          } else {
            switchObject.removeClass('chPublic').addClass('chUnPublic');
          }
          channelInfoBlock.find('.channel_info_removebutton').click({ 'channelId': channelId, 'channelName': channels[i].name, 'channelInfoBlock': channelInfoBlock }, function(event) {
            var confirmation = $('#lang_confirm_removing_channel').text().replace('{channelName}', event.data.channelName);
            if (confirm(confirmation) == false)
              return false;
            var parameters = {
              'channelId': event.data.channelId,
              'msoId': $('#msoId').val()
            };
            cms.post('/CMSAPI/removeChannelFromList', parameters, function() {
              event.data.channelInfoBlock.parent().remove();
              if (page$.overallLayout.destroyRightSideContent(false) == false) return false;
            });
            return false;
          });
          channelInfoBlock.find('.channel_info_publish').click({ 'channelId': channelId, 'switchObject': switchObject }, function(event) {
            cms.post('/CMSAPI/switchChannelPublicity', { 'channelId': event.data.channelId }, function(response) {
              if (response) {
                event.data.switchObject.css('background', 'url(' + $('#image_ch_public').text() + ') no-repeat');
              } else {
                event.data.switchObject.css('background', 'url(' + $('#image_ch_unpublic').text() + ') no-repeat');
              }
            }, 'json');
            return false;
          });
          channelInfoBlock.find('.channel_info_detailbutton').click({ 'channelId': channelId }, function(event) {
            page$.channelDetail.init(event.data.channelId, false);
            return false;
          });
          var promoteUrlTruncated = promoteUrl;
          if (promoteUrl.length > 40) {
            promoteUrlTruncated = promoteUrl.substring(0, 36) + '...';
          }
          channelInfoBlock.find('.channel_info_promoteurl').text(promoteUrlTruncated).attr('href', promoteUrl);
          $('<li></li>').append(channelInfoBlock).appendTo('#channel_list_ul');
          channelInfoBlock.click({ 'channel': channels[i] }, function(event) {
            var infoBlock = $(this);
            $('.channel_info_block').removeClass('chFocus').addClass('chUnFocus');
            $('.channel_info_title').removeClass('chFocusTitle').addClass('chUnFocusTitle');
            $('.channel_info_image').removeClass('chFocusImg').addClass('chUnFocusImg');
            infoBlock.removeClass('chUnFocus').addClass('chFocus');
            $('.channel_info_title', infoBlock).removeClass('chUnFocusTitle').addClass('chFocusTitle');
            $('.channel_info_image', infoBlock).removeClass('chUnFocusImg').addClass('chFocusImg');
            var channel = event.data.channel;
            var readonly = true;
            if (channel.contentType == 6)
              readonly = false;
            if (channel.contentType == 3 || channel.contentType == 4) {
              var username = null;
              if (channel.contentType == 3)
                username = channel.sourceUrl.match(/\/user\/([^\/]*)/)[1];
              else if (channel.contentType == 4)
                username = channel.sourceUrl.match(/\/view_play_list\?p=([^&]*)/)[1];
              if (username == null) {
                alert($('#lang_warning_error_occurs').text());
                return;
              }
              log('fetch: ' + username);
              page$.programList.initYouTube(username, channel.name, function(programCnt) {
                $('.channel_info_programcount span', infoBlock).text(programCnt);
              }, (channel.contentType == 4), channel.id);
            } else {
              page$.programList.init(channel.id, readonly, channel.name);
            }
          });
        }
        $('#channel_list').show();
        cms.initAddthis();
        log('channel count: ' + $('.channel_info_block_cloned').size());
        var pivot = $('.channel_info_block_cloned').get(0);
        $(pivot).click();
      }, 'json');
      if (cms.isGeneric()) {
        $('.create_channel_button').unbind().click(function() {
          if (page$.overallLayout.destroyRightSideContent(false) == false) return false;
          page$.skeletonCreation.create9x9Channel();
        });
      } else {
        $('.create_channel_button').unbind().click(function() {
          if (page$.overallLayout.destroyRightSideContent(false) == false) return false;
          $('#create_9x9_channel_button').unbind().click(page$.skeletonCreation.create9x9Channel);
          $('.import_button').unbind().click(page$.channelDetail.displayImportDetail);
          $('#choose_channel_type').show();
        });
      }
    }
  },
  initGenericOne: function() {
  },
  init: function() {
    $('.create_channel_hint').show();
    
    page$.channelList.init();
    /* not to confirm program creation
    $(window).bind('beforeunload', function() {
      if ($('.program_create_detail_block_cloned').size() > 0) {
        return $('#lang_confirm_leaving_program_creation').text();
      }
      return;
    });
    */
  }
};


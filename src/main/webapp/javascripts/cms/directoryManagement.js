/**
 * 
 */

var page$ = {
  draggableProperties: {
    'start': function() {
      $('.ch_normal').RemoveBubblePopup();
    },
    'stop': function() {
      page$.channelAndSetPool.initBubbles();
    },
    'appendTo': '#directory_list_ul',
    'disabled': false,
    'opacity':  0.5,
    'helper':   'clone',
    'scroll':   false,
    'revert':   'invalid',
    'zIndex':   2700
  },
  droppableProperties: {
    accept: 'li',
    hoverClass: 'ch_drag',
    drop: function(event, ui) {
      var from = $(ui.draggable);
      var to = $(event.target).parent();
      var categoryId = to.attr('id');
      var type = from.find('input[name="type"]').val();
      var objectId = from.find('input[name="id"]').val();
      var parameters = {
        'categoryId': categoryId
      };
      if (type == 'channelSet') {
        var url = '/CMSAPI/createCategoryChannelSet';
        parameters['setId'] = objectId;
      } else {
        var url = '/CMSAPI/createCategoryChannel';
        parameters['channelId'] = objectId;
      }
      cms.post(url, parameters, function(response) {
        if (response != 'OK') {
          alert($('#lang_warning_error_occurs').text());
          return;
        }
        to.find('> ul > li[rel!="folder"]').remove();
        page$.loadCategoryChannelsAndChannelSets(categoryId);
      }, 'text');
    }
  },
  populateBubbleChannelSetContent: function(channelSet) {
    var label_channel_set = $('#lang_label_channel_set').text();
    var label_update_time = $('#lang_label_update_time').text();
    var span = $('<span></span>');
    var h2 = $('<h2 class="popTitle"></h2>').text('[' + label_channel_set + ']' + channelSet.name).appendTo(span);
    
    var innerHtml = span.html();
    innerHtml += '<br/>' + label_update_time + ' : ' + cms.formatDate(channelSet.updateDate);
    
    return innerHtml;
  },
  populateBubbleChannelContent: function(channel) {
    var label_program_count = $('#lang_label_program_count').text();
    var label_update_time = $('#lang_label_update_time').text();
    var span = $('<span></span>');
    var h2 = $('<h2 class="popTitle"></h2>').text(channel.name).appendTo(span);
    
    var innerHtml = span.html();
    innerHtml += '<br/>' + label_program_count + ' : ' + channel.programCount;
    innerHtml += '<br/>' + label_update_time + ' : ' + cms.formatDate(channel.updateDate);
    
    return innerHtml;
  },
  loadCategoryChannelsAndChannelSets: function(categoryId) {
    // load channels
    cms.post('/CMSAPI/listCategoryChannels', { 'categoryId': categoryId }, function(channels) {
      for (var i in channels) {
        var channelId = channels[i].key.id;
        $('#treeview').jstree('create', '#' + categoryId, 'last', {
          attr: {
            rel: 'file',
            id: channelId
          },
          data: channels[i].name
        }, null, true);
        $('.ch_normal input[name="id"][value="'+channelId+'"]')
          .parent()
          .removeClass('ch_normal')
          /*.removeClass('jstree-draggable')*/
          .draggable('disable')
          .addClass('ch_disable')
          .RemoveBubblePopup();
      }
    }, 'json');
    // load channel sets
    cms.post('/CMSAPI/listCategoryNnSets', { 'categoryId': categoryId }, function(channelSets) {
      for (var i in channelSets) {
        var channelSetId = channelSets[i].key.id;
        $('#treeview').jstree('create', '#' + categoryId, 'last', {
          attr: {
            rel: 'set',
            id: channelSetId
          },
          data: channelSets[i].name
        }, null, true);
        $('.ch_normal input[name="id"][value="'+channelSetId+'"]')
          .parent()
          .removeClass('ch_normal')
          /*.removeClass('jstree-draggable')*/
          .draggable('disable')
          .addClass('ch_disable')
          .RemoveBubblePopup();
      }
    }, 'json');
  },
  composeCategoryTreeData: function(categories, all) {
    var result = $('<div/>');
    var ul = $('<ul/>')
    for (var i = 0; i < categories.length; i++) {
      var categoryId = categories[i].key.id;
      var item = $('<li/>').attr('rel', 'folder').attr('id', categoryId);
      var a = $('<a/>').attr('href', 'javascript:').text(categories[i].name);
      item.append(a);
      var sub = [];
      for (var j = 0; j < all.length; j++) {
        if (all[j].parentId == categoryId) {
          sub.push(all[j]);
        }
      }
      if (sub.length > 0) {
        item.append(page$.composeCategoryTreeData(sub, all));
      }
      page$.directoryArea.arrCategoryIds.push(categoryId);
      //page$.loadCategoryChannelsAndChannelSets(categoryId);
      //item.droppable(page$.droppableProperties);
      ul.append(item);
    }
    result.append(ul);
    return result.html();
  },
  directoryArea: {
    arrCategoryIds: [],
    init: function() {
      $('#btn_delete_directory').button().unbind('click').click(function() {
        var selected = $('#treeview').jstree('get_selected');
        if ($(selected).attr('rel') != 'folder') {
          alert($('#lang_warning_you_must_select_directory').text());
          return;
        } else if ($(selected).parent().parent().attr('id') == 'treeview') {
          alert($('#lang_warning_cannot_remove_root').text());
          return;
        }
        var categoryId = $(selected).attr('id');
        var categoryName = $(selected).find('> a').text();
        var confirmMessage = $('#lang_confirm_remove_directory').text();
        if (confirm(confirmMessage.replace('{categoryName}', categoryName)) == false) {
          return;
        }
        cms.post('/CMSAPI/removeCategory', { 'categoryId': categoryId }, function(response) {
          if (response != 'OK') {
            alert($('#lang_warning_error_occurs').text());
            return;
          }
          $('#treeview').jstree('remove', selected);
        }, 'text');
      });
      $('#btn_create_directory').button().unbind('click').click(function() {
        var selected = $('#treeview').jstree('get_selected');
        if ($(selected).attr('rel') != 'folder') {
          alert($('#lang_warning_you_must_select_directory').text());
          return;
        }
        var name = new Date().getTime().toString();
        var parameters = {
          msoId: $('#msoId').val(),
          parentId: $(selected).attr('id'),
          name: name
        };
        cms.post('/CMSAPI/createCategory', parameters, function(categoryId) {
          if (!categoryId.toString().match(/^[0-9]+$/) || categoryId == 0) {
            alert($('#lang_warning_error_occurs').text());
            return;
          }
          $('#treeview').jstree('create', selected, 'first', {
            attr: {
              rel: 'folder',
              id: categoryId
            },
            data: name
          }, function(data) {
          }, false);
        }, 'text');
      });
      $('#btn_rename_directory').button().unbind('click').click(function() {
        var selected = $('#treeview').jstree('get_selected');
        if ($(selected).attr('rel') != 'folder') {
          alert($('#lang_warning_you_must_select_directory').text());
          return;
        }
        $('#treeview').jstree('rename');
      });
      cms.post('/CMSAPI/listCategories', { 'msoId': $('#msoId').val() }, function(categories) {
        var root = [];
        for (var i = 0; i < categories.length; i++) {
          if (categories[i].parentId == 0) {
            root.push(categories[i]);
          }
        }
        $('#treeview').bind('rename.jstree', function(event, data) {
          var selected = $('#treeview').jstree('get_selected');
          var categoryId = $(selected).attr('id');
          var newName = data.rslt.new_name;
          var oldName = data.rslt.old_name;
          if (newName != oldName) {
            var parameter = {
              'categoryId': categoryId,
              'name': newName
            };
            cms.post('/CMSAPI/renameCategory', parameter, function(response) {
              if (response != 'OK')
                alert($('#lang_warning_error_occurs').text());
            }, 'text');
          }
        });
        $('#treeview').bind('create.jstree', function(event, data) {
          var folder = data.rslt.obj;
          if (folder.attr('rel') != 'folder') {
            return;
          }
          var categoryId = folder.attr('id');
          var name = data.rslt.name;
          var parameter = {
            'categoryId': categoryId,
            'name': name
          };
          cms.post('/CMSAPI/renameCategory', parameter, function(response) {
            if (response != 'OK')
              alert($('#lang_warning_error_occurs').text());
          }, 'text');
          folder.find('> a').droppable(page$.droppableProperties).effect('highlight', null, 'slow');
        });
        $('#treeview').bind('move_node.jstree', function(event, data) {
          var toCategoryId = $(data.rslt.np).attr('id');
          var type = $(data.rslt.o).attr('rel');
          var objectId = $(data.rslt.o).attr('id');
          var fromCategoryId = $(data.rslt.op).attr('id');
          
          var url;
          var parameters = {
            'toCategoryId': toCategoryId,
            'fromCategoryId': fromCategoryId
          }
          if (type == 'file') {
            url = '/CMSAPI/moveCategoryChannel';
            parameters['channelId'] = objectId;
          } else if (type == 'set') {
            url = '/CMSAPI/moveCategoryNnSet';
            parameters['setId'] = objectId;
          } else if (type == 'folder') {
            url = '/CMSAPI/moveCategory';
            parameters['categoryId'] = objectId;
          } else {
            return;
          }
          cms.post(url, parameters, function(response) {
            if (response != 'OK') {
              alert($('#lang_warning_error_occurs').text());
            }
          }, 'text');
        });
        $('#treeview').bind('loaded.jstree', function(event, data) {
          $('li[rel="folder"] > a').droppable(page$.droppableProperties).effect('highlight', null, 'slow');
          for (var i in page$.directoryArea.arrCategoryIds) {
            var categoryId = page$.directoryArea.arrCategoryIds[i];
            page$.loadCategoryChannelsAndChannelSets(categoryId);
          }
        });
        var html = page$.composeCategoryTreeData(root, categories);
        $('#treeview').html(html);
        $('#treeview').jstree({
          'dnd': {
            'drop_finish': function(data) {
              var categoryId = data.o.parent().parent().attr('id');
              var objectId = data.o.attr('id');
              var type = data.o.attr('rel');
              var url;
              var parameters = {
                'categoryId': categoryId
              };
              if (type == 'file') {
                url = '/CMSAPI/removeCategoryChannel';
                parameters['channelId'] = objectId;
              } else if (type == 'set') {
                url = '/CMSAPI/removeCategoryChannelSet';
                parameters['setId'] = objectId;
              } else {
                alert($('#lang_warning_cannot_drag_directory').text());
                return;
              }
              cms.post(url, parameters, function(response) {
                if (response != 'OK') {
                  alert($('#lang_warning_error_occurs').text());
                  return;
                }
                $('#treeview').jstree('remove', data.o);
                var ch = $('.ch_disable input[name="id"][value="'+objectId+'"]')
                           .parent()
                           .removeClass('ch_disable')
                           .addClass('ch_normal')
                           /*.addClass('jstree-draggable');*/
                           .draggable('enable');
                cms.bubblePopupProperties['innerHtml'] = ch.find('span').html();
                ch.CreateBubblePopup(cms.bubblePopupProperties);
              }, 'text')
            }
          },
          'types': {
            'valid_children': ['folder'],
            'types': {
              'folder': {
                'valid_children': ['folder', 'file', 'set'],
                'icon': { 'image': cms.getExternalRootPath() + '/javascripts/themes/default/folder.png' }
              },
              'file': {
                'valid_children': ['none'],
                'max_depth': 1,
                'icon': { 'image': cms.getExternalRootPath() + '/javascripts/themes/default/file.png' }
              },
              'set': {
                'valid_children': ['none'],
                'max_depth': 1,
                'icon': { 'image': cms.getExternalRootPath() + '/javascripts/themes/default/set.png' }
              },
              'default': {
              }
            }
          },
          'crrm': {
            'move': {
              'check_move': function(data) {
                var parent = $(data.o).parent().parent();
                var dest = $(data.r);
                if (parent.attr('id') == 'treeview') { // root can not be moved
                  return false;
                }
                if (dest.attr('rel') != 'folder') { // destination must be a folder
                  return false;
                }
                if (data.cr == -1) {
                  return false
                }
                return true;
              }
            }
          },
          'themes': {
            'theme': 'default',
            'dots':  true,
            'icons': true,
            'url':   cms.getExternalRootPath() + '/javascripts/themes/default/style.css'
          },
          'ui': {
            'select_limit': 1
          },
          'plugins': ['themes', 'html_data', 'types', 'ui', 'crrm', 'dnd']
        });
      }, 'json');
    }
  },
  channelAndSetPool: {
    onload: null,
    initilized: false,
    initBubbles: function() {
      $('.ch_normal').each(function() {
        cms.bubblePopupProperties['innerHtml'] = $(this).find('span').html();
        $(this).CreateBubblePopup(cms.bubblePopupProperties);
      });
    },
    init: function() {
      cms.post('/CMSAPI/listOwnedChannelSets', { 'msoId': $('#msoId').val() }, function(channelSets) {
        for (var i = 0; i < channelSets.length; i++) {
          var item = $('<li class="ch_normal"/>');
          var img = $('<img/>').attr('src', channelSets[i].imageUrl);
          var p = $('<p class="ch_name"></p>').text(channelSets[i].name);
          var type = $('<input type="hidden" name="type"/>').val('channelSet');
          var hidden = $('<input type="hidden" name="id"/>').val(channelSets[i].key.id);
          item.append(img).append(p).append(type).append(hidden).appendTo('#directory_list_ul');
          var innerHtml = page$.populateBubbleChannelSetContent(channelSets[i]);
          $('<span></span>').html(innerHtml).hide().appendTo(item);
          item/*.addClass('jstree-draggable')*/.draggable(page$.draggableProperties).css('z-index', 100);
          item.children().each(function() {
            $(this).css('z-index', 1);
          });
        }
        cms.post('/CMSAPI/listOwnedChannels', { 'msoId': $('#msoId').val() }, function(channels) {
          for (var i = 0; i < channels.length && channels[i].name != null; i++) {
            var item = $('<li class="ch_normal"/>');
            var img = $('<img/>').attr('src', channels[i].imageUrl);
            var p = $('<p class="ch_name"></p>').text(channels[i].name);
            var type = $('<input type="hidden" name="type"/>').val('channel');
            var hidden = $('<input type="hidden" name="id"/>').val(channels[i].key.id);
            item.append(img).append(p).append(type).append(hidden).appendTo('#directory_list_ul');
            var innerHtml = page$.populateBubbleChannelContent(channels[i]);
            $('<span></span>').html(innerHtml).hide().appendTo(item);
            item/*.addClass('jstree-draggable')*/.draggable(page$.draggableProperties).css('z-index', 100);
            item.children().each(function() {
              $(this).css('z-index', 1);
            });
          }
          $('#directory_list_ul').append('<div style="clear:both"/>');
          page$.channelAndSetPool.initBubbles();
          page$.channelAndSetPool.initilized = true;
          if (page$.channelAndSetPool.onload != null && typeof page$.channelAndSetPool.onload == 'function') {
            page$.channelAndSetPool.onload();
          }
        }, 'json');
      }, 'json');
    }
  },
  init: function() {
    cms.loadScript(cms.getExternalRootPath() + '/javascripts/plugins/jquery.jstree.js', function() {
      page$.channelAndSetPool.onload = page$.directoryArea.init;
      page$.channelAndSetPool.init();
    });
  }
};

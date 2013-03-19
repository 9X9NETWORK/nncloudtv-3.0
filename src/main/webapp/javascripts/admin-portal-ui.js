var constants = {
  channelContentType: {
    0: 'Unknwon',
    1: 'System',
    2: 'Podcast',
    3: 'YouTube Channel',
    4: 'YouTube Playlist',
    5: 'Facebook',
    6: '9x9',
    7: 'Slide',
    8: 'Maple Variety',
    9: 'Maple Soap'
  },
  channelStatus: {
    0:    'Success',
    1:    'Error',
    2:    'Processing',
    3:    'Wait For Approval',
    51:   'Invalid format',
    53:   'URL Not Found',
    100:  'No Valid Episode',
    101:  'Bad Quality',
    1000: 'Tanscoding DB Error',
    1001: 'NNVMSO Json Error'
  },
  msoType: {
    0: 'Unknown',
    1: 'NN',
    2: 'MSO',
    3: '3X3',
    4: 'TCO',
    5: 'Enterprise'
  },
  userType: {
    0: 'Unknown',
    1: 'Admin',
    2: 'TBC',
    3: 'TCO',
    4: 'User',
    5: 'NN',
    6: '3x3',
    7: 'Enterprise'
  }
};

var utils = {
  callbackAfterSubmitForm: function(response, postData, formId) {
    if (response.responseText != 'OK') {
      if (response.responseText.length > 50)
        alert('Error Occurs');
      else
        alert(response.responseText);
    }
  }
};

var page$ = {
  tabSetManagement: {
    gridSetList: {
	    gridChannelList: {
 	     properties: {
	      colModel: [
	      {
	        label:    'Channel ID',
	        name:     'channel',
	        index:    'channelId',
	        width:    100,
	        align:    'center',
	        sortable: true,
	        editable: true,
	        editrules: {
	          required: true,
	          number:   true
	        }
	      },
	      {
	        label:    'Channel Name',
	        name:     'name',
	        index:    'name',
	        width:    180,
	        align:    'center',
	        sortable: false
	      },
	      {
		    label:    'Seq',
		    name:     'seq',
		    index:    'seq',
		    width:    180,
		    align:    'center',
		    editable: true,
		    sortable: true
		  },	      
	      {
	        label:     'Type',
	        name:      'type',
	        index:     'type',
	        width:     150,
	        align:     'center',
	        sortable:  true,
	        formatter: 'select',
	        edittype:  'select',
	        editoptions: {
			  value:    constants.channelContentType
	        }
	      }
	    ],
	    url:         null,
	    datatype:    'json',
	    caption:     'Channel List',
	    sortname:    'updateDate',
	    sortorder:   'desc',
	    rowNum:      10,
	    rowList:     [10, 20, 30, 40, 50, 100, 200],
	    viewrecords: true,
	    height:      'auto',
	    hidegrid:    false,
	    toppager:    true,
	    ondblClickRow: null,
	    gridComplete: function() {
	      var totaltime = $(this).jqGrid('getGridParam', 'totaltime');
	      $(this).jqGrid('setCaption', 'Channel List (' + totaltime + 'ms)');
	    }
	  },
	  init: function(subgridId, rowId) {		  
	    var subgridTableId = subgridId + '_t';
	    $('#' + subgridId).html('<table id="' + subgridTableId + '"></table>');
	    $('#' + subgridId).attr('style', 'padding: 5px 5px 5px 5px');
	    var grid$ = page$.tabSetManagement.gridSetList.gridChannelList;
	    grid$.properties.url = '/admin/set/listCh?set=' + rowId;
	    grid$.properties.ondblClickRow = function(subRowId) {
	      $(this).jqGrid('editGridRow', subRowId,
	      {
	        url:               '/admin/set/editCh?set=' + rowId,
	        caption:           'Edit Channel',
	        width:             'auto',
	        top:               $(window).scrollTop() + 100, // $(this).offset().top - 50,
	        left:              200, // $(this).offset().left + $(this).width() + 20,
	        modal:             false,
	        jqModal:           true,
	        closeAfterEdit:    true,
	        closeOnEscape:     true,
	        reloadAfterSubmit: true,
	        viewPagerButtons:  false,
	        recreateForm:      true,
	        afterComplete:     utils.callbackAfterSubmitForm
	      });
	    };
	    $('#' + subgridTableId).jqGrid(grid$.properties);
	    $('#' + subgridTableId).jqGrid('navGrid', '#' + subgridTableId + '_toppager',
	    {
	      edit:     false,
	      add:      true,
	      del:      true,
	      search:   false,
	      view:     false,
	      addtitle: 'Add A New Channel',
	      deltitle: 'Remove A Channel'
	    },
	    {
	      // 'edit' properties
	    },
	    {
	      // 'add' properties
	      url:               '/admin/set/addCh?set=' + rowId,
	      addCaption:        'Add A Channel',
	      width:             'auto',
	      modal:             false,
	      jqModal:           true,
	      closeAfterAdd:     true,
	      closeOnEscape:     true,
	      reloadAfterSubmit: true,
	      beforeShowForm: function(formId)
	      {
	        $('#channel', formId).attr('disabled', false);
	      },
	      afterComplete: utils.callbackAfterSubmitForm
	    },
	    {
	      // 'delete' properties
	      url:               '/admin/set/deleteCh?set=' + rowId,
	      //grid$.properties.url = '/admin/msoIpg/list?mso=' + rowId;
	      caption:           'Remove A Channel',
	      msg:               'Do you want to remove a channel?',
	      width:             'auto',
	      modal:             false,
	      jqModal:           true,
	      closeOnEscape:     true,
	      reloadAfterSubmit: true,
	      afterComplete: utils.callbackAfterSubmitForm
	    });
	  }
	},	
      properties: {
        colModel: [
          {
            label:    'Set ID',
            name:     'id',
            index:    'id',
            width:    80,
            align:    'center',
            sortable: false,
            editable: false
          },
          {
              label:    'Set Name',
              name:     'name',
              index:    'name',
              width:    150,
              align:    'center',
              search:   false,
              sortable: true,
              editable: true,
              editrules: {
                required: true
              },
              editoptions: {
                maxlength: 100
              }
          },
          {
              label:    'Set Description',
              name:     'intro',
              index:    'intro',
              width:    280,
              align:    'center',
              search:   false,
              sortable: true,
              //hidden:   false,
              editable: true,
              edittype: 'textarea',
              editoptions: {
                rows: '3'
              }
          },
          {
            label:     'Recommended',
            name:      'featured',
            index:     'featured',
            width:     100,
            align:     'center',
            sortable:  true,
            formatter: 'checkbox',
            editable:  true,
            edittype:  'checkbox',
            editoptions: {
              value: 'true:false'
            }
          },
          {
              label:     'Public',
              name:      'isPublic',
              index:     'isPublic',
              width:     60,
              align:     'center',
              sortable:  true,
              formatter: 'checkbox',
              editable:  true,
              edittype:  'checkbox',
              editoptions: {
                value: 'true:false'
              }
          },
          {
              label:     'Language',
              name:      'lang',
              index:     'lang',
              width:     70,
              align:     'center',
              search:    true,
              stype:     'select',
              sortable:  true,
              formatter: 'select',
              editable:  true,
              edittype:  'select',
              editoptions: {
                value: 'en:en;zh:zh'
              },
              searchoptions: {
                  sopt:  ['eq'],
                  value: "en:en;zh:zh"
                }              
          },  
          {	
              label:     'Seq(rec)',
              name:      'seq',
              index:     'seq',
              width:     60,
              align:     'center',
              sortable:  true,
              editable:  true,
              editrules: {
                integer:  true
              }
          },
          {	
              label:     'Brand URL',
              name:      'beautifulUrl',
              index:     'beautifulUrl',
              width:     80,
              align:     'center',
              sortable:  true,
              editable:  true,
              editrules: {
                required: false
              }
          },                    
          {
        	  label:    'Updated Time',
              name:     'updateDate',
              index:    'updateDate',
              width:    140,
              align:    'center',
              sortable: true,
              editable: false,
              hidden:   false,
              editrules: {
                edithidden: true
              }
          },          
          {
            label:    'Image',
            name:     'imageUrl',
            index:    'imageUrl',
            width:    150,
            align:    'center',
            search:   false,
            sortable: true,
            //hidden:   false,
            editable: true,
            editoptions: {
              rows: '3'
            }
          },
        ],
        datatype:    'json',
        url:         '/admin/set/list',
        caption:     'Set List',
        sortname:    'lang, name',
        sortorder:   'asc',
        rowNum:      200,
        rowList:     [10, 20, 30, 40, 50, 100, 200],
        viewrecords: true,
        height:      'auto',
        hidegrid:    false,
        toppager:    true,
        gridComplete: function() {
          var totaltime = $(this).jqGrid('getGridParam', 'totaltime');
          $(this).jqGrid('setCaption', 'Set List (' + totaltime + 'ms)');
        },
        ondblClickRow: function(rowId) {
          var logoUrl = $(this).jqGrid('getCell', rowId, 'logoUrl');
          $(this).jqGrid('setColProp', 'logoUrl', {editoptions: {src: logoUrl}});
          $(this).jqGrid('editGridRow', rowId, {
            url:               '/admin/set/edit',
            caption:           'Edit Set Meta',
            width:             'auto',
            top:               $(window).scrollTop() + 50, // $(this).offset().top - 50,
            left:              200, // $(this).offset().left + $(this).width() + 20,
            modal:             false,
            jqModal:           true,
            closeAfterEdit:    true,
            closeOnEscape:     true,
            reloadAfterSubmit: true,
            viewPagerButtons:  true,
            recreateForm:      true,
            beforeCheckValues: function(postData, formId, mode) {
              postData['logoUrl'] = $('#logoUrl', formId).attr('src');
              return postData;
            },
            beforeShowForm: function(formId) {
            },
            afterclickPgButtons: function(whichButton, formId, rowId) {
              var logoUrl = $('#mso_table').jqGrid('getCell', rowId, 'logoUrl');
              $('#logoUrl', formId).attr('src', logoUrl);
            },
            afterComplete: utils.callbackAfterSubmitForm
          });
        }
      },
      init: function() {
        this.properties.subGrid = true;
        this.properties.subGridRowExpanded = this.gridChannelList.init;
        $('#set_table').jqGrid(this.properties);
        $('#set_table').jqGrid('navGrid', '#set_table_toppager',
        {
          edit:   false,
          add:    true,
          del:    true,
          search: true,
          view:   false,
          addtitle: 'Create A New Set'
        },
        {
          // 'edit' properties
        },
        {
          // 'add' properties
          url:               '/admin/set/create',
          addCaption:        'Create A New Set',
          width:             'auto',
          modal:             false,
          jqModal:           true,
          closeAfterAdd:     true,
          closeOnEscape:     true,
          reloadAfterSubmit: true,
          recreateForm:      true,
          afterComplete: utils.callbackAfterSubmitForm
        },
        {
            // 'delete' properties
            url:               '/admin/set/delete',
            caption:           'Remove A Set',
            msg:               'Do you want to remove it',
            width:             'auto',
            modal:             false,
            jqModal:           true,
            closeOnEscape:     true,
            reloadAfterSubmit: true,
            afterComplete: utils.callbackAfterSubmitForm
          });        
      }
    },
    init: function() {
      this.gridSetList.init();
    }
  },
  /////////////////////////
  tabChannelManagement: {
    gridChannelList: {
      gridProgramList: {
        properties: {
          colModel: [
            {
              label:    'Thumbnail',
              name:     'imageUrl',
              index:    'imageUrl',
              width:    150,
              align:    'center',
              sortable: true,
              hidden:   true,
              editable: true,
              edittype: 'image',
              editoptions: {
                disabled: true
              },
              editrules: {
                edithidden: true
              }
            },
            {
              label:    'Program ID',
              name:     'id',
              index:    'id',
              width:    80,
              align:    'center',
              sortable: false,
              editable: true,
              editoptions: {
                disabled: true
              }
            },
            {
              label:    'Program Name',
              name:     'name',
              index:    'name',
              width:    200,
              align:    'center',
              sortable: true,
              editable: true,
              editoptions: {
                maxlength: 100
              }
            },
            {
              label:    'Updated Time',
              name:     'updateDate',
              index:    'updateDate',
              width:    120,
              align:    'center',
              sortable: true,
              editable: true,
              hidden:   true,
              editrules: {
                edithidden: true
              }
            },
            {
              label:    'Created Time',
              name:     'createDate',
              index:    'createDate',
              width:    120,
              align:    'center',
              sortable: true,
              editable: true,
              hidden:   true,
              editoptions: {
                disabled: true
              },
              editrules: {
                edithidden: true
              }
            },
            {
              label:    'Audio URL',
              name:     'audioFileUrl',
              index:    'audioFileUrl',
              width:    150,
              align:    'center',
              sortable: true,
              hidden:   true,
              editable: true,
              editoptions: {
                disabled: true
              },
              editrules: {
                edithidden: true
              }
            },
            {
              label:     'Status',
              name:      'status',
              index:     'status',
              width:     70,
              align:     'center',
              sortable:  true,
              formatter: 'select',
              editable:  true,
              edittype:  'select',
              editoptions: {
                value: '0:Success;1:Error;101:Bad Quality'
              }
            },
            {
              label:     'Type',
              name:      'type',
              index:     'type',
              width:     70,
              align:     'center',
              sortable:  true,
              formatter: 'select',
              editable:  true,
              edittype:  'select',
              editoptions: {
                disabled: true,
                value:    '0:Unknown;1:Video;2:Audio;3:Slide'
              }
            },
            {
              label:     'Content Type',
              name:      'contentType',
              index:     'contentType',
              width:     100,
              align:     'center',
              sortable:  true,
              formatter: 'select',
              editable:  true,
              edittype:  'select',
              editoptions: {
                disabled: true,
    	        editoptions: {
      			  value:    constants.channelContentType
      	        }
              }
            },
            {
              label:     'Public',
              name:      'isPublic',
              index:     'isPublic',
              width:     70,
              align:     'center',
              sortable:  true,
              formatter: 'checkbox',
              editable:  true,
              edittype:  'checkbox',
              editoptions: {
                value: 'true:false'
              }
            },
            {
              label:    'Duration',
              name:     'duration',
              index:    'duration',
              width:    100,
              align:    'center',
              sortable: true,
              hidden:   true,
              editable: true,
              editoptions: {
                disabled: true
              },
              editrules: {
                edithidden: true
              }
            },
            {
              label:    'Introduction',
              name:     'intro',
              index:    'intro',
              width:    150,
              align:    'center',
              sortable: true,
              hidden:   true,
              editable: true,
              edittype: 'textarea',
              editoptions: {
                rows: '3'
              },
              editrules: {
                edithidden: true
              }
            }
          ],
          datatype:    'json',
          //url:         '/admin/program/list?channel=' + rowId,
          caption:     'Program List',
          sortname:    'updateDate',
          sortorder:   'desc',
          rowNum:      10,
          rowList:     [10, 20, 30, 40, 50, 100, 200],
          viewrecords: true,
          height:      'auto',
          hidegrid:    false,
          toppager:    true,
          gridComplete: function() {
            var totaltime = $(this).jqGrid('getGridParam', 'totaltime');
            $(this).jqGrid('setCaption', 'Program List (' + totaltime + 'ms)');
          },
          ondblClickRow: function(rowId) {
            var imageUrl = $(this).jqGrid('getCell', rowId, 'imageUrl');
            $(this).jqGrid('setColProp', 'imageUrl', {
              editoptions: {
                style: 'max-height:100px',
                src: imageUrl
              }
            });
            $(this).jqGrid('editGridRow', rowId, {
              url:               '/admin/program/modify',
              caption:           'Edit Program Meta',
              width:             'auto',
              top:               $(window).scrollTop() + 50, // $(this).offset().top - 130,
              left:              200, // $(this).offset().left + $(this).width() + 15,
              modal:             false,
              jqModal:           true,
              closeAfterEdit:    true,
              closeOnEscape:     true,
              reloadAfterSubmit: true,
              viewPagerButtons:  true,
              recreateForm:      true,
              beforeCheckValues: function(postData, formId, mode) {
                postData['imageUrl'] = $('#imageUrl', formId).attr('src');
                return postData;
              },
              beforeShowForm: function(formId) {
                $('#imageUrl', formId).click(function() {
                  var imageUrl = prompt('Please enter new image URL', $(this).attr('src'));
                  if (imageUrl != null) {
                    $(this).attr('src', imageUrl);
                  }
                });
              },
              afterclickPgButtons: null,
              afterComplete: utils.callbackAfterSubmitForm
            });
          }
        },
        init: function(subgridId, rowId) {
          var grid$ = page$.tabChannelManagement.gridChannelList.gridProgramList;
          var subgridTableId = subgridId + '_t';
          $('#' + subgridId).html('<table id="' + subgridTableId + '" class="scroll"></table>');
          $('#' + subgridId).attr('style', 'padding: 5px 5px 5px 5px');
          grid$.properties.url = '/admin/program/list?channel=' + rowId;
          grid$.properties.afterclickPgButtons = function(whichButton, formId, rowId) {
            var imageUrl = $('#' + subgridTableId).jqGrid('getCell', rowId, 'imageUrl');
            $('#imageUrl', formId).attr('src', imageUrl);
          };
          $('#' + subgridTableId).jqGrid(grid$.properties);
          $('#' + subgridTableId).jqGrid('navGrid', '#' + subgridTableId + '_toppager', {
            edit:   false,
            add:    false,
            del:    false,
            search: false,
            view:   false
          });
          $('#' + subgridTableId).jqGrid('navButtonAdd', '#' + subgridTableId + '_toppager', {
            caption:    '',
            title:      'Preview Program',
            buttonicon: 'ui-icon-extlink',
            onClickButton: function() {
              var subrowId = $(this).jqGrid('getGridParam', 'selrow');
              if (subrowId == null) { return; }
              var url = 'http://' + location.host + '/view?channel=' + rowId + '&episode=' + subrowId;
              window.open(url, '_player');
            }
          });
        }
      },
      properties: {
        colModel: [
          {
            label:    'Channel ID',
            name:     'id',
            index:    'id',
            width:    80,
            align:    'center',
            search:   true,
            editable: true,
            sortable: true,
            editoptions: {
              disabled: true
            },
            searchoptions: {
              sopt: ['eq']
            }
          },
          {
            label:    'Channel Name',
            name:     'name',
            index:    'name',
            width:    150,
            align:    'center',
            search:   true,
            sortable: true,
            editable: true,
            editoptions: {
              maxlength: 100
            },
            searchoptions: {
              sopt: ['eq']
            }
          },
          {
            label:    'Source',
            name:     'sourceUrl',
            index:    'sourceUrl',
            width:    380,
            align:    'center',
            search:   true,
            sortable: true,
            hidden:   false,
            editable: true,
            editoptions: {
              disabled: false
            },
            editrules: {
              edithidden: true,
              required: false
            },
            searchoptions: {
              sopt: ['eq'],
              searchhidden: true
            }
          },
          {
            label:     'Status',
            name:      'status',
            index:     'status',
            width:     70,
            align:     'center',
            search:    true,
            sortable:  true,
            stype:     'select',
            formatter: 'select',
            editable:  true,
            edittype:  'select',
            editoptions: {
              value: constants.channelStatus
            },
            searchoptions: {
              sopt:  ['eq'],
              value: constants.channelStatus
            }
          },
          {
            label:     'Type',
            name:      'contentType',
            index:     'contentType',
            width:     90,
            align:     'center',
            search:    true,
            stype:     'select',
            sortable:  true,
            formatter: 'select',
            editable:  false,
            edittype:  'select',
            editoptions: {
              disabled: true,
              value:    constants.channelContentType
            },
            searchoptions: {
              sopt:  ['eq'],
              value: constants.channelContentType
            }
          },
          {
            label:     'Public',
            name:      'isPublic',
            index:     'isPublic',
            width:     70,
            align:     'center',
            search:    true,
            stype:     'select',
            sortable:  true,
            formatter: 'checkbox',
            editable:  true,
            edittype:  'checkbox',
            editoptions: {
              value: 'true:false',
              defaultValue: "true"
            },
            searchoptions: {
              sopt:  ['eq'],
              value: {
                'true':  'true',
                'false': 'false'
              }
            }
          },
          {
            label:     'Piwik',
            name:      'piwik',
            index:     'piwik',
            width:     70,
            align:     'center',
            sortable:  true,
            editable:  false,
            editoptions: {
              disabled: true
            }
          },
          {
            label:    'Image',
            name:     'imageUrl',
            index:    'imageUrl',
            width:    50,
            align:    'center',
            sortable: false,
            hidden:   false,
            search:   false,
            editable: false,
            editoptions: {
              disabled: true
            },
            editrules: {
              edithidden: true
            }
          },
          {
              label:    'Updated Time',
              name:     'updateDate',
              index:    'updateDate',
              width:    140,
              align:    'center',
              sortable: true,
              editable: false,
              hidden:   false,
              editrules: {
                edithidden: true
              }
          },                    
          {
            label:    'Ep. Count',
            name:     'programCnt',
            index:    'programCnt',
            width:    90,
            align:    'center',
            search:   false,
            sortable: true,
            hidden:   false,
            editable: false,
            editrules: {
              integer:    true,
              edithidden: true
            }
          },
          {
            label:    'Sub. Count',
            name:     'subscriptionCnt',
            index:    'subscriptionCnt',
            width:    80,
            align:    'center',
            search:   false,
            sortable: false,
            hidden:   false,
            editable: false,
            editrules: {
              integer:    true,
              edithidden: true
            }
          },
        ],
        url:         '/admin/channel/list',
        datatype:    'json',
        caption:     'Channel List',
        sortname:    'updateDate',
        sortorder:   'desc',
        rowNum:      200,
        rowList:     [10, 20, 30, 40, 50, 100, 200],
        viewrecords: true,
        height:      'auto',
        hidegrid:    false,
        toppager:    true,
        gridComplete: function() {
          var totaltime = $(this).jqGrid('getGridParam', 'totaltime');
          $(this).jqGrid('setCaption', 'Channel List (' + totaltime + 'ms)');
        },
        onSelectRow: function(rowId, status) {
          $('#cc_table').attr('title', $(this).jqGrid('getCell', rowId, 'name'));
          $('#cc_table').attr('channel', rowId);
          $('#cc_table').jqGrid('setGridParam', { url: "/admin/channel/listCategories?channel=" + rowId }).trigger('reloadGrid');
        },
        ondblClickRow: function(rowId) {
          var imageUrl = $(this).jqGrid('getCell', rowId, 'imageUrl');
          $(this).jqGrid('setColProp', 'imageUrl', {
            editoptions: {
              style: 'max-height:100px',
              src: imageUrl
            }
          });
          $(this).jqGrid('editGridRow', rowId, {
            url:               '/admin/channel/modify',
            caption:           'Edit Channel Meta',
            width:             'auto',
            top:               $(window).scrollTop() + 50, // $(this).offset().top - 50,
            left:              200, // $(this).offset().left + $(this).width() + 20,
            modal:             false,
            jqModal:           true,
            closeAfterEdit:    true,
            closeOnEscape:     true,
            reloadAfterSubmit: true,
            viewPagerButtons:  true,
            recreateForm:      true,
            beforeCheckValues: function(postData, formId, mode) {
              postData['imageUrl'] = $('#imageUrl', formId).attr('src');
              return postData;
            },
            beforeShowForm: function(formId) {
              $('#imageUrl', formId).click(function() {
                var imageUrl = prompt('Please enter new image URL', $(this).attr('src'));
                if (imageUrl != null) {
                  $(this).attr('src', imageUrl);
                }
              });
            },
            afterclickPgButtons: function(whichButton, formId, rowId) {
              var imageUrl = $('#chn_table').jqGrid('getCell', rowId, 'imageUrl');
              $('#imageUrl', formId).attr('src', imageUrl);
            },
            afterComplete: utils.callbackAfterSubmitForm
          });
        }
      },
      init: function() {
        var grid$ = page$.tabChannelManagement.gridChannelList;
        grid$.properties.subGridRowExpanded = grid$.gridProgramList.init;
        grid$.properties.subGrid = true;
        $('#chn_table').jqGrid(grid$.properties);
        $('#chn_table').jqGrid('navGrid', '#chn_table_toppager', {
          edit:   false,
          add:    true,
          del:    false,
          search: true,
          view:   false
        }, 
        {
        	// 'edit' properties
        }, 
        {
            // 'add' properties
            url:               '/admin/channel/create',
            addCaption:        'Create A New Channel',
            width:             'auto',
            modal:             false,
            jqModal:           true,
            closeAfterAdd:     true,
            closeOnEscape:     true,
            reloadAfterSubmit: true,
            recreateForm:      true,
            afterComplete: utils.callbackAfterSubmitForm        	
        }, 
        {}, {
          closeAfterSearch: true,
          closeAfterReset:  true,
          closeOnEscape:    true
        });
        $('#chn_table').jqGrid('navButtonAdd', '#chn_table_toppager', {
          caption:    '',
          title:      'Preview Channel',
          buttonicon: 'ui-icon-extlink',
          onClickButton: function() {
            var rowId = $(this).jqGrid('getGridParam', 'selrow');
            if (rowId == null) { return; }
            var url = 'http://' + location.host + '/view?channel=' + rowId;
            window.open(url, '_player');
          }
        });
      }
    },

    init: function() {
      var tab$ = page$.tabChannelManagement;
      tab$.gridChannelList.init();
      //tab$.gridCategoryList.init();
    }
  },
  tabCategoryManagement: {
    gridCategoryList: {
      gridSetList: {
        properties: {
          colModel: [
            {
              label:    'Category ID',
              name:     'category',
              index:    'categoryId',
              width:    80,
              align:    'center',
              hidden:   true,
              sortable: true,
              editable: true,
              editoptions: {
                disabled: true
              },
              editrules: {
                edithidden: true,
                number:     true
              }
            },
            {
              label:    'Set ID',
              name:     'set',
              index:    'setId',
              width:    80,
              align:    'center',
              sortable: true,
              editable: true,
              editoptions: {
                disabled: false
              },
              editrules: {
                number: true
              }
            },
            {
              label:    'Set Name',
              name:     'name',
              index:    'name',
              width:    200,
              align:    'center',
              sortable: false,
              editable: false,
              editoptions: {
                maxlength: 100
              }
            },
            {
                label:     'Public',
                name:      'isPublic',
                index:     'isPublic',
                width:     70,
                align:     'center',
                search:    false,
                sortable:  false,
                formatter: 'checkbox',
                editable:  false,
                edittype:  'checkbox',
                editoptions: {
                  value:    'true:false'
                }
            },            
            {
              label:     'Language',
              name:      'lang',
              index:     'lang',
              width:     70,
              align:     'center',
              search:    false,
              stype:     'select',
              sortable:  false,
              formatter: 'select',
              editable:  false,
              edittype:  'select',
              editoptions: {
                disabled: false,
                value:    "en:en;zh:zh"
              },
              searchoptions: {
                sopt:  ['eq'],
                value: "en:en;zh:zh"
              }
            }
          ],
          datatype:    'json',
          //url:         '/admin/category/channelList?category=' + rowId,
          caption:     'Category Set List',
          sortname:    'updateDate',
          sortorder:   'desc',
          rowNum:      10,
          rowList:     [10, 20, 30, 40, 50, 100, 200],
          viewrecords: true,
          height:      'auto',
          hidegrid:    false,
          toppager:    true,
          ondblClickRow: function() {
            // empty, but don't remove it
          },
          gridComplete: function() {
            var totaltime = $(this).jqGrid('getGridParam', 'totaltime');
            $(this).jqGrid('setCaption', 'Category Set List (' + totaltime + 'ms)');
          }
        },
        init: function(subgridId, rowId) {
          var grid$ = page$.tabCategoryManagement.gridCategoryList.gridSetList;
          var subgridTableId = subgridId + '_t';
          $('#' + subgridId).html('<table id="' + subgridTableId + '" category="' + rowId + '"></table>');
          $('#' + subgridId).attr('style', 'padding: 5px 5px 5px 5px');
          grid$.properties.url = '/admin/category/listSet?category=' + rowId;
          $('#' + subgridTableId).jqGrid(grid$.properties);
          $('#' + subgridTableId).jqGrid('navGrid', '#' + subgridTableId + '_toppager',
          {
            edit:     false,
            add:      true,
            del:      true,
            search:   false,
            view:     false,
            addtitle: 'Add A Set to This Category',
            deltitle: 'Remove This Set from Category'
          },
          {
            // 'edit' properties
          },
          {
            // 'add' properties
            url:               '/admin/category/addSet',
            addCaption:        'Add Set to This Category',
            width:             'auto',
            modal:             false,
            jqModal:           true,
            closeAfterAdd:     true,
            closeOnEscape:     true,
            reloadAfterSubmit: true,
            beforeShowForm: function(formId)
            {
              $('#category', formId).val($('#' + subgridTableId).attr('category'));
            },
            afterComplete: utils.callbackAfterSubmitForm
          },
          {
            // 'delete' properties
            url:               '/admin/category/deleteSet?category=' + rowId,
            caption:           'Remove A Set',
            msg:               'Do you want to remove a set from Category ?',
            width:             'auto',
            modal:             false,
            jqModal:           true,
            closeOnEscape:     true,
            reloadAfterSubmit: true,
            afterComplete: utils.callbackAfterSubmitForm
          });
        }
      },
      properties: {
        colModel: [
          {
            label:    'Category ID',
            name:     'id',
            index:    'id',
            width:    80,
            align:    'center',
            search:   false,
            sortable: false
          },
          {
            label:    'Category Name',
            name:     'name',
            index:    'name',
            width:    200,
            align:    'center',
            search:   false,
            sortable: true,
            editable: true,
            editoptions: {
              maxlength: 100
            },
            editrules: {
              required: true
            }
          },
          {
            label:    'Updated Time',
            name:     'updateDate',
            index:    'updateDate',
            width:    140,
            align:    'center',
            search:   false,
            sortable: true,
            editable: false,
            editoptions: {
              disabled: true
            }
          },
          {
            label:     'Language',
            name:      'lang',
            index:     'lang',
            width:     70,
            align:     'center',
            search:    true,
            stype:     'select',
            sortable:  true,
            formatter: 'select',
            editable:  true,
            edittype:  'select',
            editoptions: {
              disabled: false,
              value:    "en:en;zh:zh"
            },
            searchoptions: {
              sopt:  ['eq'],
              value: "en:en;zh:zh"
            }
          },
          {
            label:     'Public',
            name:      'isPublic',
            index:     'isPublic',
            width:     70,
            align:     'center',
            search:    false,
            sortable:  true,
            formatter: 'checkbox',
            editable:  true,
            edittype:  'checkbox',
            editoptions: {
              value:    'true:false'
            }
          },
	      {
  		    label:    'Seq',
  		    name:     'seq',
  		    index:    'seq',
  		    width:    180,
  		    align:    'center',
  		    editable: true,
  		    sortable: true,
            editrules: {
              integer:    true,
              edithidden: true,
              minValue: 1,
              required: true          
            }
  		  },
          {
            label:    'Ch Cnt',
            name:     'channelCnt',
            index:    'channelCnt',
            width:    90,
            align:    'center',
            search:   false,
            sortable: true,
            hidden:   false,
            editable: true,
            editrules: {
              integer:    true,
              edithidden: true,
              minValue: 0
            }
          }
        ],
        url:         '/admin/category/list',
        datatype:    'json',
        caption:     'Category List',
        sortname:    'lang, seq',
        sortorder:   'asc',
        rowNum:      200,
        rowList:     [10, 20, 30, 40, 50, 100, 200],
        viewrecords: true,
        height:      'auto',
        hidegrid:    false,
        toppager:    true,
        gridComplete: function() {
          var totaltime = $(this).jqGrid('getGridParam', 'totaltime');
          $(this).jqGrid('setCaption', 'Category List (' + totaltime + 'ms)');
        },
        ondblClickRow: function(rowId) {
          $(this).jqGrid('editGridRow', rowId, {
            url:               '/admin/category/edit',
            caption:           'Edit Category',
            width:             'auto',
            top:               $(window).scrollTop() + 100, // $(this).offset().top - 50,
            left:              200, // $(this).offset().left + $(this).width() + 20,
            modal:             false,
            jqModal:           true,
            closeAfterEdit:    true,
            closeOnEscape:     true,
            reloadAfterSubmit: true,
            viewPagerButtons:  false,
            recreateForm:      true,
            afterComplete: utils.callbackAfterSubmitForm
          });
        }
      },
      init: function() {
        var grid$ = page$.tabCategoryManagement.gridCategoryList;
        grid$.properties.subGrid = true;
        grid$.properties.subGridRowExpanded = grid$.gridSetList.init;
        $('#cat_table').jqGrid(grid$.properties);
        $('#cat_table').jqGrid('navGrid', '#cat_table_toppager',
        {
          edit:   false,
          add:    true,
          del:    true,
          search: true,
          view:   false,
          addtitle: 'Create A New Category'
        },
        {
          // 'edit' properties
        },
        {
          // 'add' properties
          url:               '/admin/category/create',
          addCaption:        'Create A New Category',
          width:             'auto',
          modal:             false,
          jqModal:           true,
          closeAfterAdd:     true,
          closeOnEscape:     true,
          reloadAfterSubmit: true,
          recreateForm:      true,
          beforeShowForm: function(formId)
          {
            $('#tr_channelCount').hide();
            $('#isPublic', formId).attr('checked', true);
          },
          afterComplete: utils.callbackAfterSubmitForm
        },
	    {
    	      // 'delete' properties
    	      url:               '/admin/category/delete',
    	      caption:           'Remove A Category',
    	      msg:               'Do you want to remove a category?',
    	      width:             'auto',
    	      modal:             false,
    	      jqModal:           true,
    	      closeOnEscape:     true,
    	      reloadAfterSubmit: true,
    	      afterComplete: utils.callbackAfterSubmitForm
    	},        
        {	
          // 'search' properties
        },
        {
          closeAfterSearch: true,
          closeAfterReset:  true,
          closeOnEscape:    true
        });        
      }
    },
    init: function() {
      var tab = page$.tabCategoryManagement;
      tab.gridCategoryList.init();
    }
  },
  tabMsoManagement: {
    gridMsoList: {
      gridMsoIpgList: {
        properties: {
          colModel: [
            {
              label:    'Channel ID',
              name:     'channel',
              index:    'channelId',
              width:    100,
              align:    'center',
              sortable: true,
              editable: true,
              editoptions: {
                disabled: true
              },
              editrules: {
                required: true,
                number:   true
              }
            },
            {
              label:    'Channel Name',
              name:     'name',
              index:    'name',
              width:    180,
              align:    'center',
              sortable: false
            },
            {
              label:    'Updated Time',
              name:     'updateDate',
              index:    'updateDate',
              width:    140,
              align:    'center',
              sortable: true
            },
            {
              label:    'Created Time',
              name:     'createDate',
              index:    'createDate',
              width:    140,
              align:    'center',
              sortable: true,
              hidden:   true
            },
            {
              label:     'Seq.',
              name:      'seq',
              index:     'seq',
              width:     60,
              align:     'center',
              sortable:  true,
              editable:  true,
              editrules: {
                minValue: 1,
                maxValue: 81,
                required: true,
                integer:  true
              }
            },
            {
              label:     'Type',
              name:      'type',
              index:     'type',
              width:     70,
              align:     'center',
              sortable:  true,
              formatter: 'select',
              editable:  true,
              edittype:  'select',
              editoptions: {
                value: '1:General;2:Readonly;0:Unknown'
              }
            }
          ],
          url:         null,
          datatype:    'json',
          caption:     'MSO IPG List',
          sortname:    'updateDate',
          sortorder:   'desc',
          rowNum:      10,
          rowList:     [10, 20, 30, 40, 50, 100, 200],
          viewrecords: true,
          height:      'auto',
          hidegrid:    false,
          toppager:    true,
          ondblClickRow: null,
          gridComplete: function() {
            var totaltime = $(this).jqGrid('getGridParam', 'totaltime');
            $(this).jqGrid('setCaption', 'MSO IPG List (' + totaltime + 'ms)');
          }
        },
        init: function(subgridId, rowId) {
          var subgridTableId = subgridId + '_t';
          $('#' + subgridId).html('<table id="' + subgridTableId + '"></table>');
          $('#' + subgridId).attr('style', 'padding: 5px 5px 5px 5px');
          var grid$ = page$.tabMsoManagement.gridMsoList.gridMsoIpgList;
          grid$.properties.url = '/admin/msoIpg/list?mso=' + rowId;
          grid$.properties.ondblClickRow = function(subRowId) {
            $(this).jqGrid('editGridRow', subRowId,
            {
              url:               '/admin/msoIpg/modify?mso=' + rowId, // Qoo
              caption:           'Edit MSO IPG',
              width:             'auto',
              top:               $(window).scrollTop() + 100, // $(this).offset().top - 50,
              left:              200, // $(this).offset().left + $(this).width() + 20,
              modal:             false,
              jqModal:           true,
              closeAfterEdit:    true,
              closeOnEscape:     true,
              reloadAfterSubmit: true,
              viewPagerButtons:  false,
              recreateForm:      true,
              afterComplete:     utils.callbackAfterSubmitForm
            });
          };
          $('#' + subgridTableId).jqGrid(grid$.properties);
          $('#' + subgridTableId).jqGrid('navGrid', '#' + subgridTableId + '_toppager',
          {
            edit:     false,
            add:      true,
            del:      true,
            search:   false,
            view:     false,
            addtitle: 'Add A New Channel to IPG',
            deltitle: 'Remove A Channel From IPG'
          },
          {
            // 'edit' properties
          },
          {
            // 'add' properties
            url:               '/admin/msoIpg/add?mso=' + rowId,
            addCaption:        'Add A Channel to IPG',
            width:             'auto',
            modal:             false,
            jqModal:           true,
            closeAfterAdd:     true,
            closeOnEscape:     true,
            reloadAfterSubmit: true,
            beforeShowForm: function(formId)
            {
              $('#channel', formId).attr('disabled', false);
            },
            afterComplete: utils.callbackAfterSubmitForm
          },
          {
            // 'delete' properties
            url:               '/admin/msoIpg/delete?mso=' + rowId,
            caption:           'Remove A Channel',
            msg:               'Do you want to remove a channel from IPG ?',
            width:             'auto',
            modal:             false,
            jqModal:           true,
            closeOnEscape:     true,
            reloadAfterSubmit: true,
            afterComplete: utils.callbackAfterSubmitForm
          });
        }
      },
      properties: {
        colModel: [
          {
            label:    'MSO Logo',
            name:     'logoUrl',
            index:    'logoUrl',
            width:    150,
            align:    'center',
            search:   false,
            sortable: true,
            hidden:   true,
            editable: true,
            edittype: 'image',
            editoptions: {
              disabled: false
            },
            editrules: {
              edithidden: true
            }
          },
          {
            label:    'MSO ID',
            name:     'id',
            index:    'id',
            width:    80,
            align:    'center',
            search:   false,
            stype:    'select',
            sortable: false,
            editable: true,
            editoptions: {
              disabled: true
            }
          },
          {
            label:    'MSO Name',
            name:     'name',
            index:    'name',
            width:    100,
            align:    'center',
            search:   false,
            sortable: true,
            editable: true,
            editrules: {
              required: true
            },
            editoptions: {
              maxlength: 100
            }
          },
          {
            label:    'MSO Title',
            name:     'title',
            index:    'title',
            width:    100,
            align:    'center',
            search:   false,
            sortable: true,
            editable: true,
            editoptions: {
              maxlength: 100
            }
          },
          {
            label:    'Updated Time',
            name:     'updateDate',
            index:    'updateDate',
            width:    140,
            align:    'center',
            search:   false,
            sortable: true,
            editable: true,
            editoptions: {
              disabled: true
            }
          },
          {
            label:    'Created Time',
            name:     'createDate',
            index:    'createDate',
            width:    140,
            align:    'center',
            search:   false,
            sortable: true,
            editable: true,
            hidden:   true,
            editoptions: {
              disabled: true
            },
            editrules: {
              edithidden: true
            }
          },
          {
            label:    'Jingle URL',
            name:     'jingleUrl',
            index:    'jingleUrl',
            width:    150,
            align:    'center',
            search:   false,
            sortable: true,
            hidden:   true,
            editable: true,
            editrules: {
              edithidden: true
            }
          },
          {
            label:     'Type',
            name:      'type',
            index:     'type',
            width:     70,
            align:     'center',
            search:    true,
            stype:     'select',
            sortable:  true,
            formatter: 'select',
            editable:  true,
            edittype:  'select',
            editoptions: {
              disabled: false,
              value:    constants.msoType
            },
            searchoptions: {
              sopt:  ['eq'],
              value: constants.msoType
            }
          },
          {
            label:     'Language',
            name:      'lang',
            index:     'lang',
            width:     90,
            align:     'center',
            sortable:  true,
            search:    false,
            formatter: 'select',
            editable:  true,
            edittype:  'select',
            editoptions: {
              value: 'en:en;zh:zh'
            }
          },
          {
            label:    'Contact Email',
            name:     'contactEmail',
            index:    'contactEmail',
            width:    70,
            align:    'center',
            hidden:   true,
            search:   false,
            sortable: true,
            editable: true,
            editrules: {
              required:   true,
              edithidden: true,
              email:      true
            }
          },
          {
            label:    'Password',
            name:     'password',
            index:    'password',
            width:    140,
            align:    'center',
            search:   false,
            sortable: false,
            editable: true,
            edittype: 'password',
            hidden:   true,
            editoptions: {
              disabled: false
            },
            editrules: {
              required:   true,
              edithidden: true
            }
          },
          {
            label:    'Introduction',
            name:     'intro',
            index:    'intro',
            width:    150,
            align:    'center',
            search:   false,
            sortable: true,
            hidden:   true,
            editable: true,
            edittype: 'textarea',
            editoptions: {
              rows: '3'
            },
            editrules: {
              edithidden: true
            }
          },
          {
            label:    'Reg.',
            name:     'viewers',
            index:    'viewers',
            width:    60,
            align:    'center',
            search:   false,
            sortable: false,
            hidden:   false,
            editable: false
          }
        ],
        url:         '/admin/mso/list',
        datatype:    'json',
        caption:     'MSO List',
        sortname:    'updateDate',
        sortorder:   'desc',
        rowNum:      10,
        rowList:     [10, 20, 30, 40, 50, 100, 200],
        viewrecords: true,
        height:      'auto',
        hidegrid:    false,
        toppager:    true,
        gridComplete: function() {
          var totaltime = $(this).jqGrid('getGridParam', 'totaltime');
          $(this).jqGrid('setCaption', 'MSO List (' + totaltime + 'ms)');
        },
        ondblClickRow: function(rowId) {
          var logoUrl = $(this).jqGrid('getCell', rowId, 'logoUrl');
          $(this).jqGrid('setColProp', 'logoUrl', {editoptions: {src: logoUrl}});
          $(this).jqGrid('editGridRow', rowId,
          {
            url:               '/admin/mso/modify',
            caption:           'Edit MSO Meta',
            width:             'auto',
            top:               $(window).scrollTop() + 50, // $(this).offset().top - 50,
            left:              200, // $(this).offset().left + $(this).width() + 20,
            modal:             false,
            jqModal:           true,
            closeAfterEdit:    true,
            closeOnEscape:     true,
            reloadAfterSubmit: true,
            viewPagerButtons:  true,
            recreateForm:      true,
            beforeCheckValues: function(postData, formId, mode) {
              postData['logoUrl'] = $('#logoUrl', formId).attr('src');
              return postData;
            },
            beforeShowForm: function(formId) {
              $('#tr_password').hide();
              $('#type', formId).attr('disabled', true);
              $('#logoUrl', formId).click(function() {
                var logoUrl = prompt('Please enter new logo URL', $(this).attr('src'));
                if (logoUrl != null) {
                  $(this).attr('src', logoUrl);
                }
              });
            },
            afterclickPgButtons: function(whichButton, formId, rowId) {
              var logoUrl = $('#mso_table').jqGrid('getCell', rowId, 'logoUrl');
              $('#logoUrl', formId).attr('src', logoUrl);
            },
            afterComplete: utils.callbackAfterSubmitForm
          });
        }
      },
      init: function() {
        this.properties.subGrid = true;
        this.properties.subGridRowExpanded = this.gridMsoIpgList.init;
        $('#mso_table').jqGrid(this.properties);
        $('#mso_table').jqGrid('navGrid', '#mso_table_toppager',
        {
          edit:   false,
          add:    true,
          del:    false,
          search: true,
          view:   false,
          addtitle: 'Create A New MSO'
        },
        {
          // 'edit' properties
        },
        {
          // 'add' properties
          url:               '/admin/mso/create',
          addCaption:        'Create A New MSO',
          width:             'auto',
          modal:             false,
          jqModal:           true,
          closeAfterAdd:     true,
          closeOnEscape:     true,
          reloadAfterSubmit: true,
          recreateForm:      true,
          beforeSubmit: function(postData, formId) {
            var info = 'Please confirm following data:\n';
            info += '\nmso / default set name = ' + $('#name', formId).val();
            info += '\nemail / cms account = ' + $('#contactEmail', formId).val();
            info += '\ncms password = ' + $('#password', formId).val();
            if (confirm(info) == false) {
              return [false, 'Not Confirmed'];
            }
            return [true, ''];
          },
          beforeCheckValues: function(postData, formId, mode) {
            postData['logoUrl'] = $('#logoUrl', formId).attr('src');
            return postData;
          },
          beforeShowForm: function(formId) {
            $('#logoUrl', formId).attr('src', '/images/cms/upload_img.jpg');
            $('#logoUrl', formId).click(function() {
              var logoUrl = prompt('Please enter logo URL', $(this).attr('src'));
              if (logoUrl != null) {
                $(this).attr('src', logoUrl);
              }
            });
            $('#type option', formId).each(function() {
              if ($(this).val() != '3' && $(this).val() != '5') {
                $(this).detach();
              }
            });
            $('#type', formId).val(3);
            $('#tr_mso', formId).hide();
            $('#tr_updateDate', formId).hide();
            $('#tr_createDate', formId).hide();
            $('#tr_title', formId).hide();
            $('#tr_logoClickUrl', formId).hide();
            $('#tr_jingleUrl', formId).hide();
            $('#tr_preferredLangCode', formId).hide();
            $('#tr_intro', formId).hide();
          },
          afterComplete: utils.callbackAfterSubmitForm
        });
      }
    },
    init: function() {
      this.gridMsoList.init();
    }
  },
/////////////!!!!!!!!!!
  tabNewsManagement: {
    gridSetList: {
      properties: {	
        colModel: [
                   {
                       label:    'Set ID',
                       name:     'setId',
                       index:    'setId',
                       width:    80,
                       align:    'center',
                       sortable: false,
                       editable: false
                     },
                     {
                         label:    'Set Name',
                         name:     'name',
                         index:    'name',
                         width:    150,
                         align:    'center',
                         search:   false,
                         sortable: true,
                         editable: true,
                         editrules: {
                           required: true
                         },
                         editoptions: {
                           maxlength: 100
                         }
                     },
                     {
                         label:    'Set Description',
                         name:     'intro',
                         index:    'intro',
                         width:    280,
                         align:    'center',
                         search:   false,
                         sortable: true,
                         //hidden:   false,
                         editable: true,
                         edittype: 'textarea',
                         editoptions: {
                           rows: '3'
                         }
                     },
                     {
                       label:     'Recommended',
                       name:      'featured',
                       index:     'featured',
                       width:     100,
                       align:     'center',
                       sortable:  true,
                       formatter: 'checkbox',
                       editable:  true,
                       edittype:  'checkbox',
                       editoptions: {
                         value: 'true:false'
                       }
                     },
                     {
                         label:     'Public',
                         name:      'isPublic',
                         index:     'isPublic',
                         width:     60,
                         align:     'center',
                         sortable:  true,
                         formatter: 'checkbox',
                         editable:  true,
                         edittype:  'checkbox',
                         editoptions: {
                           value: 'true:false'
                         }
                     },
                     {
                         label:     'Language',
                         name:      'lang',
                         index:     'lang',
                         width:     70,
                         align:     'center',
                         search:    true,
                         stype:     'select',
                         sortable:  true,
                         formatter: 'select',
                         editable:  true,
                         edittype:  'select',
                         editoptions: {
                           value: 'en:en;zh:zh'
                         },
                         searchoptions: {
                             sopt:  ['eq'],
                             value: "en:en;zh:zh"
                           }              
                     },  
                     {	
                         label:     'Seq(rec)',
                         name:      'seq',
                         index:     'seq',
                         width:     60,
                         align:     'center',
                         sortable:  true,
                         editable:  true
                         /*
                         editrules: {
                           minValue: 0,
                           maxValue: 81,
                           required: false,
                           integer:  true
                         }
                         */
                     },                    
                     {	
                         label:     'Brand URL',
                         name:      'beautifulUrl',
                         index:     'beautifulUrl',
                         width:     80,
                         align:     'center',
                         sortable:  true,
                         editable:  true,
                         editrules: {
                           required: false
                         }
                     },                    
                     {
                   	  label:    'Updated Time',
                         name:     'updateDate',
                         index:    'updateDate',
                         width:    140,
                         align:    'center',
                         sortable: true,
                         editable: false,
                         hidden:   false,
                         editrules: {
                           edithidden: true
                         }
                     },          
                     {
                       label:    'Image',
                       name:     'imageUrl',
                       index:    'imageUrl',
                       width:    150,
                       align:    'center',
                       search:   false,
                       sortable: true,
                       //hidden:   false,
                       editable: true,
                       editoptions: {
                         rows: '3'
                       }
                     },
        ],
        url:         '/admin/set/list?notify=true',
        datatype:    'json',
        caption:     'Set List',
        sortname:    'updateDate',
        sortorder:   'desc',
        rowNum:      200,
        rowList:     [10, 20, 30, 40, 50, 100, 200],
        viewrecords: true,
        height:      'auto',
        hidegrid:    false,
        toppager:    true,
        gridComplete: function() {
          var totaltime = $(this).jqGrid('getGridParam', 'totaltime');
          $(this).jqGrid('setCaption', 'Set List (' + totaltime + 'ms)');
        },
        ondblClickRow: function(rowId) {
          var logoUrl = $(this).jqGrid('getCell', rowId, 'logoUrl');
          $(this).jqGrid('setColProp', 'logoUrl', {editoptions: {src: logoUrl}});
          $(this).jqGrid('editGridRow', rowId, {
            url:               '/admin/set/edit',
            caption:           'Edit MSO Meta',
            width:             'auto',
            top:               $(window).scrollTop() + 50, // $(this).offset().top - 50,
            left:              200, // $(this).offset().left + $(this).width() + 20,
            modal:             false,
            jqModal:           true,
            closeAfterEdit:    true,
            closeOnEscape:     true,
            reloadAfterSubmit: true,
            viewPagerButtons:  true,
            recreateForm:      true,
            beforeCheckValues: function(postData, formId, mode) {
              postData['logoUrl'] = $('#logoUrl', formId).attr('src');
              return postData;
            },
            beforeShowForm: function(formId) {
            },
            afterclickPgButtons: function(whichButton, formId, rowId) {
              var logoUrl = $('#mso_table').jqGrid('getCell', rowId, 'logoUrl');
              $('#logoUrl', formId).attr('src', logoUrl);
            },
            afterComplete: utils.callbackAfterSubmitForm
          });
        }
      },
      init: function() {
        this.properties.subGrid = true;
        $('#news_table').jqGrid(this.properties);
        $('#news_table').jqGrid('navGrid', '#news_table_toppager',
        {
            edit:   true,
            add:    false,
            del:    false,
            search: true,
            view:   false        	
        },
        {
        },
        {
            // 'delete' properties
            url:               '/admin/set/delete',
            caption:           'Remove A Set',
            msg:               'Do you want to remove it',
            width:             'auto',
            modal:             false,
            jqModal:           true,
            closeOnEscape:     true,
            reloadAfterSubmit: true,
            afterComplete: utils.callbackAfterSubmitForm
          });        
      }
    },
    init: function() {
      this.gridSetList.init();
    },
    gridChannelList: {
        properties: {	
          colModel: [
                     {
                         label:    'Channel ID',
                         name:     'channelId',
                         index:    'channelId',
                         width:    80,
                         align:    'center',
                         search:   true,
                         editable: true,
                         sortable: true,
                         editoptions: {
                           disabled: true
                         },
                         searchoptions: {
                           sopt: ['eq']
                         }
                       },
                       {
                         label:    'Channel Name',
                         name:     'name',
                         index:    'name',
                         width:    150,
                         align:    'center',
                         search:   true,
                         sortable: true,
                         editable: true,
                         editoptions: {
                           maxlength: 100
                         },
                         searchoptions: {
                           sopt: ['eq']
                         }
                       },
                       {
                         label:    'Source',
                         name:     'sourceUrl',
                         index:    'sourceUrl',
                         width:    380,
                         align:    'center',
                         search:   true,
                         sortable: true,
                         hidden:   false,
                         editable: true,
                         editoptions: {
                           disabled: false
                         },
                         editrules: {
                           edithidden: true
                         },
                         searchoptions: {
                           sopt: ['eq'],
                           searchhidden: true
                         }
                       },
                       {
                         label:     'Status',
                         name:      'status',
                         index:     'status',
                         width:     70,
                         align:     'center',
                         search:    true,
                         sortable:  true,
                         stype:     'select',
                         formatter: 'select',
                         editable:  true,
                         edittype:  'select',
                         editoptions: {
                           value: constants.channelStatus
                         },
                         searchoptions: {
                           sopt:  ['eq'],
                           value: constants.channelStatus
                         }
                       },
                       {
                         label:     'Type',
                         name:      'contentType',
                         index:     'contentType',
                         width:     90,
                         align:     'center',
                         search:    true,
                         stype:     'select',
                         sortable:  true,
                         formatter: 'select',
                         editable:  false,
                         edittype:  'select',
                         editoptions: {
                           disabled: true,
                           value:    constants.channelContentType
                         },
                         searchoptions: {
                           sopt:  ['eq'],
                           value: constants.channelContentType
                         }
                       },
                       {
                         label:     'Language',
                         name:      'langCode',
                         index:     'langCode',
                         width:     70,
                         hidden:    true,
                         align:     'center',
                         search:    true,
                         stype:     'select',
                         sortable:  true,
                         formatter: 'select',
                         editable:  true,
                         edittype:  'select',
                         editoptions: {
                           disabled: false,
                           value:    "en:en;zh:zh"
                         },
                         searchoptions: {
                           sopt:  ['eq'],
                           value: "en:en;zh:zh"
                         }
                       },
                       {
                         label:     'Public',
                         name:      'isPublic',
                         index:     'isPublic',
                         width:     70,
                         align:     'center',
                         search:    true,
                         stype:     'select',
                         sortable:  true,
                         formatter: 'checkbox',
                         editable:  true,
                         edittype:  'checkbox',
                         editoptions: {
                           value: 'true:false'
                         },
                         searchoptions: {
                           sopt:  ['eq'],
                           value: {
                             'true':  'true',
                             'false': 'false'
                           }
                         }
                       },
                       {
                         label:     'Piwik',
                         name:      'piwik',
                         index:     'piwik',
                         width:     70,
                         align:     'center',
                         sortable:  true,
                         editable:  false,
                         editoptions: {
                           disabled: true
                         }
                       },
                       {
                         label:    'Image',
                         name:     'imageUrl',
                         index:    'imageUrl',
                         width:    50,
                         align:    'center',
                         sortable: false,
                         hidden:   false,
                         search:   false,
                         editable: false,
                         editoptions: {
                           disabled: true
                         },
                         editrules: {
                           edithidden: true
                         }
                       },
                       {
                           label:    'Updated Time',
                           name:     'updateDate',
                           index:    'updateDate',
                           width:    140,
                           align:    'center',
                           sortable: true,
                           editable: false,
                           hidden:   false,
                           editrules: {
                             edithidden: true
                           }
                       },                    
                       {
                         label:    'Ep. Count',
                         name:     'programCnt',
                         index:    'programCnt',
                         width:    90,
                         align:    'center',
                         search:   false,
                         sortable: true,
                         hidden:   false,
                         editable: false,
                         editrules: {
                           integer:    true,
                           edithidden: true
                         }
                       },
                       {
                         label:    'Sub. Count',
                         name:     'subscriptionCnt',
                         index:    'subscriptionCnt',
                         width:    80,
                         align:    'center',
                         search:   false,
                         sortable: false,
                         hidden:   false,
                         editable: false,
                         editrules: {
                           integer:    true,
                           edithidden: true
                         }
                       },                     
          ],
          url:         '/admin/channel/list?notify=true',
          datatype:    'json',
          caption:     'Channel List',
          sortname:    'updateDate',
          sortorder:   'desc',
          rowNum:      10,
          rowList:     [10, 20, 30, 40, 50, 100, 200],
          viewrecords: true,
          height:      'auto',
          hidegrid:    false,
          toppager:    true,
          gridComplete: function() {
            var totaltime = $(this).jqGrid('getGridParam', 'totaltime');
            $(this).jqGrid('setCaption', 'Channel List (' + totaltime + 'ms)');
          },
          ondblClickRow: function(rowId) {
            //var logoUrl = $(this).jqGrid('getCell', rowId, 'logoUrl');
            //$(this).jqGrid('setColProp', 'logoUrl', {editoptions: {src: logoUrl}});
            $(this).jqGrid('editGridRow', rowId, {
              url:               '/admin/channel/modify',
              caption:           'Edit Channel Meta',
              width:             'auto',
              top:               $(window).scrollTop() + 50, // $(this).offset().top - 50,
              left:              200, // $(this).offset().left + $(this).width() + 20,
              modal:             false,
              jqModal:           true,
              closeAfterEdit:    true,
              closeOnEscape:     true,
              reloadAfterSubmit: true,
              viewPagerButtons:  true,
              recreateForm:      true,
              /*
              beforeCheckValues: function(postData, formId, mode) {
                postData['logoUrl'] = $('#logoUrl', formId).attr('src');
                return postData;
              },
              */
              beforeShowForm: function(formId) {
              },
              afterclickPgButtons: function(whichButton, formId, rowId) {
            	  /*
                var logoUrl = $('#mso_table').jqGrid('getCell', rowId, 'logoUrl');
                $('#logoUrl', formId).attr('src', logoUrl);
                */
              },
              afterComplete: utils.callbackAfterSubmitForm
            });
          }
        },
        init: function() {
          this.properties.subGrid = true;
          $('#news_ch_table').jqGrid(this.properties);
          $('#news_ch_table').jqGrid('navGrid', '#news_ch_table_toppager',
          {
              edit:   true,
              add:    false,
              del:    false,
              search: true,
              view:   false        	
          },
          {
          },
          {
              // 'delete' properties
              url:               '/admin/channel/delete',
              caption:           'Remove A Channel',
              msg:               'Do you want to remove it',
              width:             'auto',
              modal:             false,
              jqModal:           true,
              closeOnEscape:     true,
              reloadAfterSubmit: true,
              afterComplete: utils.callbackAfterSubmitForm
            });        
        }
      },
      init: function() {
        this.gridSetList.init();
        this.gridChannelList.init();
      }    
  },  
  
/////////////!!!!!!!!!!  
  tabUserManagement: {
    gridUserList: {
      gridSubscription: {
        properties: {
          colModel: [
            {
              label:    'Channel ID',
              name:     'channel',
              index:    'channelId',
              width:    100,
              align:    'center',
              sortable: true,
              editable: true,
              editoptions: {
                disabled: true
              },
              editrules: {
                required: true,
                number:   true
              }
            },
            {
              label:    'Channel Name',
              name:     'name',
              index:    'name',
              width:    180,
              align:    'center',
              sortable: false
            },
            {
              label:    'Updated Time',
              name:     'updateDate',
              index:    'updateDate',
              width:    140,
              align:    'center',
              sortable: true
            },
            {
              label:    'Created Time',
              name:     'createDate',
              index:    'createDate',
              width:    140,
              align:    'center',
              sortable: true,
              hidden:   true
            },
            {
              label:     'Seq.',
              name:      'seq',
              index:     'seq',
              width:     60,
              align:     'center',
              sortable:  true,
              editable:  true,
              editrules: {
                minValue: 1,
                maxValue: 81,
                required: true,
                integer:  true
              }
            },
            {
              label:     'Type',
              name:      'type',
              index:     'type',
              width:     70,
              align:     'center',
              sortable:  true,
              formatter: 'select',
              editable:  true,
              edittype:  'select',
              editoptions: {
                value: '1:General;2:Readonly;0:Unknown'
              }
            }
          ],
          url:         null,
          datatype:    'json',
          caption:     'Subscription',
          sortname:    'updateDate',
          sortorder:   'desc',
          rowNum:      10,
          rowList:     [10, 20, 30, 40, 50, 100, 200],
          viewrecords: true,
          height:      'auto',
          hidegrid:    false,
          toppager:    true,
          ondblClickRow: function(subRowId) {
            // empty, but don't remove it
          },
          gridComplete: function() {
            var totaltime = $(this).jqGrid('getGridParam', 'totaltime');
            $(this).jqGrid('setCaption', 'Subscription (' + totaltime + 'ms)');
          }
        },
        init: function(subgridId, rowId) {
          var subgridTableId = subgridId + '_t';
          $('#' + subgridId).html('<table id="' + subgridTableId + '"></table>');
          $('#' + subgridId).attr('style', 'padding: 5px 5px 5px 5px');
          var grid$ = page$.tabUserManagement.gridUserList.gridSubscription;
          grid$.properties.url = '/admin/nnuser/subscription?id=' + rowId;
          $('#' + subgridTableId).jqGrid(grid$.properties);
          $('#' + subgridTableId).jqGrid('navGrid', '#' + subgridTableId + '_toppager',
          {
            edit:   false,
            add:    false,
            del:    false,
            search: false,
            view:   false
          });
        }
      },
      properties: {
        colModel: [
          {
            label:    'Image',
            name:     'imageUrl',
            index:    'imageUrl',
            width:    150,
            align:    'center',
            sortable: true,
            hidden:   true,
            search:   false,
            editable: true,
            edittype: 'image',
            editoptions: {
              disabled: true
            },
            editrules: {
              edithidden: true
            }
          },
          {
            label:    'MSO ID',
            name:     'mso',
            index:    'msoId',
            width:    80,
            align:    'center',
            search:   true,
            stype:    'select',
            sortable: true,
            editable: true,
            editoptions: {
              disabled: true
            },
            searchoptions: {
              sopt:    ['eq'],
              dataUrl: '/admin/mso/msoHtmlSelectOptions'
            }
          },
          {
            label:    'User ID',
            name:     'id',
            index:    'id',
            width:    80,
            align:    'center',
            search:   false,
            sortable: false,
            editable: true,
            editoptions: {
              disabled: true
            }
          },
          {
            label:    'Name',
            name:     'name',
            index:    'name',
            width:    100,
            align:    'center',
            search:   false,
            sortable: true,
            editable: true,
            editoptions: {
              maxlength: 100
            }
          },
          {
            label:    'Email',
            name:     'email',
            index:    'email',
            width:    130,
            align:    'center',
            search:   false,
            sortable: true,
            editable: true,
            editoptions: {
              maxlength: 100
            }
          },
          {
            label:    'Updated Time',
            name:     'updateDate',
            index:    'updateDate',
            width:    140,
            align:    'center',
            search:   false,
            sortable: true,
            editable: true,
            editoptions: {
              disabled: true
            }
          },
          {
            label:    'Created Time',
            name:     'createDate',
            index:    'createDate',
            width:    140,
            align:    'center',
            search:   false,
            sortable: true,
            editable: true,
            hidden:   true,
            editoptions: {
              disabled: true
            },
            editrules: {
              edithidden: true
            }
          },
          {
            label:     'Type',
            name:      'type',
            index:     'type',
            width:     70,
            align:     'center',
            search:    true,
            stype:     'select',
            sortable:  true,
            formatter: 'select',
            editable:  true,
            edittype:  'select',
            editoptions: {
              disabled: true,
              value:    constants.userType
            },
            searchoptions: {
              sopt:  ['eq'],
              value: constants.userType
            }
          },
          {
            label:     'Age',
            name:      'age',
            index:     'age',
            width:     70,
            align:     'center',
            hidden:    true,
            search:    false,
            sortable:  true,
            editable:  true,
            editrules: {
              edithidden: true
            }
          },
          {
            label:    'Introduction',
            name:     'intro',
            index:    'intro',
            width:    150,
            align:    'center',
            search:   false,
            sortable: true,
            hidden:   true,
            editable: true,
            edittype: 'textarea',
            editoptions: {
              rows: '3'
            },
            editrules: {
              edithidden: true
            }
          }
        ],
        url:         '/admin/nnuser/list',
        datatype:    'json',
        caption:     'User List',
        sortname:    'updateDate',
        sortorder:   'desc',
        rowNum:      10,
        rowList:     [10, 20, 30, 40, 50, 100, 200],
        viewrecords: true,
        height:      'auto',
        hidegrid:    false,
        toppager:    true,
        gridComplete: function() {
          var totaltime = $(this).jqGrid('getGridParam', 'totaltime');
          $(this).jqGrid('setCaption', 'User List (' + totaltime + 'ms)');
        },
        ondblClickRow: function(rowId) {
          // empty, but don't remove it
        }
      },
      init: function() {
        var grid$ = page$.tabUserManagement.gridUserList;
        grid$.properties.subGrid = true;
        grid$.properties.subGridRowExpanded = grid$.gridSubscription.init;
        $('#user_table').jqGrid(grid$.properties);
        $('#user_table').jqGrid('navGrid', '#user_table_toppager',
        {
          edit:   false,
          add:    false,
          del:    false,
          search: true,
          view:   false
        }, {}, {}, {},
        {
          closeAfterSearch: true,
          closeAfterReset:  true,
          closeOnEscape:    true
        });
      }
    },
    init: function() {
      var tab$ = page$.tabUserManagement;
      tab$.gridUserList.init();
    }
  },
  init: function() {
    $('#ui_tabs').tabs();
    $.jgrid.no_legacy_api = true;
    page$.tabChannelManagement.init();
    page$.tabCategoryManagement.init();
    page$.tabMsoManagement.init();
    page$.tabSetManagement.init(); 
    page$.tabNewsManagement.init();
    page$.tabUserManagement.init();
    
    //$.jqGrid('gridDnD'), {connectWith:'page$.tabChannelManagement.gridChannelList'}
    
    //page$.tabChannelManagement.gridChannelList.properties

    //jQuery("#grid2").jqGrid('gridDnD',{connectWith:'#grid1'});
  }
};

$(function()
{
  page$.init();
});

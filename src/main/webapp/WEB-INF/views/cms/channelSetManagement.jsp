<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="global.jsp" %>
<html xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<link type="text/css" rel="stylesheet" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.15/themes/start/jquery-ui.css"/>
<link type="text/css" rel="stylesheet" href="${root}/stylesheets/cms.css"/>
<link type="text/css" rel="stylesheet" href="${root}/stylesheets/jquery.bubble.css"/>
<link type="text/css" rel="stylesheet" href="${root}/javascripts/plugins/dynatree/ui.dynatree.css"/>
<link type="text/css" rel="stylesheet" href="${root}/javascripts/plugins/msdropdown/dd.css"/>
<script type="text/javascript" src="${root}/javascripts/jquery-1.6.4.fixed.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.15/jquery-ui.min.js"></script>
<script type="text/javascript" src="${root}/javascripts/plugins/jquery.bubble.js"></script>
<script type="text/javascript" src="${root}/javascripts/plugins/dynatree/jquery.dynatree.js"></script>
<script type="text/javascript" src="${root}/javascripts/plugins/msdropdown/jquery.dd.js"></script>
<script type="text/javascript" src="${root}/javascripts/plugins/jquery.scrollTo-1.4.2.js"></script>
<script type="text/javascript" src="${root}/javascripts/swfupload/swfupload.js"></script>
<script type="text/javascript" src="${root}/javascripts/cms/common.js"></script>
<script type="text/javascript" src="${root}/javascripts/cms/channelSetManagement.js"></script>
<title><spring:message code="cms.channel_set_management.title"/></title>
</head>
<body>
<%@include file="header.jsp"%>
<div class="content">
  <div class="left_content floatL">
    <ul>
      <li><a class="menuA"></a></li>
      <li><a class="menuB_active"></a></li>
      <li><a class="menuC"></a></li>
      <li><a class="menuD"></a></li>
      <li><a class="menuE"></a></li>
    </ul>
    <label class="lang" id="image_menu">${root}/images/<spring:message code="cms.image.menu"/></label>
    <div class="clear"></div>
    <div class="left_body">
      <div class="left_title"><spring:message code="cms.channel_set_management.title.channel_set_info"/></div>
      <p class="hint_title"><span class="red">＊</span><spring:message code="cms.channel_set_management.msg.necessary_info"/></p>
      <form>
        <fieldset class="setAlbum">
          <input type="hidden" id="cc_id" value="0"/>
          <label class="floatL"><span class="red">＊</span><spring:message code="cms.channel_set_management.label.logo"/></label>
          <div class="uploadImg">
            <img alt="" id="cc_image" src="${root}/images/upload_img.jpg" class="floatL"/><input type="hidden" id="cc_image_updated" value="false"/>
            <div class="floatL imgBtn">
              <p class="gray"><spring:message code="cms.channel_set_management.msg.best_resolution"/></p>
              <a href="javascript:" id="upload_image" class="uploadBtn"></a>
              <span id="uploading" style="display:none"><spring:message code="cms.channel_set_management.msg.uploading"/></span>
              <input type="hidden" id="s3_policy" value="${s3Policy}"/>
              <input type="hidden" id="s3_signature" value="${s3Signature}"/>
              <input type="hidden" id="s3_id" value="${s3Id}"/>
            </div>
            <div class="clear"></div>
          </div>
          <label class="floatL"><spring:message code="cms.channel_set_management.label.url"/></label>
          <div class="floatL">
            <a class="floatL" href="javascript:" id="channel_set_promote_url" target="_player"></a>
            <a class="floatL icon addthis_button_expanded"></a>
          </div>
          <div class="clear"></div><br/>
          <label class="floatL"><span class="red">＊</span><spring:message code="cms.channel_set_management.label.name"/></label>
          <div class="bg_input floatL"><input type="text" id="cc_name" name="cc_name" size="32" maxlength="40"/></div>
          <div class="clear"></div>
          <p class="hint"><spring:message code="cms.channel_set_management.msg.fourty_characters"/></p>
          <br/>
          <label class="floatL"><spring:message code="cms.channel_set_management.label.introduction"/></label>
          <div class="bg_textarea floatL"><textarea name="cc_intro" id="cc_intro" cols="32" rows="5"></textarea></div>
          <div class="clear"></div>
          <p class="hint"><spring:message code="cms.channel_set_management.msg.two_hundred_characters"/></p>
          <br/>
          <label class="floatL"><spring:message code="cms.channel_set_management.label.tag"/></label>
          <div class="bg_input floatL"><input name="cc_tag" id="cc_tag" type="text" size="32" maxlength="40"/></div>
          <div class="clear"></div>
          <p class="hint"><spring:message code="cms.channel_set_management.msg.camma_seperated"/></p>
          <br/>
          <label class="floatL"><span class="red">＊</span><spring:message code="cms.channel_set_management.label.system_category"/></label>
          <div class="floatL">
            <select id="sys_directory" name="sys_directory" class="sys_directory" style="width:200px">
              <option value="0" selected="selected"><spring:message code="cms.channel_set_management.msg.select_category"/></option>
            </select>
          </div>
          <br/><br/>
          <label class="floatL"><span class="red">＊</span><spring:message code="cms.channel_management.label.language"/></label>
          <div class="floatL">
            <select id="cc_language" class="language">
              <option value="zh">中文</option>
              <option value="en">English</option>
            </select>
          </div>
          <div class="clear"></div>
          <p class="hint" style="display:none"><spring:message code="cms.channel_set_management.msg.system_category_explanation"/></p>
        </fieldset>
      </form>
    </div>
    <div class="left_footer"></div>
  </div>
  <div class="right_content floatL">
    <div class="right_body">
      <div class="right_title"><spring:message code="cms.channel_set_management.title.edit_channel_set"/><p id="publish_button" class="btnGray"><span><spring:message code="cms.channel_set_management.btn.pubish"/></span></p></div>
      <div class="set_ch_arrange">
        <div id="set_ch_holder">
          <ul id="set_ch_list" class="connectedSortable"></ul>
        </div>
      </div>
      <div class="ch_pool">
        <p class="ch_sub_title"><spring:message code="cms.channel_set_management.msg.drag_drop_explanation"/></p>
        <ul id="pool_tabs">
          <li id="category"><spring:message code="cms.channel_set_management.label.category"/></li>
          <li id="search"><spring:message code="cms.channel_set_management.label.search"/></li>
          <li id="youtube"><spring:message code="cms.channel_set_management.label.youtube"/></li>
        </ul>
        <div id="category_content" class="tab_content">
          <div id="treeview"></div>
        </div>
        <div id="search_content" class="tab_content">
          <div id="search_area">
            <p><spring:message code="cms.channel_set_management.label.search_keyword"/>:</p>
            <input type="text" id="search_input"/>
            <p id="search_button" class="btnGray"><span><spring:message code="cms.channel_set_management.btn.search"/></span></p>
          </div>
          <div id="search_result">
            <div id="no_search_result">Search Not Found</div>
            <ul id="result_list" class="connectedSortable">
              <li class="ch_normal"><p class="btnAdd"></p><p class="btnRemove"></p><p class="btnPlay"></p><img src="http://teltel.co.cc/louis/cms3.1/channelSetManagement_files/ch_logo_85005_11902.jpg">
              <p class="chTitle">街舞</p></li>
              <li class="ch_normal"><p class="btnAdd"></p><p class="btnRemove"></p><p class="btnPlay"></p><img src="http://teltel.co.cc/louis/cms3.1/channelSetManagement_files/hqdefault.jpg">
              <p class="chTitle">心理學與現代生活 Psychology and Modern Life</p></li>
              <li class="ch_normal"><p class="btnAdd"></p><p class="btnRemove"></p><p class="btnPlay"></p><img src="http://teltel.co.cc/louis/cms3.1/channelSetManagement_files/hqdefault(1).jpg">
              <p class="chTitle">心理學與現代生活 Psychology and Modern Life</p></li>
              <li class="ch_normal"><p class="btnAdd"></p><p class="btnRemove"></p><p class="btnPlay"></p><img src="http://teltel.co.cc/louis/cms3.1/channelSetManagement_files/hqdefault(2).jpg">
              <p class="chTitle">心理學與現代生活 Psychology and Modern Life</p></li>
              <li class="ch_normal"><p class="btnAdd"></p><p class="btnRemove"></p><p class="btnRemove"></p><p class="btnPlay"></p><img src="http://teltel.co.cc/louis/cms3.1/channelSetManagement_files/hqdefault(3).jpg">
              <p class="chTitle">心理學與現代生活 Psychology and Modern Life</p></li>
              <li class="ch_normal"><p class="btnAdd"></p><p class="btnRemove"></p><p class="btnPlay"></p><img src="http://teltel.co.cc/louis/cms3.1/channelSetManagement_files/hqdefault(4).jpg">
              <p class="chTitle">心理學與現代生活 Psychology and Modern Life</p></li>
              <li class="ch_normal"><p class="btnAdd"></p><p class="btnRemove"></p><p class="btnPlay"></p><img src="http://teltel.co.cc/louis/cms3.1/channelSetManagement_files/hqdefault(5).jpg">
              <p class="chTitle">心理學與現代生活 Psychology and Modern Life</p></li>
              <li class="ch_normal"><p class="btnAdd"></p><p class="btnRemove"></p><p class="btnPlay"></p><img src="http://teltel.co.cc/louis/cms3.1/channelSetManagement_files/default(1).jpg" />
              <p class="chTitle">心理學與現代生活 Psychology and Modern Life</p></li>
              <li class="ch_normal"><p class="btnAdd"></p><p class="btnRemove"></p><p class="btnPlay"></p><img src="http://teltel.co.cc/louis/cms3.1/channelSetManagement_files/default(2).jpg" />
              <p class="chTitle">心理學與現代生活 Psychology and Modern Life</p></li>
            </ul>
          </div>
        </div>
        <div id="youtube_content" class="tab_content">
          <p><spring:message code="cms.channel_set_management.label.youtube_channel_playlist_url"/>:</p>
          <input name="" type="text" id="youtube_input"/>
          <div id="youtube_button" class="btnGray floatL"><span><spring:message code="cms.channel_set_management.btn.add"/></span></div>
          <div class="floatL" style="padding-left:15px"><spring:message code="cms.channel_set_management.msg.url_example"/></div>
        </div>
      </div>
    </div>
    <div class="right_footer"></div>
  </div>
  <%@include file="footer.jsp"%>
  <div class="clear"></div>
</div>
<!-- language tags for javascript -->
<label class="lang" id="lang_label_program_count"><spring:message code="cms.channel_set_management.label.program_count"/></label>
<label class="lang" id="lang_label_update_time"><spring:message code="cms.channel_set_management.label.update_time"/></label>
<label class="lang" id="lang_warning_select_category"><spring:message code="cms.warning.select_system_category"/></label>
<label class="lang" id="lang_warning_empty_name"><spring:message code="cms.warning.empty_name"/></label>
<label class="lang" id="lang_warning_empty_logo"><spring:message code="cms.warning.empty_logo"/></label>
<label class="lang" id="lang_warning_tag_over_limitation"><spring:message code="cms.warning.tag_over_limitation"/></label>
<label class="lang" id="lang_warning_name_over_limitation"><spring:message code="cms.warning.name_over_limitation"/></label>
<label class="lang" id="lang_warning_intro_over_limitation"><spring:message code="cms.warning.intro_over_limitation"/></label>
<label class="lang" id="lang_warning_error_occurs"><spring:message code="cms.warning.error_occurs"/></label>
<label class="lang" id="lang_channel_source_is_wrong"><spring:message code="cms.warning.channel_source_is_wrong"/></label>
<label class="lang" id="lang_channel_source_is_empty"><spring:message code="cms.warning.channel_source_is_empty"/></label>
<label class="lang" id="lang_update_successfully"><spring:message code="cms.warning.update_successfully"/></label>
<label class="lang" id="lang_warning_channel_is_already_in"><spring:message code="cms.warning.channel_is_already_in"/></label>
<label class="lang" id="lang_warning_reached_maximum_amount"><spring:message code="cms.warning.reached_maximum_amount"/></label>
<label class="lang" id="lang_channel_had_been_added"><spring:message code="cms.info.channel_has_been_added"/></label>
<label class="lang" id="lang_warning_channel_has_not_been_saved"><spring:message code="cms.warning.change_has_not_been_saved"/></label>
<label class="lang" id="lang_warning_set_is_empty"><spring:message code="cms.warning.set_is_empty"/></label>
<label class="lang" id="lang_warning_new_channel_will_be_reviewed"><spring:message code="cms.warning.new_channel_will_be_reviewed"/></label>
<label class="lang" id="lang_warning_new_channels_will_be_reviewed"><spring:message code="cms.warning.new_channels_will_be_reviewed"/></label>
<label class="lang" id="image_bg_album">${root}/images/<spring:message code="cms.image.bg_album"/></label>
<label class="lang" id="image_btn_upload">${root}/images/<spring:message code="cms.image.btn_upload"/></label>
</body>
</html>

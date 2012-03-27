<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="global.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.15/themes/start/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="${root}/stylesheets/cms.css" />
<script type="text/javascript" src="${root}/javascripts/jquery-1.6.4.fixed.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.15/jquery-ui.min.js"></script>
<script type="text/javascript" src="${root}/javascripts/cms/common.js"></script>
<script type="text/javascript" src="${root}/javascripts/cms/promotionTools.js"></script>
<title><spring:message code="cms.promotion_tools.title"/></title>
</head>
<body>
<%@include file="header.jsp"%>
<div class="content">
  <div class="left_content floatL">
    <ul class="menu">
      <li><a class="menuA"></a></li>
      <li><a class="menuB"></a></li>
      <li><a class="menuC"></a></li>
      <li><a class="menuD_active"></a></li>
      <li><a class="menuE"></a></li>
    </ul>
    <label class="lang" id="image_menu">${root}/images/<spring:message code="cms.image.menu"/></label>
    <div class="clear"></div>
    <div class="left_body">
      <div class="left_title"><spring:message code="cms.promotion_tools.title.channel_list"/></div>
      <div class="createChList2">
        <ul class="chList" id="channel_list_ul">
          <li style="display:none">
            <div class="chUnFocus channel_info_block" id="channel_info_block">
              <div class="chUnFocusTitle channel_info_title"><div>Tile Tile Tile</div></div>
              <div class="chUnFocusImg channel_info_image"></div>
              <div class="floatL chInfo">
                <a href="#" target="_player" class="floatL channel_info_promoteurl">http://www.9x9.tv/channel/3958</a>
                <a class="floatL icon addthis_button_expanded"></a>
                <a href="javascript:" class="iconStatistics" title=""></a>
                <div class="clear"></div>
                <div class="floatL">
                  <p class="channel_info_contenttype"><spring:message code="cms.promotion_tools.label.channel_type"/> : <span>9x9</span></p>
                  <p class="channel_info_programcount"><spring:message code="cms.promotion_tools.label.program_count"/> : <span>0</span></p>
                  <p class="channel_info_subscribers"><spring:message code="cms.promotion_tools.label.subscribers"/> : <span>0</span></p>
                  <p class="channel_info_updatedate"><spring:message code="cms.promotion_tools.label.update_time"/> : <span>2011/04/15 14:45</span></p>
                </div>
                <ul class="floatL" style="display:none">
                  <li><a class="chUnPublic channel_info_publish"></a></li>
                </ul>
              </div>
            </div>
          </li>
        </ul>
        <div style="display:none" class="left_title"><spring:message code="cms.promotion_tools.title.channel_set_list"/></div>
        <ul class="chList" id="channel_set_list_ul">
        </ul>
      </div>
    </div>
    <div class="left_footer"></div>
  </div>
  <div class="right_content floatL">
    <div class="right_body">
      <div id="no_promotion" style="display:none">
        <div class="right_title"><div>Title Title Title</div></div>
        <div class="promote_title"><spring:message code="cms.promotion_tools.msg.no_autosharing_provided"/></div>
        <label>
          <input type="checkbox" class="sns_checkbox" disabled="disabled"/>&nbsp; <span style="color:grey" class="facebook_label"><spring:message code="cms.promotion_tools.label.facebook"/></span>
        </label>
      </div>
      <div id="promotion_content" style="display:none">
        <div class="right_title"><div>Title Title Title</div></div>
        <div class="promote_title">
          <div class="floatL"><spring:message code="cms.promotion_tools.title.autosharing"/>&nbsp;&nbsp;</div>
          <a href="javascript:" class="promoteInfo"></a>
          <div class="clear"></div>
          <div class="promote_hint" id="pro_hint">
            <ul>
              <li><spring:message code="cms.promotion_tools.msg.autosharing_hint_1"/></li>
              <li><spring:message code="cms.promotion_tools.msg.autosharing_hint_2"/></li>
            </ul>
          </div>
        </div>
        <br/>
        <label class="pro_check">
          <input type="checkbox" class="sns_checkbox" name="sns_facebook" disabled="disabled"/>&nbsp; <span style="color:grey" class="facebook_label"><spring:message code="cms.promotion_tools.label.facebook"/></span>
        </label>
        <select class="facebook_select" disabled="disabled">
          <option value="0"><spring:message code="cms.promotion_tools.label.my_profile_page"/></option>
        </select>
        <br/>
        <label class="pro_check">
          <input type="checkbox" class="sns_checkbox" name="sns_twitter" disabled="disabled"/>&nbsp; <span style="color:grey" class="twitter_label"><spring:message code="cms.promotion_tools.label.twitter"/></span>
        </label>
        <label class="pro_check" style="display:none"><input type="checkbox" class="sns_checkbox" name="sns_plurk" disabled="disabled"/>&nbsp; <spring:message code="cms.promotion_tools.label.plurk"/></label>
        <label class="pro_check" style="display:none"><input type="checkbox" class="sns_checkbox" name="sns_sina" disabled="disabled"/>&nbsp; <spring:message code="cms.promotion_tools.label.sina"/></label>
        <br/><br/><br/>
        <div class="promote_title" style="display:none"><spring:message code="cms.promotion_tools.label.9x9_rss_feed"/>&nbsp;&nbsp; <a id="9x9_rss_tutorial" href="javascript:"><spring:message code="cms.promotion_tools.msg.tutorial"/></a></div>
        <br/><br/>
        <div style="display:none"><input type="text" size="40" disabled="disabled" value="9x9 RSS needs be provided"/>&nbsp;&nbsp;<a href="javascript:"><spring:message code="cms.promotion_tools.button.click_to_copy"/></a></div>
      </div>
    </div>
    <div class="right_footer"></div>
  </div>
  <div class="clear"></div>
  <%@include file="footer.jsp"%>
</div>
<label class="lang" id="lang_view_statistics"><spring:message code="cms.promotion_tools.msg.view_statistics"/></label>
<label class="lang" id="lang_label_channel_set"><spring:message code="cms.promotion_tools.label.channel_set"/></label>
<label class="lang" id="lang_confirm_goto_setting_page"><spring:message code="cms.warning.goto_setting_page"/></label>
<label class="lang" id="lang_update_successfully"><spring:message code="cms.warning.update_successfully"/></label>
<label class="lang" id="lang_warning_error_occurs"><spring:message code="cms.warning.error_occurs"/></label>
<label class="lang" id="image_ch_public">${root}/images/<spring:message code="cms.image.ch_public"/></label>
<label class="lang" id="image_ch_unpublic">${root}/images/<spring:message code="cms.image.ch_unpublic"/></label>
</body>
</html>

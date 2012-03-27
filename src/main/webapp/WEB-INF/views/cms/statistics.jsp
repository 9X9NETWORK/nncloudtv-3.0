<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.15/themes/start/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="${root}/stylesheets/cms.css" />
<script type="text/javascript" src="${root}/javascripts/jquery-1.6.4.fixed.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.15/jquery-ui.min.js"></script>
<script type="text/javascript" src="${root}/javascripts/cms/common.js"></script>
<script type="text/javascript" src="${root}/javascripts/cms/statistics.js"></script>
<title><spring:message code="cms.statistics.title"/></title>
</head>
<body>
<%@include file="header.jsp"%>
<div class="content">
  <div class="left_content floatL">
    <ul class="menu">
      <li><a class="menuA"></a></li>
      <li><a class="menuB"></a></li>
      <li><a class="menuC"></a></li>
      <li><a class="menuD"></a></li>
      <li><a class="menuE_active"></a></li>
    </ul>
    <label class="lang" id="image_menu">${root}/images/cms/<spring:message code="cms.image.menu"/></label>
    <div class="clear"></div>
    <div class="left_body">
      <div class="createChList2" style="height: 548px">
        <div class="left_title" id="channel_set_title"><spring:message code="cms.statistics.title.channel_set_list"/></div>
        <ul class="chList" id="channel_set_list_ul"></ul>
        <div class="clear"></div>
        <div class="left_title"><spring:message code="cms.statistics.title.channel_list"/></div>
        <ul class="chList" id="channel_list_ul">
          <li style="display:none">
            <div class="chUnFocus channel_info_block" id="channel_info_block">
              <div class="chUnFocusTitle channel_info_title"><div>Title Title Title</div></div>
              <div class="chUnFocusImg channel_info_image"></div>
              <div class="floatL chInfo">
                <a href="#" target="_player" class="floatL channel_info_promoteurl">http://www.9x9.tv/channel/3958</a>
                <a class="floatL icon addthis_button_expanded"></a>
                <a href="javascript:" class="iconStatistics" title=""></a>
                <div class="clear"></div>
                <div class="floatL">
                  <p class="channel_info_contenttype"><spring:message code="cms.channel_management.label.channel_type"/> : <span>9x9</span></p>
                  <p style="display:none" class="channel_info_programcount"><spring:message code="cms.channel_management.label.program_count"/> : <span>0</span></p>
                  <p class="channel_info_subscribers"><spring:message code="cms.channel_management.label.subscribers"/> : <span>0</span></p>
                  <p class="channel_info_updatedate"><spring:message code="cms.channel_management.label.update_time"/> : <span>2011/04/15 14:45</span></p>
                  <select class="channel_info_statistics">
                    <option value="overview_with_graph"><spring:message code="cms.statistics.label.overview"/></option>
                    <option value="page_titles"><spring:message code="cms.statistics.label.channel_performance"/></option>
                    <option value="pages_per_visit"><spring:message code="cms.statistics.label.view_per_visit"/></option>
                    <option value="length_of_visits"><spring:message code="cms.statistics.label.visit_duration"/></option>
                  </select>
                </div>
                <ul style="display:none" class="floatL">
                  <li><a class="chUnPublic channel_info_publish"></a></li>
                </ul>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
    <div class="left_footer"></div>
  </div>
  <div class="right_content floatL">
    <div class="right_body">
      <div class="set_stastics" style="display:none" id="channel_set_statistics"><!--set stastics-->
        <div class="right_title"><div>Title Title Title</div></div>
        <div class="stastics_title">
          <div><spring:message code="cms.statistics.msg.effective_watch"/></div>
          <div class="datePick"><spring:message code="cms.statistics.label.please_select_time_range"/>&nbsp;<input id="set_pickdate" type="text"/></div>
          <div class="clear"></div>
        </div>
        <div class="stastics_chart">
          <select name="">
            <option><spring:message code="cms.statistics.label.accumulated_watch_times"/></option>
            <option><spring:message code="cms.statistics.label.average_watch_times_each"/></option>
            <option><spring:message code="cms.statistics.label.subscription_count"/></option>
            <option><spring:message code="cms.statistics.label.average_elapsed_time"/></option>
            <option><spring:message code="cms.statistics.label.returning_rate"/></option>
          </select>
          <img alt="" src="${root}/images/cms/img_stastics.png"/>
        </div>
        <table border="0" cellpadding="0" cellspacing="0" class="stastics_list">
          <tr>
            <td>1,057,201</td>
            <td><a href="javascript:"><spring:message code="cms.statistics.label.accumulated_watch_times"/></a></td>
            <td>00:03:21</td>
            <td><a href="javascript:"><spring:message code="cms.statistics.label.average_elapsed_time"/></a></td>
          </tr>
          <tr>
            <td>3</td>
            <td><a href="javascript:"><spring:message code="cms.statistics.label.average_watch_times_each"/></a></td>
            <td>20%</td>
            <td><a href="javascript:"><spring:message code="cms.statistics.label.returning_rate"/></a></td>
          </tr>
          <tr>
            <td id="set_subscription_count">13,596</td>
            <td><a href="javascript:"><spring:message code="cms.statistics.label.subscription_count"/></a></td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
          </tr>
        </table>
      </div>
      <div class="ch_stastics" id="channel_statistics" style="display:none"><!--channel stastics-->
        <div class="right_title" style="display:none"><div>Title Title Title</div></div>
        <div class="stasticTab" style="display:none">
          <ul>
            <li id="stasticTabA" class="tab_focus"><a href="javascript:"><spring:message code="cms.statistics.label.channel_statistics"/></a></li>
            <li style="display:none" id="stasticTabB" class="tab_unfocus"><a href="javascript:"><spring:message code="cms.statistics.label.program_statistics"/></a></li>
            <div class="clear"></div>
          </ul>
        </div>
        <div class="ch_stastics" id="ch_stastics">
          <div class="stastics_title" style="display:none">
            <div style="display:none"><spring:message code="cms.statistics.msg.effective_watch"/></div>
            <div class="datePick"><spring:message code="cms.statistics.label.please_select_time_range"/>&nbsp;<input type="text"/></div>
            <div class="clear"></div>
          </div>
          <iframe class="stastics_iframe" scrolling="auto" frameborder="0" marginheight="0" marginwidth="0" style="display:none"></iframe>
          <div class="stastics_empty" style="display:none"><spring:message code="cms.statistics.label.no_report"/></div>
          <div class="stastics_chart" style="display:none">
            <select name="">
              <option><spring:message code="cms.statistics.label.accumulated_watch_times"/></option>
              <option><spring:message code="cms.statistics.label.average_watch_times_each"/></option>
              <option><spring:message code="cms.statistics.label.subscription_count"/></option>
              <option><spring:message code="cms.statistics.label.average_elapsed_time"/></option>
              <option><spring:message code="cms.statistics.label.returning_rate"/></option>
            </select>
            <img alt="" src="${root}/images/cms/img_stastics.png"/>
          </div>
          <table border="0" cellpadding="0" cellspacing="0" class="stastics_list" style="display:none">
            <tr>
              <td>1,057,201</td>
              <td><a href="javascript:"><spring:message code="cms.statistics.label.accumulated_watch_times"/></a></td>
              <td>00:03:21</td>
              <td><a href="javascript:"><spring:message code="cms.statistics.label.average_elapsed_time"/></a></td>
            </tr>
            <tr>
              <td>3</td>
              <td><a href="javascript:"><spring:message code="cms.statistics.label.average_watch_times_each"/></a></td>
              <td>20%</td>
              <td><a href="javascript:"><spring:message code="cms.statistics.label.returning_rate"/></a></td>
            </tr>
            <tr>
              <td id="ch_subscription_count">13,596</td>
              <td><a href="javascript:"><spring:message code="cms.statistics.label.subscription_count"/></a></td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
            </tr>
          </table>
        </div>
        <div class="ep_stastics" id="ep_stastics" style="display:none">
          <div class="stastics_title">
            <div><spring:message code="cms.statistics.msg.effective_watch"/></div>
            <div class="datePick"><spring:message code="cms.statistics.label.please_select_time_range"/>&nbsp;<input type="text"/></div>
            <div class="clear"></div>
          </div>
          <div class="stastics_chart">
            <select id="ep_selector">
              <option><spring:message code="cms.statistics.lable.please_select_program"/></option>
            </select>&nbsp;&nbsp;&nbsp;
            <select name="">
              <option><spring:message code="cms.statistics.label.accumulated_watch_times"/></option>
              <option><spring:message code="cms.statistics.label.shared_times"/></option>
              <option><spring:message code="cms.statistics.label.average_watch_time_each"/></option>
            </select>
            <img alt="" src="${root}/images/cms/img_stastics.png"/>
          </div>
          <table border="0" cellpadding="0" cellspacing="0" class="stastics_list">
            <tr>
              <td>1,057,201</td>
              <td><a href="javascript:"><spring:message code="cms.statistics.label.accumulated_watch_times"/></a></td>
              <td id="ep_share_count">3</td>
              <td><a href="javascript:"><spring:message code="cms.statistics.label.shared_times"/></a></td>
            </tr>
            <tr>
              <td>00:03:21</td>
              <td><a href="javascript:"><spring:message code="cms.statistics.label.average_watch_time_each"/></a></td>
              <td></td>
              <td></td>
            </tr>
          </table>
        </div>
      </div>
    </div>
    <div class="right_footer"></div>
  </div>
  <%@include file="footer.jsp"%>
  <div class="clear"></div>
</div>
<label class="lang" id="lang_view_statistics"><spring:message code="cms.statistics.msg.view_statistics"/></label>
<label class="lang" id="lang_label_channel_set"><spring:message code="cms.statistics.label.channel_set"/></label>
<label class="lang" id="lang_title_set_statistics"><spring:message code="cms.statistics.title.channel_set_statistics"/></label>
<label class="lang" id="lang_label_please_select_program"><spring:message code="cms.statistics.lable.please_select_program"/></label>
<label class="lang" id="image_ch_public">${root}/images/cms/<spring:message code="cms.image.ch_public"/></label>
<label class="lang" id="image_ch_unpublic">${root}/images/cms/<spring:message code="cms.image.ch_unpublic"/></label>
<%@ include file="global.jsp" %>
</body>
</html>

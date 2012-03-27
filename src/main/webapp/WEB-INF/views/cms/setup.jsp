<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="root" value="http://9x9ui.s3.amazonaws.com/cms"/>
<div class="setup_context">
  <a href="javascript:" class="btnClose jqmClose"></a>
  <div class="setCon">
    <div class="setTitle">
      <ul class="setupTab">
        <li id="tabA" class="setupTabItem link_Act"><a href="javascript:"><spring:message code="cms.setup.label.account_sync"/></a></li>
        <li id="tabB" class="setupTabItem link_Normal" style="display:none"><a href="javascript:"><spring:message code="cms.setup.label.admin_group"/></a></li>
        <li id="tabC" class="setupTabItem link_Normal"><a href="javascript:"><spring:message code="cms.setup.label.change_password"/></a></li>
        <div class="clear"></div>
      </ul>
    </div>
    <div id="syncAcc" class="syncAcc setupContent">
      <spring:message code="cms.setup.msg.account_synchronization_explanation"/>
      <ul>
        <li class="syncFb">
          <span><spring:message code="cms.setup.label.facebook"/></span>&nbsp;&nbsp;
          <span><a style="display:block;width:120px" href="javascript:" id="facebook_connect"><spring:message code="cms.setup.button.account_connect"/></a></span>
          <span id="fb_field" style="display:none;width:300px">
            <a style="display:none" href="javascript:" id="facebook_disconnect"><spring:message code="cms.setup.button.account_disconnect"/></a>
            &nbsp;&nbsp;|&nbsp;&nbsp;<a href="javascript:" id="fb_switch"></a>
          </span>
          <a href="javascript:" class="syncInfo"></a>
          <div class="sync_hint" id="fb_hint">
            <spring:message code="cms.setup.msg.fb_sync_hint"/>
          </div>
        </li>
        <li class="syncTwitter">
          <span><spring:message code="cms.setup.label.twitter"/></span>&nbsp;&nbsp;
          <span><a style="display:none" href="javascript:" id="twitter_connect"><spring:message code="cms.setup.button.account_connect"/></a></span>&nbsp;&nbsp;
          <span id="twitter_field" style="display:none;width:300px"><a style="display:none" href="javascript:" id="twitter_disconnect"><spring:message code="cms.setup.button.account_disconnect"/></a>
          &nbsp;&nbsp;|&nbsp;&nbsp;
          <a style="display:none" href="javascript:" id="tw_switch"><spring:message code="cms.setup.button.disable_autosharing"/></a></span>
        </li>
        <li class="syncPlurk">
          <span><spring:message code="cms.setup.label.plurk"/></span>&nbsp;&nbsp;
          <a href="javascript:"><spring:message code="cms.setup.button.account_connect"/></a>
          <a href="#" style="display:none"><spring:message code="cms.setup.button.account_disconnect"/></a>&nbsp; | &nbsp;<a href="#"><spring:message code="cms.setup.button.disable_autosharing"/> </a>
        </li>
        <li class="syncSina">
          <span><spring:message code="cms.setup.label.sina"/></span>&nbsp;&nbsp;
          <a href="javascript:"><spring:message code="cms.setup.button.account_connect"/></a>
          <a href="#" style="display:none"><spring:message code="cms.setup.button.account_disconnect"/></a>&nbsp; | &nbsp;<a href="#"><spring:message code="cms.setup.button.disable_autosharing"/> </a>
        </li>
      </ul>
    </div>
    <div id="addAcc" class="addAcc setupContent" style="display:none;">
      <table border="0" cellpadding="0" cellspacing="0" class="addForm">
        <tr>
          <th align="left"><spring:message code="cms.setup.title.add_admin_account"/></th>
          <th align="left"><spring:message code="cms.setup.title.comment"/></th>
          <th></th>
        </tr>
        <tr>
          <td><input type="text"/></td>
          <td><input type="text"/></td>
          <td><button><big><spring:message code="cms.setup.button.add_account"/></big></button></td>
        </tr>
        <tr>
          <td colspan="3"><spring:message code="cms.setup.msg.add_account_explanation"/></td>
        </tr>
      </table>
      <table border="0" cellpadding="0" cellspacing="0" class="mainAcc">
        <tr>
          <th align="left" width="200"><spring:message code="cms.setup.title.admin_account"/></th>
          <th align="left"><spring:message code="cms.setup.title.comment"/></th>
        </tr>
        <tr>
          <td><span>daai@9x9.tv</span></td>
          <td><span><spring:message code="cms.setup.msg.master_admin"/></span></td>
        </tr>
      </table>
      <div class="subAcc">
        <table border="0" cellpadding="0" cellspacing="0">
          <tr>
            <th align="left" width="200"><spring:message code="cms.setup.title.sub_admin"/></th>
            <th align="left"><spring:message code="cms.setup.title.comment"/></th>
          </tr>
          <tr>
            <td><span>daai1@9x9.tv</span></td>
            <td><span>commet</span></td>
          </tr>
          <tr>
            <td><span>daai2@9x9.tv</span></td>
            <td><span>commet</span></td>
          </tr>
          <tr>
            <td><span>daai3@9x9.tv</span></td>
            <td><span>commet</span></td>
          </tr>
          <tr>
            <td><span>daai4@9x9.tv</span></td>
            <td><span>commet</span></td>
          </tr>
          <tr>
            <td><span>daai5@9x9.tv</span></td>
            <td><span>commet</span></td>
          </tr>
          <tr>
            <td><span>daai6@9x9.tv</span></td>
            <td><span>commet</span></td>
          </tr>
        </table>
      </div>
    </div>
    <div id="setPs" class="setPs setupContent" style="display:none;">
      <table border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td><spring:message code="cms.setup.label.reset_password"/></td>
          <td><input id="password" type="password" length="20" type="text"/></td>
        </tr>
        <tr>
          <td><spring:message code="cms.setup.label.retype_password"/></td>
          <td><input id="retype" type="password" length="20" type="text"/></td>
        </tr>
        <tr>
          <td></td>
          <td><button id="save_password_button"><big><spring:message code="cms.setup.button.save"/></big></button>&nbsp;&nbsp;<a href="javascript:" id="cancel_password_button"><spring:message code="cms.setup.button.cancel"/></a></td>
        </tr>
      </table>
    </div>
  </div>
</div>
<label class="lang" id="lang_button_disable_autosharing"><spring:message code="cms.setup.button.disable_autosharing"/></label>
<label class="lang" id="lang_button_enable_autosharing"><spring:message code="cms.setup.button.enable_autosharing"/></label>
<label class="lang" id="lang_warning_not_9x9_user"><spring:message code="cms.warning.not_9x9_user"/></label>
<label class="lang" id="lang_warning_remove_admin_member"><spring:message code="cms.warning.remove_admin_member"/></label>
<label class="lang" id="image_bg_setup">${root}/images/<spring:message code="cms.image.bg_setup"/></label>
<label class="lang" id="lang_warning_error_occurs"><spring:message code="cms.warning.error_occurs"/></label>
<label class="lang" id="lang_confirm_disconnect_with_facebook"><spring:message code="cms.setup.confirm_disconnect_with_facebook"/></label>
<label class="lang" id="lang_confirm_disconnect_with_twitter"><spring:message code="cms.setup.confirm_disconnect_with_twitter"/></label>
<label class="lang" id="lang_warning_retype_not_match"><spring:message code="cms.warning.retype_not_match"/></label>
<label class="lang" id="lang_update_successfully"><spring:message code="cms.warning.update_successfully"/></label>


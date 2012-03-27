<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><spring:message code="cms.login.title"/></title>
<link href="${root}/stylesheets/cms.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${root}/javascripts/jquery-1.6.4.fixed.js"></script>
<script type="text/javascript">
$(function() {
  var style = '<style>'
            + 'a.btnLogin { background-image: url(' + $('#image_btn_login').text() + '); }'
            + 'a.btnLogin:hover { background-image: url(' + $('#image_btn_login_hover').text() + '); }'
            + '</style>';
  $(style).appendTo('head');
  $('#login_form').keypress(function(event) {
    if (event.which == 13) {
      $('#login_form').submit();
    }
  });
});
</script>
</head>
<body>
  <div class="header"></div>
  <div class="content loginContent">
    <div class="loginPanel">
      <img alt="" src="${msoLogo}" /><br/><br/>
      <form id="login_form" method="POST">
        <fieldset class="setAlbum">
          <label class="floatL"><spring:message code="cms.login.label.9x9_account"/> : </label>
          <input type="text" name="email" size="30" value="${email}"/><br/><br/>
          <label class="floatL"><spring:message code="cms.login.label.password"/> : </label>
          <input type="password" name="password" size="30" value="${password}"/><br/><br/>
          <label class="floatL">&nbsp;</label>
          <input type="checkbox" name="rememberMe" checked="checked"/><label>&nbsp;&nbsp;<spring:message code="cms.login.label.remember_me"/></label><br/><br/>
          <label class="floatL">&nbsp;</label>
          <a href="javascript:$('#login_form').submit();" class="btnLogin" id="submit_button"></a>
          <br/><br/>
          <label class="floatL">&nbsp;</label>
          <span class="error">${error}</span>
        </fieldset>
      </form>
    </div>
  </div>
<label class="lang" id="image_btn_login">${root}/images/cms/<spring:message code="cms.image.btn_login"/></label>
<label class="lang" id="image_btn_login_hover">${root}/images/cms/<spring:message code="cms.image.btn_login_hover"/></label>
<%@ include file="global.jsp" %>
</body>
</html>

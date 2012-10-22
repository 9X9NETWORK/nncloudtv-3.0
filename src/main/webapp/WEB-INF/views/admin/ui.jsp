<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.15/themes/ui-darkness/jquery-ui.css" type="text/css" />
<link rel="stylesheet" href="${root}/stylesheets/ui.jqgrid.css" />
<link rel="stylesheet" href="${root}/stylesheets/admin-portal-ui.css" />
<script type="text/javascript" src="${root}/javascripts/jquery-1.6.4.fixed.js"></script>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.15/jquery-ui.js"></script>
<script type="text/javascript" src="${root}/javascripts/grid.locale-en.js"></script>
<script type="text/javascript" src="${root}/javascripts/jquery.jqGrid.src.js"></script>
<script type="text/javascript" src="${root}/javascripts/piwik-analytics.js"></script>
<script type="text/javascript" src="${root}/javascripts/admin-portal-ui.js"></script>
<title>9x9 Admin Portal</title>
</head>
<body>
<div id="header">
  <img src="${root}/images/9x9.tv.png" id="logo">
  <a href="<c:url value="/j_spring_security_logout" />" id="btn-signout">Sign Out</a>
  
  <span id="btn-account">${admin}</span>
</div>
<div id="ui_tabs">
  <ul>
    <li><a href="">Content List</a></li>
    <!--    
    <li><a href="#tabs_category_mngt">Category Management</a></li> 
    <li><a href="#tabs_category_mngt">Category Management</a></li>
    <li><a href="#tabs_set_mngt">Set Management</a></li>
    <li><a href="#tabs_channel_mngt">Channel Management</a></li>
    <li><a href="#tabs_news_mngt">Notification</a></li>
    <li><a href="#tabs_mso_mngt">MSO Management</a></li>
    <li><a href="#tabs_user_mngt">User Management</a></li>
     -->
  </ul>
  <!-- 
  <div id="tabs_category_mngt">
    <p>
      <table id="cat_table"></table>
    </p>
  </div>
  <div id="tabs_set_mngt">
    <p>
      <table id="set_table"></table>
    </p>
  </div>
  <div id="tabs_mso_mngt">
    <p>
      <table id="mso_table"></table>
    </p>
  </div>
  <div id="tabs_channel_mngt">
    <p>
      <table id="chn_table"></table>
      <br/>
      <table id="cc_table" channel="0" title=""></table>
    </p>
  </div>
  <div id="tabs_news_mngt">
    <p>
      <table id="news_table"></table>
      <br/>
      <table id="news_ch_table" channel="0" title=""></table>
    </p>
  </div>  
  <div id="tabs_user_mngt">
    <p>
      <table id="user_table"></table>
    </p>
  </div>
   -->
</div><!-- id="ui_tabs" -->

<div id="footer">
  <p id="copyright">&nbsp; &copy; 2010 9x9CloudTV. All rights reserved.</p>
</div>
</body>
</html>

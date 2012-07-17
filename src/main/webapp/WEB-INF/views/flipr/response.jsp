<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.15/themes/ui-darkness/jquery-ui.css" type="text/css" />
<link rel="stylesheet" href="http://9x9ui.s3.amazonaws.com/war/v0/stylesheets/ui.jqgrid.css" />
<link rel="stylesheet" href="http://9x9ui.s3.amazonaws.com/war/v0/stylesheets/admin-portal-ui.css" />
<script type="text/javascript" src="http://9x9ui.s3.amazonaws.com/war/v0/javascripts/jquery-1.6.4.fixed.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.15/jquery-ui.js"></script>
<script type="text/javascript" src="http://9x9ui.s3.amazonaws.com/war/v0/javascripts/grid.locale-en.js"></script>
<script type="text/javascript" src="http://9x9ui.s3.amazonaws.com/war/v0/javascripts/jquery.jqGrid.src.js"></script>
<script type="text/javascript" src="http://9x9ui.s3.amazonaws.com/war/v0/javascripts/piwik-analytics.js"></script>
<script type="text/javascript" src="http://9x9ui.s3.amazonaws.com/war/v0/javascripts/admin-portal-ui.js"></script>
<title>Flipr Invitation</title>
<style>
.errorblock {
	color: #ff0000;
	background-color: #ffEEEE;
	border: 3px solid #ff0000;
	padding: 8px;
	margin: 16px;
}
</style>
</head>
<body>
<div id="header">
  <img src="http://9x9ui.s3.amazonaws.com/war/v0/images/9x9.tv.png" id="logo">
  <a href="<c:url value="/j_spring_security_logout" />" id="btn-signout"> Logout</a>
  <span id="btn-account">${admin}</span>
</div>
<div id="ui_tabs">
	<h3>Join your friend on fliper</h3> 
	<c:if test="${not empty error}">
		<div class="errorblock">
			Your login attempt was not successful, try again.<br /> Caused :
			${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}
		</div>
	</c:if>
 
	<form name='f' action="/share/invite/response" method='POST'>
	    <input type="hidden" name='token' value = ${token} />	    	
		<table>
			<c:if test="${old == 'y'}">
				This invitation has been used.   
			</c:if>
		    <c:if test="${resp == 'y'}">
		       Now enjoy your friend's channel on flipr.
			</c:if>
			<c:if test="${exist == 'n'}">
			   Your temporary password is 123456.
			</c:if>
		    <c:if test="${resp == 'n'}">
			<tr>
				Too bad. Maybe next time.
			</tr>
		    </c:if>			
		</table>
 
	</form>  
</div><!-- id="ui_tabs" -->

<div id="footer">
  <p id="copyright">&nbsp; &copy; 2010 9x9CloudTV. All rights reserved.</p>
</div>
</body>
</html>
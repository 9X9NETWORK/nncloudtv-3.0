<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
</head>
<body>

<form name="input" action="/admin/content/form" method="post">
<div>${message}</div>  
<div>key<input id="key" name="key" type="text" value="${key}"></input></div>
<div>lang (zh, en)<input id="lang" type="text" name="lang" value="${lang}"></input></div>
<div>content<textarea id="text" name="text" rows="10" cols="60">${text}</textarea></div> 
<div><input type="submit"  value="submit"/></div>
</form>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<head>
<meta charset="utf-8" />
<link rel="stylesheet" href="http://9x9ui.s3.amazonaws.com/war/v0/stylesheets/maintenance.css" />
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
<script src="http://9x9ui.s3.amazonaws.com/war/v0/javascripts/maintenance.js"></script>
<title>9x9.tv</title>

<script type="text/javascript">
  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-21595932-1']);
  _gaq.push(['_setDomainName', '.9x9.tv']);
  _gaq.push(['_trackPageview']);
  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
</script>

</head>

<body>
<div id="maintenance-layer">
  <div id="maintenance-holder">
    <img src="https://s3.amazonaws.com/9x9ui/war/v0/images/9x9.tv.png" id="maintenance-logo">
    <p class="maintenance-text" id="en">Sorry, this page is not available.</p>
    <p class="maintenance-text" id="cn">啊很抱歉找不到這個網頁.</p>
  </div>
</div>
</body>
</html>
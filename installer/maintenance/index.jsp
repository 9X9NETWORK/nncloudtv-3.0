<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<head>
<meta charset="utf-8" />
<link rel="stylesheet" href="http://9x9ui.s3.amazonaws.com/war/v0/stylesheets/maintenance.css" />
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
<script src="http://9x9ui.s3.amazonaws.com/war/v0/maintenance.js"></script>
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
    <p class="maintenance-text" id="en">We are working on system upgrades to improve our service.<br>Please return later for better browsing experience.</p>
    <p class="maintenance-text" id="cn">為了提供您更好的服務，目前正在進行系統升級。<br>請稍後再拜訪我們，享受更愉快視覺饗宴！</p>
  </div>
</div>
</body>
</html>
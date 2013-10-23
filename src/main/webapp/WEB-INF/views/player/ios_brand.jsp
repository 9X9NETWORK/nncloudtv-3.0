<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
<meta charset="utf-8" />
<meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0;" name="viewport" />
<meta name="apple-mobile-web-app-capable" content="yes">
<link rel="stylesheet" href="http://s3-us-west-2.amazonaws.com/9x9pm1/facebook_app1/${brandName}/stylesheets/main.css" />
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"/>
<title>9x9.tv</title>
<script type="text/javascript">
  var url = '${reportUrl}';
  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-21595932-1']);
  _gaq.push(['set', 'page', '/transition']);
  _gaq.push(['_trackPageview', url]);
  _gaq.push(['_trackPageview', '/transition']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>
</head>
<body>

<c:import charEncoding="UTF-8" url="http://s3-us-west-2.amazonaws.com/9x9pm1/facebook_app1/${brandName}/deployed.html"/>

<script type="text/javascript">
   $(document).ready(function(){
        $("#storeUrl").attr("href", "${storeUrl}");
        $("#fliprUrl").attr("href", "${fliprUrl}");
   });
</script>

</body>
</html>
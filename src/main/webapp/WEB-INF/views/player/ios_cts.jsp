<!DOCTYPE html>
<head>
<meta charset="utf-8" />
<meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0;" name="viewport" />
<meta name="apple-mobile-web-app-capable" content="yes">
<link rel="stylesheet" href="http://s3-us-west-2.amazonaws.com/9x9pm1/facebook_app1/cts/stylesheets/main.css" />
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
<div id="ios-holder">
    <div id="ios-content">
    <span class="logo"></span>
    <h1>臺灣微電影節    花非花霧非霧    華視精選節目</h1>
    <h2>華視雲端網現正熱映</h2>
  </div>
  <div id="ios-btn">
    <div class="btn-white" id="btn-ios-download">
      <a href="${storeUrl}" onclick="_gaq.push(['_trackEvent','download','download']);">下載 App</a>
    </div>
    <div class="btn-white" id="btn-ios-launch">
      <a href="${fliprUrl}" onclick="_gaq.push(['_trackEvent','launch','launch']);">打開 App</a>
    </div>
  </div>
</div>
<div id="ios-view">
  <img src="http://s3-us-west-2.amazonaws.com/9x9pm1/facebook_app1/ipad.png">
</div>
</body>
</html>
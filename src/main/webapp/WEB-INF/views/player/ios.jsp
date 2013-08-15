<!DOCTYPE html>

<head>
<meta charset="utf-8" />
<meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0;" name="viewport" />   
<link rel="stylesheet" href="https://s3-us-west-2.amazonaws.com/9x9pm/facebook_app_01/stylesheets/main.css" />
<title>9x9.tv</title>
<script type="text/javascript">

  var url = '${reportUrl}';
  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-21595932-1']);
  _gaq.push(['_trackPageview', url]);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>

</head>

<body>

<div id="ios-layer">
    <div id="ios-holder">
        <div id="ios-content">
            <span class="logo"></span>
            <h1>Fresh video channels<br/> curated daily just for you</h1>
            <h2>Find what you like to watch in 9 flips</h2>
            <div id="ios-btn">                
                <div class="btn-white" id="btn-ios-download" onclick="location.href='https://itunes.apple.com/app/9x9.tv/id443352510?mt=8';">
                    <p class="btn-white-left"></p>
                    <p class="btn-white-middle">Download 9x9.tv App</p>
                    <p class="btn-white-right"></p>
                </div> 
                <div class="btn-white" id="btn-ios-launch" onclick="location.href='${fliprUrl}';">
                    <p class="btn-white-left"></p>
                    <p class="btn-white-middle">Launch 9x9.tv App</p>
                    <p class="btn-white-right"></p>
                </div> 
            </div>
        </div>
        <div id="ios-view">
            <img src="https://s3-us-west-2.amazonaws.com/9x9pm/facebook_app_01/thumbnail/ios-view1.png">
        </div>
    </div>
    <div class="reco-wall"></div>
    <div class="reco-shelf"></div>
</div>

</body>
</html>
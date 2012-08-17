<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><!DOCTYPE html>
<html itemscope itemtype="http://schema.org/">
<head>
<meta charset="UTF-8" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="root" value="http://9x9ui.s3.amazonaws.com/9x9playerV68a"/>
<c:set var="nroot" value="http://9x9ui.s3.amazonaws.com/mock7"/>

<!-- $Revision: 2612 $ -->

<!-- Google+ Sharing meta data -->
<meta itemprop="name" content="${fbName}">
<meta itemprop="description" content="${fbDescription}">
<meta itemprop="image" content="${fbImg}"><!-- Google+ requires thumbnail size at least 125px -->

<!-- FB Sharing meta data -->
<meta name="title" content="${fbName}" />
<meta name="description" content="${fbDescription}" />

<link rel="image_src" href="${fbImg}" />

<meta property="og:title" content="${fbName}"/>
<meta property="og:image" content="${fbImg}"/>
<meta property="og:description" content="${fbDescription}"/>

<link rel="stylesheet" href="${nroot}/stylesheets/main.css" />
<link rel="stylesheet" href="${nroot}/stylesheets/temporary.css" />

<link rel="stylesheet" href="http://9x9ui.s3.amazonaws.com/contest/contest.css" />

<script type="text/javascript" charset="utf-8" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>

<script type="text/javascript" charset="utf-8" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.8/jquery-ui.min.js"></script>
<script type="text/javascript" charset="utf-8" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.8/i18n/jquery-ui-i18n.min.js"></script>

<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/all.js"></script>
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/scripts/swfobject.js"></script>
<script type="text/javascript" charset="utf-8" src="${root}/javascripts/jquery.swfobject.1-1-1.min.js"></script>
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/scripts/flowplayer-3.2.4.min.js"></script>
<script type="text/javascript" charset="utf-8" src="${nroot}/javascripts/jquery.ellipsis.js"></script>

<c:if test="${js == \"\"}">
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/mogwai.js"></script>
</c:if>
<c:if test="${js != \"\"}">
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/${js}.js"></script>
</c:if>

<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/vertical.slider.V2.js"></script>
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/jquery.mousewheel.min.js"></script>
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/jquery.ba-hashchange.min.js"></script>

<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/soundmanager/soundmanager2.js"></script>
                                                                                                                        
<script type="text/javascript">
var analytz = false;
var _gaq = _gaq || [];
var acct = document.location.host.match (/(dev|stage|alpha)/) ? 'UA-31930874-1' : 'UA-21595932-1';
_gaq.push(['_setAccount', acct]);
_gaq.push(['_setDomainName', '.9x9.tv']);
_gaq.push(['_trackPageview']);
function analytics()
  {
  if (!analytz)
    {
    log ('submitting analytics');
    (function() {
      var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
      ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
      var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
      })();
    setTimeout ("_gaq.push(['_trackEvent', 'NoBounce', '10 second ping'])", 10000);
    analytz = true;
    }
  }
</script>

<!-- Quantcast Tag -->
<script type="text/javascript">
var _qevents = _qevents || [];
(function() {
var elem = document.createElement('script');
elem.src = (document.location.protocol == "https:" ? "https://secure" : "http://edge") + ".quantserve.com/quant.js";
elem.async = true;
elem.type = "text/javascript";
var scpt = document.getElementsByTagName('script')[0];
scpt.parentNode.insertBefore(elem, scpt);
})();
_qevents.push({
qacct:"p-b2xUunKYSaIeQ"
});
</script>
<noscript>
<div style="display:none;">
<img src="//pixel.quantserve.com/pixel/p-b2xUunKYSaIeQ.gif" border="0" height="1" width="1" alt="Quantcast"/>
</div>
</noscript>
<!-- End Quantcast tag -->

<script type="text/javascript">
var brandinfo = "${brandInfo}";
</script>

<script type="text/javascript">
soundManager.url = '/player/';
soundManager.useFlashBlock = false;
soundManager.onready(function()
  {
  log ('***************************************** SOUND MANAGER READY **********************************************\n');
  });
</script>

<title>9x9.tv</title>

</head>

<body id="body" style="overflow: hidden">

<div id="blue" style="background: black; width: 100%; height: 100%; display: block; position: absolute; color: white">
</div>

<div id="audio1" style="display:block;width:750px;height:30px;visibility:hidden" href=""></div>
<div id="audio2" style="display:block;width:750px;height:30px;visibility:hidden" href=""></div>
<div id="audio3" style="display:block;width:750px;height:30px;visibility:hidden" href=""></div>

<div id="preload-control-images" style="display: none"></div>

<div id="log-layer" style="position: absolute; left: 0; top: 0; height: 100%; width: 100%; background: white; color: black; text-align: left; padding: 20px; overflow: scroll; z-index: 9999; display: none"></div>

<div id="fb-root"></div>

<div id="relaydiv" style="z-index: 1; position: absolute; top: 0px; left: 0px; width=500px; height=500px">
</div>



<!-- Header Begin -->
<div id="header">
  <p id="logo"></p>
  <ul id="nav">
    <li id="home"><span>Home</span></li>
    <li id="guide"><span>Guide</span></li>
    <li id="browse">
      <span>Browse</span>
    </li>
    <li id="search">
      <p id="btn-search"></p>
      <input type="text" class="textfield" value="Search" id="search-field">
    </li>
    <li id="profile">
      <p id="btn-profile"></p>
      <p id="selected-profile">Snowball</p>
      <ul id="profile-dropdown" class="dropdown">
        <li>My page</li>
        <li>Studio</li>
        <li>Settings</li>
        <li>Log out</li>
      </ul>
    </li>
  </ul>
</div>
<!-- Header End -->

<!-- Curator Bubble Begin -->
<div id="curator-bubble">
  <ul id="icon-social">
    <li id="twitter"></li>
    <li id="facebook"></li>
    <li id="blogger"></li>
    <li id="google"></li>
  </ul>
  <div id="curator-info" class="ellipsis multiline">
    <p id="curator-thumb"><img src="thumbnail/curator_09.png"></p>
    <p id="curator-name-bubble">Liz Lisa</p>
    <p id="curator-intro">The idea behind curators & content curation is that here is such a flood of new content through the Internet pipes these days that being aware of all of it.</p>
  </div>
  <div id="curator-ch-meta">
    <p id="top-ch-name">My Top Channel: Coffee Shop</p> 
    <p>50 Episodes<span class="divider">|</span>1 day ago</p>
    <p>100 Views</p>
  </div>
  <p class="icon-pl"></p>
  <img src="thumbnail/coffee_01.png" class="thumb1"><img src="thumbnail/coffee_02.png" class="thumb2"><img src="thumbnail/coffee_03.png" class="thumb3">
</div>
<!-- Curator Bubble End -->

<!-- Footer Begin -->
<p id="footer-control"></p>
<div id="footer">
  <ul id="curator-list">
    <li id="list-title">Featured Curators</li>
    <li><img src="thumbnail/curator_01.png"></li>
    <li><img src="thumbnail/curator_02.png"></li>
    <li><img src="thumbnail/curator_03.png"></li>
    <li><img src="thumbnail/curator_04.png"></li>
    <li><img src="thumbnail/curator_05.png"></li>
    <li><img src="thumbnail/curator_06.png"></li>
    <li><img src="thumbnail/curator_07.png"></li>
    <li><img src="thumbnail/curator_08.png"></li>
    <li><img src="thumbnail/curator_09.png"></li>
  </ul>
  <ul id="footer-list">
    <li id="siteinfo">
      <p id="btn-siteinfo"></p>
      <p id="selected-siteinfo">Company</p>
      <ul id="siteinfo-dropdown" class="dropdown">
        <li class="on">Company</li>
        <li>Blog</li>
        <li>Forum</li>
      </ul>
    </li>
    <li id="sitelang">
      <p id="btn-sitelang"></p>
      <p id="selected-sitelang">English Site</p>
      <ul id="sitelang-dropdown" class="dropdown">
        <li class="on">English Site</li>
        <li>中文網站</li>
      </ul>
    </li>
  </ul>
  <p id="copyright"><span>&copy; 2012 9x9.tv.  All right reserved</span></p>
</div>
<!-- Footer End -->

<!-- Video Layer Begin -->
<div id="video-layer" class="stage">
  <div id="video-constrain">
    <iframe width="100%" height="100%" src="http://www.youtube.com/v/dySwrhMQdX4?wmode=transparent&version=3&autoplay=0&controls=0&showinfo=0" frameborder="0"></iframe>
  </div>
  <div id="video-control">
    <p id="btn-knob"></p>
    <div id="progress-bar">
      <p id="loaded"></p>
      <p id="played"></p>
    </div>
    <ul>
      <li id="btn-play"></li>
      <li id="btn-pause"></li>
      <li id="volume-control">
        <p id="btn-volume" class="loud"></p>
        <p id="volume-bar"></p>
        <p id="btn-dragger"></p>
      </li>
      <li id="left-edge"></li>
      <li id="btn-shopping"></li>
      <li id="btn-expand"></li>
      <li id="btn-shrink"></li>
      <li id="play-time">
        <span id="played-length">0:14</span><span class="divider">/</span><span id="total-length">3:25</span>
      </li>
      <li id="right-edge"></li>
    </ul>
  </div>
</div>
<!-- Video Layer End -->

<!-- Player Layer Begin -->
<div id="player-layer" class="stage">
  <div id="player-sidebar">
    <p id="player-sidebar-shadow"></p>
    <div id="bar-controller">
      <p id="bar-arrowup"></p>
      <p id="bar-arrowdown"></p>
    </div>
    <div id="pl-switcher">
      <ul id="pl-menu"><li id="trending" class="on"></li><li id="recommendation"></li><li id="myfollow1"></li><li id="myfollow2"></li></ul>
      <p id="pl-type">Trending Stories</p>
      <p id="pl-note">(Sorted by updated time)</p>
    </div>
    <div id="pl-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
    <div id="pl-constrain">
    <ul id="pl-list">
      <li class="on">
        <p class="btn-quickfollow"></p>
        <p class="pl-hilite"></p>
        <p class="pl-title-line"><span class="pl-number">01.</span><span class="pl-title">Mountain Bike</span></p>
        <p class="pl-curator-line"><span>by</span><span class="pl-curator">Moutain Biker</span></p>
        <p class="icon-pl"></p>
        <img src="thumbnail/01-1.png" class="thumb1"><img src="thumbnail/01-2.png" class="thumb2"><img src="thumbnail/01-3.png" class="thumb3">
        <p class="pl-sign"><span>1 day ago</span></p>
      </li>
      <li>
        <p class="btn-quickfollow"></p>
        <p class="pl-hilite"></p>
        <p class="pl-title-line"><span class="pl-number">02.</span><span class="pl-title">Mountain Bike's Home</span></p>
        <p class="pl-curator-line"><span>by</span><span class="pl-curator">Moutain Biker</span></p>
        <p class="icon-pl"></p>
        <img src="thumbnail/02-1.png" class="thumb1"><img src="thumbnail/02-2.png" class="thumb2"><img src="thumbnail/02-3.png" class="thumb3">
        <p class="pl-sign"><span>2 hours ago</span></p>
      </li>
      <li>
        <p class="btn-quickfollow"></p>
        <p class="pl-hilite"></p>
        <p class="pl-title-line"><span class="pl-number">03.</span><span class="pl-title">In The Mountain</span></p>
        <p class="pl-curator-line"><span>by</span><span class="pl-curator">Andy Lin</span></p>
        <p class="icon-pl"></p>
        <img src="thumbnail/03-1.png" class="thumb1"><img src="thumbnail/03-2.png" class="thumb2"><img src="thumbnail/03-3.png" class="thumb3">
        <p class="pl-sign"><span>1 day ago</span></p>
      </li>
      <li>
        <p class="btn-quickfollow"></p>
        <p class="pl-hilite"></p>
        <p class="pl-title-line"><span class="pl-number">04.</span><span class="pl-title">In The Mountain</span></p>
        <p class="pl-curator-line"><span>by</span><span class="pl-curator">Andy Lin</span></p>
        <p class="icon-pl"></p>
        <img src="thumbnail/04-1.png" class="thumb1"><img src="thumbnail/04-2.png" class="thumb2"><img src="thumbnail/04-3.png" class="thumb3">
        <p class="pl-sign"><span>1 day ago</span></p>
      </li>
      <li>
        <p class="btn-quickfollow"></p>
        <p class="pl-hilite"></p>
        <p class="pl-title-line"><span class="pl-number">05.</span><span class="pl-title">In The Mountain</span></p>
        <p class="pl-curator-line"><span>by</span><span class="pl-curator">Andy Lin</span></p>
        <p class="icon-pl"></p>
        <img src="thumbnail/05-1.png" class="thumb1"><img src="thumbnail/05-2.png" class="thumb2"><img src="thumbnail/05-3.png" class="thumb3">
        <p class="pl-sign"><span>1 day ago</span></p>
      </li>
      <li>
        <p class="btn-quickfollow"></p>
        <p class="pl-hilite"></p>
        <p class="pl-title-line"><span class="pl-number">06.</span><span class="pl-title">In The Mountain</span></p>
        <p class="pl-curator-line"><span>by</span><span class="pl-curator">Andy Lin</span></p>
        <p class="icon-pl"></p>
        <img src="thumbnail/01-1.png" class="thumb1"><img src="thumbnail/01-2.png" class="thumb2"><img src="thumbnail/01-3.png" class="thumb3">
        <p class="pl-sign"><span>1 day ago</span></p>
      </li>
      <li>
        <p class="btn-quickfollow"></p>
        <p class="pl-hilite"></p>
        <p class="pl-title-line"><span class="pl-number">07.</span><span class="pl-title">In The Mountain</span></p>
        <p class="pl-curator-line"><span>by</span><span class="pl-curator">Andy Lin</span></p>
        <p class="icon-pl"></p>
        <img src="thumbnail/02-1.png" class="thumb1"><img src="thumbnail/02-2.png" class="thumb2"><img src="thumbnail/02-3.png" class="thumb3">
        <p class="pl-sign"><span>1 day ago</span></p>
      </li>
      <li>
        <p class="btn-quickfollow"></p>
        <p class="pl-hilite"></p>
        <p class="pl-title-line"><span class="pl-number">08.</span><span class="pl-title">In The Mountain</span></p>
        <p class="pl-curator-line"><span>by</span><span class="pl-curator">Andy Lin</span></p>
        <p class="icon-pl"></p>
        <img src="thumbnail/03-1.png" class="thumb1"><img src="thumbnail/03-2.png" class="thumb2"><img src="thumbnail/03-3.png" class="thumb3">
        <p class="pl-sign"><span>1 day ago</span></p>
      </li>
      <li>
        <p class="btn-quickfollow"></p>
        <p class="pl-hilite"></p>
        <p class="pl-title-line"><span class="pl-number">09.</span><span class="pl-title">In The Mountain</span></p>
        <p class="pl-curator-line"><span>by</span><span class="pl-curator">Andy Lin</span></p>
        <p class="icon-pl"></p>
        <img src="thumbnail/03-1.png" class="thumb1"><img src="thumbnail/03-2.png" class="thumb2"><img src="thumbnail/03-3.png" class="thumb3">
        <p class="pl-sign"><span>1 day ago</span></p>
      </li>
    </ul>
    </div>
  </div>
    
  <div id="player-holder">
    <div id="player-ch-info">
      <p id="ch-title">Moutain Bike Channel</p>
      <p id="ep-title">Mountain Biker gets taken out by BUCK - CRAZY Footage - in Africa</p>
      <p id="btn-follow"><span>Follow this Channel</span></p>
      <div class="fb-like">
	  	<div id="fb-root"></div>
		<div class="fb-like" data-send="false" data-layout="button_count" data-show-faces="false" data-font="arial"></div>
      </div>
      <ul class="favorite">
        <li class="favorite-head"></li>
        <li class="favorite-body"><span>Favorite</span></li>
        <li class="favorite-tail"></li>
        <li class="favorite-bubble-left"></li>
        <li class="favorite-bubble-center"><span>172K</span></li>
        <li class="favorite-bubble-right"></li>
      </ul>
    </div>
    <p id="video-placeholder"></p>
    <div id="player-ep-bar">
      <p id="btn-ep-left"></p>
      <p id="btn-ep-right"></p>
      <p id="player-ep-meta">
        <span class="ep-title">Mountain Biker gets taken out by BUCK - CRAZY Footage</span>
        <span class="ep-index">(3/50)</span>
        <span>-</span>
        <span class="ep-age">3 months ago</span>
      </p>
      <div id="player-ep-constrain">
        <ul id="player-ep-list">
        </ul>
      </div>
    </div>
    <div id="player-ep-source">
      <p id="ch-source">From <span>Mountain Biker gets taken out by BUCK Footage</span></p>
      <p id="video-source">on <span>YouTube</span></p>
      <p id="curator-source">by <span>Moutain Biker's News</span></p>
    </div>
    <div id="curator-photo"><img src="thumbnail/curator_01.png"></div>
    <div id="curator-title">
      <span>Curator:</span>
      <p id="curator-name">Moutain Biker</p>
    </div>
    <p id="curator-description" class="ellipsis multiline">To watch mountain bike videos. Bikes, gear reviews and ratings from other riders. To watch mountain bike videos. Bikes, gear reviews and ratings from other riders. Don't wait no longer. Subscript now.</p>
    <div id="bubble">
      <p id="bubble-title">Show me more</p>
      <p id="bubble-wording" class="ellipsis multiline">Want to buy a new stuff for your bike?</p>
    </div>
    <div id="flipr">
      <p id="flipr-playpause"></p>
      <p id="flipr-arrowup"></p>
      <p id="flipr-arrowdown"></p>
      <p id="flipr-arrowleft"></p>
      <p id="flipr-arrowright"></p>
    </div>
  </div>
</div>
<!-- Player Layer End -->

<!-- Curator Layer Begin -->
<div id="curator-layer" class="stage curator">
  <div id="curator-sidebar">
    <p id="curator-sidebar-shadow"></p>
    <p id="curator-profile-photo"><img src="thumbnail/curator_05.png"></p>
    <h2>Snowball</h2>
    <div id="curator-paragraphs">
      <p id="curator-declaration">Hi,<br>Welcome to visit my channels.<br>I love here!</p>
      <p id="curator-email">My E-mail:<br>snowball@9x9.tv</p>
      <p id="curator-blog">My Blog:<br>http://esnowball.blogspot.com</p>
      <p id="curator-page">My Page's URL:<br>www.9x9.tv/curator/24225524222<br><span class="link">Create customized short URL</span></p>
    </div>
    <p class="curator-sidebar-btn" id="btn-edit-curator">Edit</p>
    <p class="curator-sidebar-btn" id="btn-about-curator">About Snowball</p>
    <ul id="curator-activity">
      <li>
        <p class="number">13</p>
        <p class="item">channels</p>
      </li>
      <li>
        <p class="number">20</p>
        <p class="item">followings</p>
      </li>
      <li>
        <p class="number">3,500</p>
        <p class="item">followers</p>
      </li>
    </ul>
  </div>
  <ul id="curator-tabs"><li class="left on" id="channel"><span id="btn-manage"></span><div><p><span class="name">Snowball</span>'s Channels<span class="number">(13)</span></p></div></li><li class="right" id="following"><div><p><span class="name">Snowball</span>'s Followings<span class="number">(20)</span></p></div></li></ul>
  <ul id="manage-tip">
    <li id="tip-left"></li>
    <li id="tip-center">Manage your channels</li>
    <li id="tip-right"></li>
  </ul>
  <div class="curator-panel" id="channel-panel">
    <div id="channel-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
    <div class="curator-panel-constrain" id="channel-constrain">
      <ul id="channel-list">
        <li class="default">
          <p class="channel-title">Snowball's Favorites</p>
          <p class="btn-action"><span>Follow</span></p>
          <p class="icon-pl"></p>
          <img src="thumbnail/coffee_01.png" class="thumb1">
          <img src="thumbnail/coffee_02.png" class="thumb2">
          <img src="thumbnail/coffee_03.png" class="thumb3">
          <div class="channel-meta">
            <p>30 Episodes<span class="divider">|</span>10 minutes ago</p>
            <p class="next-update">Next Update: Tomorrow</p>
          </div>
          <p class="channel-description ellipsis multiline">Coffee reviews and article by coffee group and experts. Coffee is a brewed beverage with a flavor prepared from roasted seeds of the coffee plant.</p>
          <p class="default-channel-title">Snowball's Favorite</p>
          <ul class="follower-list">
            <li></li>
          </ul>
          <div class="channel-popularity">
            <p>0 Followers</p>
            <p>0 Viewers</p>
          </div>
        </li>
        <li class="new-channel">
          <div><p>Create a<br>new channel</p></div>
        </li>
        <li>
          <p class="channel-title">Coffee Shop</p>
          <p class="btn-action"><span>Follow</span></p>
          <p class="icon-pl"></p>
          <img src="thumbnail/coffee_01.png" class="thumb1">
          <img src="thumbnail/coffee_02.png" class="thumb2">
          <img src="thumbnail/coffee_03.png" class="thumb3">
          <div class="channel-meta">
            <p>30 Episodes<span class="divider">|</span>10 minutes ago</p>
            <p class="next-update">Next Update: Tomorrow</p>
          </div>
          <p class="channel-description">Coffee reviews and article by coffee group and experts. Coffee is a brewed beverage with a flavor prepared from roasted seeds of the coffee plant.</p>
          <p class="default-channel-title">Snowball's Favorite</p>
          <ul class="follower-list">
            <li><img src="thumbnail/follower_01.png"></li>
            <li><img src="thumbnail/follower_02.png"></li>
            <li><img src="thumbnail/follower_03.png"></li>
          </ul>
          <div class="channel-popularity">
            <p>0 Followers</p>
            <p>0 Viewers</p>
          </div>
        </li>
        <li>
          <p class="channel-title">Coffee Shop</p>
          <p class="btn-action"><span>Follow</span></p>
          <p class="icon-pl"></p>
          <img src="thumbnail/coffee_01.png" class="thumb1">
          <img src="thumbnail/coffee_02.png" class="thumb2">
          <img src="thumbnail/coffee_03.png" class="thumb3">
          <div class="channel-meta">
            <p>30 Episodes<span class="divider">|</span>10 minutes ago</p>
            <p class="next-update">Next Update: Tomorrow</p>
          </div>
          <p class="channel-description">Coffee reviews and article by coffee group and experts. Coffee is a brewed beverage with a flavor prepared from roasted seeds of the coffee plant.</p>
          <p class="default-channel-title">Snowball's Favorite</p>
          <ul class="follower-list">
            <li><img src="thumbnail/follower_01.png"></li>
            <li><img src="thumbnail/follower_02.png"></li>
            <li><img src="thumbnail/follower_03.png"></li>
          </ul>
          <div class="channel-popularity">
            <p>0 Followers</p>
            <p>0 Viewers</p>
          </div>
        </li>
        <li>
          <p class="channel-title">Coffee Shop</p>
          <p class="btn-action"><span>Follow</span></p>
          <p class="icon-pl"></p>
          <img src="thumbnail/coffee_01.png" class="thumb1">
          <img src="thumbnail/coffee_02.png" class="thumb2">
          <img src="thumbnail/coffee_03.png" class="thumb3">
          <div class="channel-meta">
            <p>30 Episodes<span class="divider">|</span>10 minutes ago</p>
            <p class="next-update">Next Update: Tomorrow</p>
          </div>
          <p class="channel-description">Coffee reviews and article by coffee group and experts. Coffee is a brewed beverage with a flavor prepared from roasted seeds of the coffee plant.</p>
          <p class="default-channel-title">Snowball's Favorite</p>
          <ul class="follower-list">
            <li><img src="thumbnail/follower_01.png"></li>
            <li><img src="thumbnail/follower_02.png"></li>
            <li><img src="thumbnail/follower_03.png"></li>
          </ul>
          <div class="channel-popularity">
            <p>0 Followers</p>
            <p>0 Viewers</p>
          </div>
        </li>
      </ul>
    </div>
  </div>
  <div class="curator-panel" id="following-panel">
    <div id="following-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
    <div class="curator-panel-constrain" id="following-constrain">
      <ul id="following-list">
        <li>
          <p class="channel-title">Summer Vacation</p>
          <p class="btn-action"><span>Follow</span></p>
          <p class="icon-pl"></p>
          <img src="thumbnail/vacation_01.png" class="thumb1">
          <img src="thumbnail/vacation_02.png" class="thumb2">
          <img src="thumbnail/vacation_03.png" class="thumb3">
          <p class="followed-curator-photo"><img src="thumbnail/curator_02.png"></p>
          <div class="channel-meta">
            <p>30 Episodes<span class="divider">|</span>10 minutes ago</p>
            <p class="next-update">Next Update: Tomorrow</p>
          </div>
          <p class="followed-curator">by<span>Cunnie Pan</span></p>
        </li>
        <li>
          <p class="channel-title">Coffee House</p>
          <p class="btn-action"><span>Follow</span></p>
          <p class="icon-pl"></p>
          <img src="thumbnail/coffee_01.png" class="thumb1">
          <img src="thumbnail/coffee_02.png" class="thumb2">
          <img src="thumbnail/coffee_03.png" class="thumb3">
          <p class="followed-curator-photo"><img src="thumbnail/curator_03.png"></p>
          <div class="channel-meta">
            <p>30 Episodes<span class="divider">|</span>10 minutes ago</p>
            <p class="next-update">Next Update: Tomorrow</p>
          </div>
          <p class="followed-curator">by<span>Cunnie Pan</span></p>
        </li>
        <li>
          <p class="channel-title">Mountain Bike</p>
          <p class="btn-action"><span>Follow</span></p>
          <p class="icon-pl"></p>
          <img src="thumbnail/01-1.png" class="thumb1">
          <img src="thumbnail/01-2.png" class="thumb2">
          <img src="thumbnail/01-3.png" class="thumb3">
          <p class="followed-curator-photo"><img src="thumbnail/curator_04.png"></p>
          <div class="channel-meta">
            <p>30 Episodes<span class="divider">|</span>1 day ago</p>
            <p class="next-update">Next Update: Tomorrow</p>
          </div>
          <p class="followed-curator">by<span>Cunnie Pan</span></p>
        </li>
        <li>
          <p class="channel-title">World Class Limited Sport</p>
          <p class="btn-action"><span>Follow</span></p>
          <p class="icon-pl"></p>
          <img src="thumbnail/epi_01.png"class="thumb1">
          <img src="thumbnail/epi_02.png" class="thumb2">
          <img src="thumbnail/epi_03.png" class="thumb3">
          <p class="followed-curator-photo"><img src="thumbnail/curator_08.png"></p>
          <div class="channel-meta">
            <p>30 Episodes<span class="divider">|</span>1 day ago</p>
            <p class="next-update">Next Update: Tomorrow</p>
          </div>
          <p class="followed-curator">by<span>Cunnie Pan</span></p>
        </li>
      </ul>
    </div>
  </div>
</div>
<!-- Curator Layer End -->

<!-- Search Layer Begin -->
<div id="search-layer" class="stage">
  <div id="result-summary">
    <ul>
      <li class="term">Results for<span id="search-term">Travel</span></li>
      <li class="numbers"><span id="curator-found">12</span>Curators<span class="divider">|</span><span id="channel-found">300</span>Channels</li>
    </ul>
  </div>
  <div id="results-constrain">
    <div class="clip" id="curator-result-clip">
      <p class="clip-left"></p>
      <p class="clip-center"><span>12</span>Curators</p>
      <p class="clip-right"></p>
    </div>
    <ul id="curator-pagination">
      <li class="btn-pagination" id="btn-prev-curator">Prev</li>
      <li class="page-number">1</li>
      <li class="page-number">2</li>
      <li class="page-number">3</li>
      <li class="btn-pagination" id="btn-next-curator">Next</li>
    </ul>
    <div id="curator-result-constrain">
      <ul id="curator-result">
        <li>
          <p class="curator-divider"></p>
          <p class="searched-curator-photo"><img src="thumbnail/curator_09.png"></p>
          <p class="curator-name">Liz Lisa</p>
          <p class="channel-popularity"><span class="viewer-number">3.4K</span>Viewer<span class="divider">|</span><span class="follower-number">1K</span>Followers</p>
          <p class="channel-owned"><span class="channel-number">13</span>Channels</p>
          <p class="pl-title-line"><span class="pl-title">Travel in the world</span></p>
          <p class="pl-curator-line"><span>by</span><span class="pl-curator">Liz lisa</span></p>
          <p class="icon-pl"></p>
          <img src="thumbnail/01-1.png" class="thumb1"><img src="thumbnail/01-2.png" class="thumb2"><img src="thumbnail/01-3.png" class="thumb3">
          <p class="pl-sign"><span>1 day ago</span></p>
        </li>
        <li>
          <p class="curator-divider"></p>
          <p class="searched-curator-photo"><img src="thumbnail/curator_09.png"></p>
          <p class="curator-name">Liz Lisa</p>
          <p class="channel-popularity"><span class="viewer-number">3.4K</span>Viewer<span class="divider">|</span><span class="follower-number">1K</span>Followers</p>
          <p class="channel-owned"><span class="channel-number">13</span>Channels</p>
          <p class="pl-title-line"><span class="pl-title">Travel in the world</span></p>
          <p class="pl-curator-line"><span>by</span><span class="pl-curator">Liz lisa</span></p>
          <p class="icon-pl"></p>
          <img src="thumbnail/01-1.png" class="thumb1"><img src="thumbnail/01-2.png" class="thumb2"><img src="thumbnail/01-3.png" class="thumb3">
          <p class="pl-sign"><span>1 day ago</span></p>
        </li>
        <li>
          <p class="curator-divider"></p>
          <p class="searched-curator-photo"><img src="thumbnail/curator_09.png"></p>
          <p class="curator-name">Liz Lisa</p>
          <p class="channel-popularity"><span class="viewer-number">3.4K</span>Viewer<span class="divider">|</span><span class="follower-number">1K</span>Followers</p>
          <p class="channel-owned"><span class="channel-number">13</span>Channels</p>
          <p class="pl-title-line"><span class="pl-title">Travel in the world</span></p>
          <p class="pl-curator-line"><span>by</span><span class="pl-curator">Liz lisa</span></p>
          <p class="icon-pl"></p>
          <img src="thumbnail/01-1.png" class="thumb1"><img src="thumbnail/01-2.png" class="thumb2"><img src="thumbnail/01-3.png" class="thumb3">
          <p class="pl-sign"><span>1 day ago</span></p>
        </li>
        <li>
          <p class="curator-divider"></p>
          <p class="searched-curator-photo"><img src="thumbnail/curator_09.png"></p>
          <p class="curator-name">Liz Lisa</p>
          <p class="channel-popularity"><span class="viewer-number">3.4K</span>Viewer<span class="divider">|</span><span class="follower-number">1K</span>Followers</p>
          <p class="channel-owned"><span class="channel-number">13</span>Channels</p>
          <p class="pl-title-line"><span class="pl-title">Travel in the world</span></p>
          <p class="pl-curator-line"><span>by</span><span class="pl-curator">Liz lisa</span></p>
          <p class="icon-pl"></p>
          <img src="thumbnail/01-1.png" class="thumb1"><img src="thumbnail/01-2.png" class="thumb2"><img src="thumbnail/01-3.png" class="thumb3">
          <p class="pl-sign"><span>1 day ago</span></p>
        </li>
        <li>
          <p class="curator-divider"></p>
          <p class="searched-curator-photo"><img src="thumbnail/curator_09.png"></p>
          <p class="curator-name">Liz Lisa</p>
          <p class="channel-popularity"><span class="viewer-number">3.4K</span>Viewer<span class="divider">|</span><span class="follower-number">1K</span>Followers</p>
          <p class="channel-owned"><span class="channel-number">13</span>Channels</p>
          <p class="pl-title-line"><span class="pl-title">Travel in the world</span></p>
          <p class="pl-curator-line"><span>by</span><span class="pl-curator">Liz lisa</span></p>
          <p class="icon-pl"></p>
          <img src="thumbnail/01-1.png" class="thumb1"><img src="thumbnail/01-2.png" class="thumb2"><img src="thumbnail/01-3.png" class="thumb3">
          <p class="pl-sign"><span>1 day ago</span></p>
        </li>
        <li>
          <p class="curator-divider"></p>
          <p class="searched-curator-photo"><img src="thumbnail/curator_09.png"></p>
          <p class="curator-name">Liz Lisa</p>
          <p class="channel-popularity"><span class="viewer-number">3.4K</span>Viewer<span class="divider">|</span><span class="follower-number">1K</span>Followers</p>
          <p class="channel-owned"><span class="channel-number">13</span>Channels</p>
          <p class="pl-title-line"><span class="pl-title">Travel in the world</span></p>
          <p class="pl-curator-line"><span>by</span><span class="pl-curator">Liz lisa</span></p>
          <p class="icon-pl"></p>
          <img src="thumbnail/01-1.png" class="thumb1"><img src="thumbnail/01-2.png" class="thumb2"><img src="thumbnail/01-3.png" class="thumb3">
          <p class="pl-sign"><span>1 day ago</span></p>
        </li>
      </ul>
    </div>
    <div class="clip" id="channel-result-clip">
      <p class="clip-left"></p>
      <p class="clip-center"><span>30</span>Channels</p>
      <p class="clip-right"></p>
    </div>
    <div id="channel-result-constrain">
      <ul id="channel-result">
        <li>
          <p class="btn-follow">Follow</p>
          <p class="icon-pl"></p>
          <img src="thumbnail/vacation_01.png" class="thumb1">
          <img src="thumbnail/vacation_02.png" class="thumb2">
          <img src="thumbnail/vacation_03.png" class="thumb3">
          <p class="channel-title">Travel is My Life</p>
          <p class="channel-meta">
            <span>75</span>Episodes<br>
            <span>1</span>hour ago<br>
            <span>10K</span>Followers<br>
            <span>90K</span>Views
          </p>
          <p class="curator-name">by<span>Someone</span></p>
        </li>
        <li>
          <p class="btn-follow">Follow</p>
          <p class="icon-pl"></p>
          <img src="thumbnail/vacation_01.png" class="thumb1">
          <img src="thumbnail/vacation_02.png" class="thumb2">
          <img src="thumbnail/vacation_03.png" class="thumb3">
          <p class="channel-title">Travel is My Life</p>
          <p class="channel-meta">
            <span>75</span>Episodes<br>
            <span>1</span>hour ago<br>
            <span>10K</span>Followers<br>
            <span>90K</span>Views
          </p>
          <p class="curator-name">by<span>Someone</span></p>
        </li>
        <li>
          <p class="btn-follow">Follow</p>
          <p class="icon-pl"></p>
          <img src="thumbnail/vacation_01.png" class="thumb1">
          <img src="thumbnail/vacation_02.png" class="thumb2">
          <img src="thumbnail/vacation_03.png" class="thumb3">
          <p class="channel-title">Travel is My Life</p>
          <p class="channel-meta">
            <span>75</span>Episodes<br>
            <span>1</span>hour ago<br>
            <span>10K</span>Followers<br>
            <span>90K</span>Views
          </p>
          <p class="curator-name">by<span>Someone</span></p>
        </li>
        <li>
          <p class="btn-follow">Follow</p>
          <p class="icon-pl"></p>
          <img src="thumbnail/vacation_01.png" class="thumb1">
          <img src="thumbnail/vacation_02.png" class="thumb2">
          <img src="thumbnail/vacation_03.png" class="thumb3">
          <p class="channel-title">Travel is My Life</p>
          <p class="channel-meta">
            <span>75</span>Episodes<br>
            <span>1</span>hour ago<br>
            <span>10K</span>Followers<br>
            <span>90K</span>Views
          </p>
          <p class="curator-name">by<span>Someone</span></p>
        </li>
        <li>
          <p class="btn-follow">Follow</p>
          <p class="icon-pl"></p>
          <img src="thumbnail/vacation_01.png" class="thumb1">
          <img src="thumbnail/vacation_02.png" class="thumb2">
          <img src="thumbnail/vacation_03.png" class="thumb3">
          <p class="channel-title">Travel is My Life</p>
          <p class="channel-meta">
            <span>75</span>Episodes<br>
            <span>1</span>hour ago<br>
            <span>10K</span>Followers<br>
            <span>90K</span>Views
          </p>
          <p class="curator-name">by<span>Someone</span></p>
        </li>
        <li>
          <p class="btn-follow">Follow</p>
          <p class="icon-pl"></p>
          <img src="thumbnail/vacation_01.png" class="thumb1">
          <img src="thumbnail/vacation_02.png" class="thumb2">
          <img src="thumbnail/vacation_03.png" class="thumb3">
          <p class="channel-title">Travel is My Life</p>
          <p class="channel-meta">
            <span>75</span>Episodes<br>
            <span>1</span>hour ago<br>
            <span>10K</span>Followers<br>
            <span>90K</span>Views
          </p>
          <p class="curator-name">by<span>Someone</span></p>
        </li>
        <li>
          <p class="btn-follow">Follow</p>
          <p class="icon-pl"></p>
          <img src="thumbnail/vacation_01.png" class="thumb1">
          <img src="thumbnail/vacation_02.png" class="thumb2">
          <img src="thumbnail/vacation_03.png" class="thumb3">
          <p class="channel-title">Travel is My Life</p>
          <p class="channel-meta">
            <span>75</span>Episodes<br>
            <span>1</span>hour ago<br>
            <span>10K</span>Followers<br>
            <span>90K</span>Views
          </p>
          <p class="curator-name">by<span>Someone</span></p>
        </li>
        <li>
          <p class="btn-follow">Follow</p>
          <p class="icon-pl"></p>
          <img src="thumbnail/vacation_01.png" class="thumb1">
          <img src="thumbnail/vacation_02.png" class="thumb2">
          <img src="thumbnail/vacation_03.png" class="thumb3">
          <p class="channel-title">Travel is My Life</p>
          <p class="channel-meta">
            <span>75</span>Episodes<br>
            <span>1</span>hour ago<br>
            <span>10K</span>Followers<br>
            <span>90K</span>Views
          </p>
          <p class="curator-name">by<span>Someone</span></p>
        </li>
      </ul>
    </div>
  </div>
  <div id="noresult-summary">
    <h3></h3>
    <div id="research">
      <p id="btn-research"></p>
      <input type="text" class="textfield" id="research-field">
    </div>
    <p class="msg">Sorry, can't find any results for that search, Please try a difference one.</p>
  </div>
  <div id="reco-results">
    <div class="clip" id="reco-result-clip">
      <p class="clip-left"></p>
      <p class="clip-center">You May Like</p>
      <p class="clip-right"></p>
    </div>     
    <div id="reco-shelf"><p></p></div>   
    <div id="reco-result-constrain">   
      <ul id="reco-result">
        <li>
          <p class="channel-title">Summer Vacation</p>
          <p class="btn-action"><span>Follow</span></p>
          <p class="icon-pl"></p>
          <img src="thumbnail/vacation_01.png" class="thumb1">
          <img src="thumbnail/vacation_02.png" class="thumb2">
          <img src="thumbnail/vacation_03.png" class="thumb3">
          <p class="followed-curator-photo"><img src="thumbnail/curator_02.png"></p>
          <div class="channel-meta">
            <p>30 Episodes<span class="divider">|</span>10 minutes ago</p>
            <p class="next-update">Next Update: Tomorrow</p>
          </div>
          <p class="followed-curator">by<span>Cunnie Pan</span></p>
        </li>
        <li>
          <p class="channel-title">Coffee House</p>
          <p class="btn-action"><span>Follow</span></p>
          <p class="icon-pl"></p>
          <img src="thumbnail/coffee_01.png" class="thumb1">
          <img src="thumbnail/coffee_02.png" class="thumb2">
          <img src="thumbnail/coffee_03.png" class="thumb3">
          <p class="followed-curator-photo"><img src="thumbnail/curator_03.png"></p>
          <div class="channel-meta">
            <p>30 Episodes<span class="divider">|</span>10 minutes ago</p>
            <p class="next-update">Next Update: Tomorrow</p>
          </div>
          <p class="followed-curator">by<span>Cunnie Pan</span></p>
        </li>
        <li>
          <p class="channel-title">Mountain Bike</p>
          <p class="btn-action"><span>Follow</span></p>
          <p class="icon-pl"></p>
          <img src="thumbnail/01-1.png" class="thumb1">
          <img src="thumbnail/01-2.png" class="thumb2">
          <img src="thumbnail/01-3.png" class="thumb3">
          <p class="followed-curator-photo"><img src="thumbnail/curator_04.png"></p>
          <div class="channel-meta">
            <p>30 Episodes<span class="divider">|</span>1 day ago</p>
            <p class="next-update">Next Update: Tomorrow</p>
          </div>
          <p class="followed-curator">by<span>Cunnie Pan</span></p>
        </li>
        <li>
          <p class="channel-title">World Class Limited Sport</p>
          <p class="btn-action"><span>Follow</span></p>
          <p class="icon-pl"></p>
          <img src="thumbnail/epi_01.png" class="thumb1">
          <img src="thumbnail/epi_02.png" class="thumb2">
          <img src="thumbnail/epi_03.png" class="thumb3">
          <p class="followed-curator-photo"><img src="thumbnail/curator_08.png"></p>
          <div class="channel-meta">
            <p>30 Episodes<span class="divider">|</span>1 day ago</p>
            <p class="next-update">Next Update: Tomorrow</p>
          </div>
          <p class="followed-curator">by<span>Cunnie Pan</span></p>
        </li>  
      </ul>
    </div>
  </div>
</div>
<!-- Search Layer End -->

<!-- Temporary Utility Layers -->
<div id="shazam-layer">
  <div id="shazam-holder" class="shazam-holder">
    <ul id="shazam-input">
      <li><p><input type="text" class="textfield" id="return-email" value="Please enter your email address"></p></li>
      <li><p><input type="text" class="textfield" id="signin-password" value="Please enter your password"></p></li>
    </ul>
    <ul class="action-list">
      <li><p class="btn" id="btn-shazam-signin"><span>Signin</span></p></li>
      <li><p class="btn" id="btn-shazam-cancel"><span>Cancel</span></p></li>
    </ul>
  </div>
</div>

<div id="forgot-layer">
  <div id="forgot-holder" class="forgot-holder">
    <p id="forgot-input">
      <input type="text" class="textfield" value="Please enter your email address">
    </p>
    <ul class="action-list">
      <li><p class="btn disable" id="btn-forgot-retrieve"><span>Retrieve</span></p></li>
      <li><p class="btn" id="btn-forgot-cancel"><span>Cancel</span></p></li>
    </ul>
  </div>
</div>

<div id="waiting-layer">
  <div id="waiting-holder">
    <img src="temporary/loading.gif">
    <p>One moment...</p>
  </div>
</div>

<div id="msg-layer">
  <div id="msg-holder">
    <p><span>No program in this channel</span></p>
  </div>
</div>

<div id="confirm-layer">
  <div id="confirm-holder">
    <p><span id="confirm-text">Thank you for using 9x9.tv. You have signed out.</span></p>
    <p class="btn" id="btn-confirm-close"><span>Close</span></p>
  </div>
</div>

<div id="error-layer">
  <div id="error-holder">
    <img src="temporary/icon_error.png" id="icon-error">
    <p><span>Ooops....9x9 player has crashed.<br>Please reload the page to try again.</span></p>
    <p class="btn" id="btn-error-ok"><span>OK</span></p>
  </div>
</div>

<div id="yesno-layer">
  <div id="yesno-holder">
    <p><span>You will be sharing the Public section of your Guide with your Facebook friends. Continue?</span></p>
    <ul class="action-list">
      <li><p class="btn" id="btn-yesno-yes"><span>Yes</span></p></li>
      <li><p class="btn" id="btn-yesno-no"><span>No</span></p></li>
    </ul>
  </div>
</div>

<div id="tribtn-layer">
  <div id="tribtn-holder">
    <p><span>The channel you follow is added successfully!</span></p>
    <ul class="action-list">
      <li><p class="btn"><span>Watching this Set</span></p></li>
      <li><p class="btn"><span>Back to Add Featured Sets</span></p></li>
      <li><p class="btn"><span>Return to Smart Guide</span></p></li>
    </ul>
  </div>
</div>

<div id="success-layer">
  <div id="success-holder">
    <p class="greeting"><span>Success!</span></p>
    <p><span>Welcome to 9x9! Now you can access thousand of curated channels!</span></p>
    <p><span>Browse throuth out Channel Store, add new channels to your personalized progamming guide and watch your favorite channelsjust like how you would on a TV!</span></p>
    <ul class="action-list">
      <li><p class="btn on" id="btn-success2store"><span>Start Adding Channels</span></p></li>
    </ul>
  </div>
</div>
<!-- End Temporary Utility Layers -->


</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><!DOCTYPE html>
<html itemscope itemtype="http://schema.org/">
<head>
<meta charset="UTF-8" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="root" value="http://9x9ui.s3.amazonaws.com/9x9playerV68a"/>
<c:set var="nroot" value="http://9x9ui.s3.amazonaws.com/mock24"/>

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

<!--
<script type="text/javascript" charset="utf-8" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
<script type="text/javascript" charset="utf-8" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.8/jquery-ui.min.js"></script>
<script type="text/javascript" charset="utf-8" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.8/i18n/jquery-ui-i18n.min.js"></script>
-->

<script type="text/javascript" charset="utf-8" src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js"></script>
<script type="text/javascript" charset="utf-8" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js"></script>

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

<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/vertical.slider.V3.js"></script>
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/vertical.slider.HOME.js"></script>
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/jquery.mousewheel.min.js"></script>
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/jquery.ba-hashchange.min.js"></script>

<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/soundmanager/soundmanager2.js"></script>
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/nn-sdk.js"></script>
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/jquery.titlecard.js"></script>

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

<div id="blue" style="background-color: #dedede; width: 100%; height: 100%; display: block; position: absolute; color: white">
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
  <span id="secret-message" style="left: 10%; position: absolute"></span>
  <ul id="nav">
    <li id="home"><span>Home</span></li>
    <li id="guide"><span>Guide</span></li>
    <li id="browse"><span>Browse</span></li>
    <li id="search">
      <p id="btn-search"></p>
      <input type="text" class="textfield" value="Search" id="search-input">
    </li>
    <li id="profile">
      <p id="btn-profile"></p>
      <p id="selected-profile">Snowball</p>
      <ul id="profile-dropdown" class="dropdown">
        <li id="mypage">My page</li>
        <li id="studio">Studio</li>
        <li id="settings">Settings</li>
        <li id="logout">Log out</li>
      </ul>
    </li>
    <li id="signin"><p id="signin-btn">Sign in / up</p></li>
  </ul>
</div>
<!-- Header End -->

<!-- Curator Bubble Begin -->
<div id="curator-bubble">
  <ul id="icon-social">
    <li id="facebook"></li>
  </ul>
  <div id="curator-info" class="ellipsis multiline">
    <p id="curator-thumb"><img src="thumbnail/curator_09.png"></p>
    <p id="curator-name-bubble">Liz Lisa</p>
    <p id="curator-intro"></p>
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
  </ul>
  <ul id="footer-list">
    <li id="developer">
      <p id="btn-developer"></p>
      <p id="selected-developer">About us</p>
      <ul id="developer-dropdown" class="dropdown">
        <li data-doc="v-about">About us</li>                                 
        <li data-doc="v-help">Help</li>
        <li data-doc="v-report">Report</li>
        <li data-doc="v-terms">Terms & Policy</li>
        <li data-doc="v-contact">Contact us</li>
        <li data-doc="v-partners">Partners</li>
        <li data-doc="v-curators">Curators</li>
        <li data-doc="v-press">Press</li>
        <li data-doc="v-curation">Curation Contest</li>
      </ul>
    </li>
    <li id="sitelang">
      <p id="btn-sitelang"></p>
      <p id="selected-sitelang">Language</p>
      <ul id="sitelang-dropdown" class="dropdown">
        <li class="on">English Site</li>
        <li>中文網站</li>
      </ul>
    </li>
    <li id="sitelocation">
      <p id="btn-sitelocation"></p>
      <p id="selected-sitelocation">Location</p>
      <ul id="sitelocation-dropdown" class="dropdown">
        <li class="on">USA</li>
        <li>台灣</li>
      </ul>
    </li>
  </ul>
  <p id="copyright"><span>&copy; 2012 9x9.tv.  All right reserved</span></p>
</div>
<!-- Footer End -->


<!-- Video Layer Begin -->
<div id="video-layer" class="stage">
  <div id="video-constrain">
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
      <ul id="pl-menu"><li id="trending" title="Trending"></li><li id="recommendation" title="Recommended"></li><li id="myfollow1" title="Featured"></li><li id="myfollow2" title="Your Subscriptions"></li></ul>
      <p id="pl-type">Trending Stories</p>
      <p id="pl-note">(Sorted by updated time)</p>
      <div id="popmessage-player-list" style="display: none"><p class="popmessage-left"></p><p class="popmessage-middle">Added to <span>your Guide.</span></p><p class="popmessage-right"></p></div>
    </div>
    <div id="pl-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
    <div id="pl-constrain">
    <ul id="pl-list">
    </ul>
    </div>
  </div>
    
  <div id="player-holder">
    <div id="player-ch-info" style="display: none">
      <p id="ch-title"></p>
      <p id="ep-title"></p>
      <p id="btn-follow"><span>Follow this Channel</span></p>
      <ul class="favorite">
        <li class="favorite-head"></li>
        <li class="favorite-body"><span>Favorite</span></li>
        <li class="favorite-tail"></li>
        <li class="favorite-bubble-left"></li>
        <li class="favorite-bubble-center"><span style="display: none">172K</span></li>
        <li class="favorite-bubble-right"></li>
      </ul>
      <div id="fb-like-container" style="display: none">
        <div class="fb-like" data-send="false" data-layout="button_count" data-show-faces="false" data-font="arial" data-href=""></div>
      </div>
      <div id="popmessage-player-info" style="display: none"><p class="popmessage-left"></p><p class="popmessage-middle">This channel has been added to <span>your Guide.</span></p><p class="popmessage-right"></p></div>
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
    <h2 id="curator-profile-name">Snowball</h2>
    <div id="curator-paragraphs">
      <p id="curator-declaration"></p>
      <p id="curator-email"></p>
      <p id="curator-blog"></p>
      <p id="curator-page">My Page's URL:<br><span id="curator-url"></span><br><span class="link">Create customized short URL</span></p>
    </div>
    <p class="curator-sidebar-btn" id="btn-edit-curator" style="display: none">Edit</p>
    <p class="curator-sidebar-btn" id="btn-about-curator"></p>
    <ul id="curator-activity">
      <li>
        <p class="number" id="curator-activity-channels"></p>
        <p class="item">channels</p>
      </li>
      <li>
        <p class="number" id="curator-activity-following"></p>
        <p class="item">I'm following</p>
      </li>
      <li>
        <p class="number" id="curator-activity-followers"></p>
        <p class="item">Following me</p>
      </li>
    </ul>
  </div>
  <ul id="curator-tabs"><li class="left on" id="channel"><span id="btn-manage"></span><div><p><span class="name">Snowball</span>'s Channels<span id="curator-ch-num" class="number">(13)</span></p></div></li><li class="right" id="following"><div><p><span class="name">Snowball</span>'s Followings<span id="curator-fol-num" class="number">(20)</span></p></div></li></ul>
  <ul id="manage-tip">
    <li id="tip-left"></li>
    <li id="tip-center">Manage your channels</li>
    <li id="tip-right"></li>
  </ul>
  <div class="curator-panel" id="channel-panel">
    <div id="channel-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
    <div class="curator-panel-constrain" id="channel-constrain">
      <ul id="channel-list">
      </ul>
    </div>
  </div>
  <div class="curator-panel" id="following-panel">
    <div id="following-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
    <div class="curator-panel-constrain" id="following-constrain">
      <ul id="following-list">
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
  <div id="results-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
  <div id="results-constrain">
    <div id="results-list">
      <div class="clip" id="curator-result-clip">
        <p class="clip-left"></p>
        <p class="clip-center"><span>12</span>Curators</p>
        <p class="clip-right"></p>
      </div>
      <ul id="curator-pagination">
      </ul>
      <div id="curator-result-constrain">
        <ul id="curator-result">
        </ul>
      </div>
      <div class="clip" id="channel-result-clip">
        <p class="clip-left"></p>
        <p class="clip-center"><span>30</span>Channels</p>
        <p class="clip-right"></p>
      </div>
      <div id="channel-result-constrain">
        <ul id="channel-result">
        </ul>
      </div>
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

<!-- Browse Layer Begin -->
<div id="browse-layer" class="stage">
  <div id="categ-bar">
    <ul id="categ-list">
    </ul>
  </div>
  <div id="browse-sort">
     <ul id="sort-list">
       <li class="head">Sorted by</li>
       <li id="sort-by-update" class="on">Update Time</li>
       <li id="sort-by-sub">Most Subscribed</li>
       <li id="sort-by-alpha">Alphabetical</li>
     </ul>
     <div id="tag-area">
       <p id="tag-head">The most popular tags:</p>
       <ul id="tag-list">
       </ul> 
     </div>
  </div>
  <div id="browse-main">
    <div id="browse-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
    <div id="browse-constrain">
      <div id="browse-list">
        <ul id="ch-list">
        </ul>
      </div>
    </div>
  </div>
</div>
<!-- Browse Layer End -->












<!-- Guide Begin -->
<div id="guide-layer" class="stage">
  <h2>I'm Following (<span id="chNum">20</span>/72)</h2>
  
  <div id="guide-bubble">
    <img src="${nroot}/images/bg_guide_bubble1.png" id="bg1" class="bg">
    <img src="${nroot}/images/bg_guide_bubble2.png" id="bg2" class="bg">
    <img src="${nroot}/images/bg_guide_bubble3.png" id="bg3" class="bg">
    <h3><span></span></h3>
    <img src="${nroot}/thumbnail/photo/11.jpg" class="thumbnail1">
    <img src="${nroot}/thumbnail/photo/12.jpg" class="thumbnail2">
    <img src="${nroot}/photo/13.jpg" class="thumbnail3">
    <div id="ch-meta">
      <p><span id="ch-meta-count">10 Episodes</span></p>
      <p><span id="ch-meta-ago">2 day ago</span></p>
      <p><span>by</span><span class="name" id="ch-meta-curator">Vialo</span></p>
      <p id="ch-brief" class="ellipsis multiline"><span></span></p>
    </div>
  </div>
  
  <div id="guide-holder">
      <div class="col">
        <div class="group-input">
          <p><img src="${nroot}/images/bg_group_name.png" class="bg-group-input"><span class="group-name"></span></p>
          <input type="text" class="group-field">
        </div>
        <ul id="col-1-list">
        </ul>
      </div>
      <div class="col">
        <div class="group-input">
          <p><img src="${nroot}/images/bg_group_name.png" class="bg-group-input"><span class="group-name"></span></p>
          <input type="text" class="group-field">
        </div>
        <ul id="col-2-list">
        <ul>
      </div>
      <div class="col">
        <div class="group-input">
          <p><img src="${nroot}/images/bg_group_name.png" class="bg-group-input"><span class="group-name"></span></p>
          <input type="text" class="group-field">
        </div>
        <ul id="col-3-list">
        </ul>
      </div>
      <div class="col">
        <div class="group-input">
          <p><img src="${nroot}/images/bg_group_name.png" class="bg-group-input"><span class="group-name"></span></p>
          <input type="text" class="group-field">
        </div>
        <ul id="col-4-list">
        </ul>
      </div>
      <div class="col">
        <div class="group-input">
          <p><img src="${nroot}/images/bg_group_name.png" class="bg-group-input"><span class="group-name"></span></p>
          <input type="text" class="group-field">
        </div>
        <ul id="col-5-list">
        </ul>
      </div>
      <div class="col">
        <div class="group-input">
          <p><img src="${nroot}/images/bg_group_name.png" class="bg-group-input"><span class="group-name"></span></p>
          <input type="text" class="group-field">
        </div>
        <ul id="col-6-list">
        </ul>
      </div>
      <div class="col">
        <div class="group-input">
          <p><img src="${nroot}/images/bg_group_name.png" class="bg-group-input"><span class="group-name"></span></p>
          <input type="text" class="group-field">
        </div>
        <ul id="col-7-list">
        </ul>
      </div>
      <div class="col">
        <div class="group-input">
          <p><img src="${nroot}/images/bg_group_name.png" class="bg-group-input"><span class="group-name"></span></p>
          <input type="text" class="group-field">
        </div>
        <ul id="col-8-list">
        </ul>
      </div>

      <div id="guide-bubble-l">
        <img src="${nroot}/images/bg_guide_bubble_l.png" class="min-bg">
        <ul id="bubble-list">
        <li class="on" id="bubble-normal">
          <h3><span></span></h3>
          <img src="" class="thumbnail1">
          <img src="" class="thumbnail2">
          <img src="" class="thumbnail3">
          <span id="ch-meta">
              <p><span>10 Episodes</span></p>
              <p><span>2 day ago</span></p>
              <p><span>by</span><span class="name">Vialo</span></p>
           </span>
          <p id="ch-brief" class="ellipsis multiline"><span>It is involved in range of activities to promot travel to Japan by the activities oversea as well as these tourists to promot their best activities.</span></p>
        </li>
        <li class="default" id="bubble-default">
          <h3><span></span></h3>
          <div class="default-thumbnail1">
            	<div class="default-no">No Episodes</div>
                <img src="${nroot}/images/guide_ep_default1.png" class="thumbnail1">
          </div>
          <img src="${nroot}/images/guide_ep_default2.png" class="thumbnail2">
          <img src="${nroot}/images/guide_ep_default2.png" class="thumbnail3">
          <span id="ch-meta">
              <p><span>0 Episodes</span></p>
              <p><span></span></p>
              <p><span>by</span><span class="name">Vialo</span></p>
           </span>
          <p id="ch-brief" class="ellipsis multiline"><span>It is involved in range of activities to promot travel to Japan by the activities oversea as well as these tourists to promot their best activities.</span></p>
	</li>
        </ul>
      </div>
      <div id="guide-tm">
        <div id="guide-trending">
          <h3><span>Trending Stories (</span><span id="trading-chNum">9</span><span>)</span></h3>
          <div id="trending-arrows" class="horizon-arrows">
            <div id="trending-up"></div>
            <div id="trending-down"></div>
          </div>
          <img src="${nroot}/images/bg_guide_trending.png" class="min-bg">
          <ul id="gt-list">
          </ul>    
        </div>
        <div id="guide-maylike"> 
          <h3><span>Recommended (</span><span id="recommend-chNum">9</span><span>)</span></h3>
          <div id="maylike-arrows" class="horizon-arrows">
            <div id="maylike-up"></div>
            <div id="maylike-down"></div>
          </div>
          <img src="${nroot}/images/bg_guide_trending.png" class="min-bg">
          <ul id="gr-list">
          </ul>
        </div>
     </div>
  </div>
</div>
<!-- Guide Layer End -->









<div id="home-layer" class="stage">
<div id="home-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
<div id="home-constrain">
    <div id="home-list">
    <div id="banner">
      <p class="l"></p>
      <p class="r"></p>
      <p id="closebtn"></p>
      <p class="home-title"></p> 
      <img src="${nroot}/thumbnail/banner.png">
    </div>
    <div id="homeleftbox">
      <h1>Trending Stories (9)</h1>
      <p id="home-arrow-up"></p>
      <p id="home-arrow-down"></p>
      <div id="trending-stories">
        <div class="trending-box"> 
          <p class="trending-box-tab"><span>Follow</span></p>
          <p class="trending-box-play"></p>
          <img src="" class="mainpic" height="100%" width="100%">
          <div class="trending-box-footer"> 
            <p class="trending-box-footer-l"></p> 
            <div class="trending-box-footer-c">
              <img src="" width="80px" height="45px"><img src="" width="80px" height="45px">
              <ul>
                <li class="trending-f">Sport Channel</li>
                <li>50 Episodes</li>
                <li>1 hour ago  | 100 Views</li>
              </ul>
            </div> 
            <p class="trending-box-footer-r"></p>
          </div>
        </div>
        <div class="trending-content-msg">
          <h2>Jeremy Lin's rise from ordinary guy to sensation</h2>
          <p class="msg-p-left">The most captivating strand of the Jeremy Lin mystique is that he came from nowhere, emerging overnight to become a star.</p>
          <p class="msg-p-right">by <b>Liz lisa</b></p>
          <img src="" class="msg-pic">
        </div>
      </div>
      <ul id="trending-stories-right">
      </ul>
      <hr>
      <div id="followings-wrap">
      </div>
    </div>
    <div id="homerightbox">
      <p id="home-type">Hottest</p>
      <ul id="home-menu">
        <li id="hottest" class="on"></li>
        <li id="featured"></li>
        <li id="recommended"></li>
      </ul>
      <ul id="home-billboard">
      </ul>
    </div>  
  </div>
</div>
</div>
<!-- Home Layer End --> 





<!-- Setting Layer Begin -->
<div id="settings-layer" class="stage">
  <div id="settings-wrap">
    <div class="settings-bar">
      <h1 class="settings-h1">Settings</h1>
    </div> 
    <div id="settings-content">    
      <p id="title">Settings</p>
      <ul id="left">
        <li>
          <div class="title">Email</div>
          <div class="data" id="settings-email"></div>
        </li>
        <li>
          <p class="title title-position">Name</p>
          <div class="data"> 
            <div class="input-wrap"> 
              <p class="input-l"></p>
              <input value="" class="input-c" id="settings-username">
              <p class="input-r"></p>
            </div>
            <!--div id="btn-settings-check" class="btn-gray">
              <p class="btn-gray-left"></p>
              <p class="btn-gray-middle">Check</p>
              <p class="btn-gray-right"></p>
            </div-->
            <!--p class="username-p-position">The username is composed of 8-16 English letters or numbers or low line.</span><br>
            This username has already been used, please type another one.</p-->
          </div>
        </li-->
        <li>
          <p class="title title-position">Password</p>
          <div class="data">
            <div id="btn-change-password" class="btn-gray"> 
              <p class="btn-gray-left"></p>
              <p class="btn-gray-middle">Change password</p>
              <p class="btn-gray-right"></p>
            </div>
          </div>
        </li>
        <li>
          <p class="title">About</p>
          <p class="data">
            <textarea class="about-textarea" id="settings-about"></textarea>
          </p>
        </li>
        <!--li>
          <p class="title title-position">Notification</p>
          <div class="data">
            <div id="btn-notification" class="btn-gray"> 
              <p class="btn-gray-left"></p>
              <p class="btn-gray-middle">Change notification settings</p>
              <p class="btn-gray-right"></p>
            </div>
          </div>
        </li-->
        <!--li>
          <div class="title">Facebook</div>
          <div class="data">
            <div class="settings-list">
              <p class="icon"><img src="${nroot}/images/icon_facebook.png"></p>
              <p class="settings-control"></p>
              <p>Connect to Facebook</p>
            </div>
            <div class="settings-list">
              <p class="icon"><img src="${nroot}/images/icon_facebook.png"></p>
              <p class="settings-control"></p>
              <p>Publish activity to your Facebook Timeline</p>
            </div>
          </div>
        </li-->
        <!--li>
          <p class="title">Youtube</p>
          <div class="data">
            <div class="settings-list">
              <p class="icon"><img src="${nroot}/images/icon_youtube.png"></p>
              <p class="settings-control"></p>
              <p>Connect to Youtube</p>
            </div>
          </div>
        </li-->
      </ul>
      <div id="right">
        <p class="title">Image</p>
        <div class="imagebox">
          <p class="l"></p>
          <div class="c">
            <div id="imagebox-upload-box">
        	<div id="imagebox-upload-wrap">
                <span id="per">50%</span>
                <div id="bar">
                  <p class="bar_l"></p>
                  <p class="bar_m"></p>
                  <p class="bar_r"></p>
	        </div>
              </div>
              <p class="imagebox-upload-wrap-p">Sample.jpg<br><br>00:52 remaining</p>
        	  <div id="btn-cancel-upload-image" class="btn-white">
                <p class="btn-white-left"></p>
                <p class="btn-white-middle">Cancel</p>
                <p class="btn-white-right"></p>
              </div>
            </div>
            <img src="${nroot}/thumbnail/curator_05.png">
          </div>
          <p class="r"></p>
          <div id="btn-upload" class="btn-gray">
            <p class="btn-gray-left"></p>
            <p class="btn-gray-middle">Upload a image</p>
            <p class="btn-gray-right"></p>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div id="btn-save-profile" class="btn-gray">
    <p class="btn-gray-left"></p>
    <p class="btn-gray-middle">Save profile</p>
    <p class="btn-gray-right"></p>
  </div>
</div>
<!-- Setting Layer End --> 

<!-- Setting Layer Change password Begin-->
<div id="settings-change-layer">
  <div id="settings-change-holder"> 
    <p class="l"></p>
    <div class="m"> 
      <p id="btn-change-close"></p>
      <div id="settings-panel-change"> 
        <img src="${nroot}/images/change_password.png" class="changepic">
        <ul>
          <li> 
            <p class="input-l"></p>
            <p class="input-m-set">
              <span class="hint">Old Password</span>
              <img src="${nroot}/images/icon_password.png" class="icon-set">
              <input class="textfield" type="password" id="settings-old-pw" value="" />
            </p>
            <p class="input-r"></p>
          </li>
          <li>
            <p class="input-l"></p>
            <p class="input-m-set">
              <span class="hint">New Password</span>
              <img src="${nroot}/images/icon_password.png" class="icon-set">
              <input class="textfield" type="password" id="settings-new-pw1" value="" />
            </p>
            <p class="input-r"></p>
          </li>
          <li>
            <p class="input-l"></p>
            <p class="input-m-set">
              <span class="hint">Repeat New Password</span>
              <img src="${nroot}/images/icon_password.png" class="icon-set">
              <input class="textfield" type="password" id="settings-new-pw2" value="" />
            </p>
            <p class="input-r"></p>
          </li>
          <li>
            <p class="passwor-incorrect">Old password is incorrect.<br>Two passwords don't match, please retype.</p>
          </li>
          <div id="btn-change-return-password" class="btn-gray">
            <p class="btn-gray-left"></p>
            <p class="btn-gray-middle">Change password</p>
            <p class="btn-gray-right"></p>
          </div>
          <div id="btn-cancel-password" class="btn-gray">
            <p class="btn-gray-left"></p>
            <p class="btn-gray-middle">Cancel</p>
            <p class="btn-gray-right"></p>
          </div>
        </ul>
      </div>
    </div>
    <p class="r"></p>
  </div>
</div>
<!-- Setting Layer Change password End --> 

<!-- Settings notification Begin-->
<div id="settings-notification-layer"  class="stage">
  <div id="settings-wrap">
    <div class="settings-bar">
      <h1 class="settings-h1">Settings</h1>
    </div>
    <div id="settings-content">
      <div id="title">Settings > Change notification</div>
    <div id="notification-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
        <div id="notification-constrain">
            <div id="notification-list"> 
              <ul id="notification">
                <li>
                  <div class="title">All</div>
                  <div class="data"> <span class="settings-control"></span>
                    <p>Any emails</p>
                  </div>
                </li>
                <p class="shadow-h"></p>
                <li><b>We will send you email of channel updates weekly</b></li>
                <li>
                  <div class="title">Treanding stories</div>
                  <div class="data">
                    <div class="settings-list">
                      <p class="settings-control"></p>
                      <p>When a new episode is added to "Trending Stories"</p>
                    </div>
                    <div class="settings-list">
                      <p class="settings-control"></p>
                      <p>When a new channel is added to “Trending Stories”</p>
                    </div>
                  </div>
                </li>
                <li>
                  <div class="title">Followings</div>
                  <div class="data">
                    <div class="settings-list">
                      <p class="settings-control"></p>
                      <p>When a new channel is added to your followings</p>
                    </div>
                  </div>
                </li>
                <p class="shadow-h"></p>
                <li>
                  <div class="title">Followers</div>
                  <div class="data">
                    <div class="settings-list">
                      <p class="settings-control"></p>
                      <p>When a new person follows your channel</p>
                    </div>
                  </div>
                </li>
                <li>
                  <div class="title">Favorite</div>
                  <div class="data">
                    <div class="settings-list">
                      <p class="settings-control"></p>
                      <p>When someone favorite episode in your channel</p>
                    </div>
                  </div>
                </li>
                <li>
                  <div class="title">Frequency</div>
                  <div class="data">
                    <div class="settings-list h40">
                      <p>How often you receive emails about above information:</p>
                      <div class="settings-radio">
                        <p class="btn-radio on"></p>
                        <p class="radio-p">Once Daily</p>
                      </div>
                      <div class="settings-radio">
                        <p class="btn-radio"></p>
                        <p class="radio-p">Once Weekly</p>
                      </div>
                    </div>
                  </div>
                </li>
                <p class="shadow-h"></p>
                <li>
                  <div class="title">Statistics</div>
                  <div class="data">
                    <div class="settings-list">
                      <p class="settings-control"></p>
                      <p>Emails summarizimg your weekly activity</p>
                    </div>
                  </div>
                </li>
                <li>
                  <div class="title">News</div>
                  <div class="data">
                    <div class="settings-list">
                      <p class="settings-control"></p>
                      <p>Occasional 9x9 news and updates</p>
                    </div>
                  </div>
                </li>
                <p class="shadow-h"></p>       
              </ul>
            <div id="notification-btn">
                <div class="notification-wrap-btn">
                  <div id="btn-save-notification" class="btn-gray">
                    <p class="btn-gray-left"></p>
                    <p class="btn-gray-middle">Save</p>
                    <p class="btn-gray-right"></p>
                  </div>
                  <div id="btn-cancel-notification" class="btn-gray">
                    <p class="btn-gray-left"></p>
                    <p class="btn-gray-middle">Cancel</p>
                    <p class="btn-gray-right"></p>
                  </div>
                </div>
            </div>
          </div>
        </div>
	</div>
	</div>
</div>
<!-- Settings notification End -->




<!-- signin Begin -->
<div id="signin-layer" class="stage">
  <div id="signin-holder"> 
    <p class="l"></p>
    <div class="m"> 
      <p id="btn-signin-close"></p>
      <div id="signin-panel-signin"> 
        <img src="${nroot}/images/sign_in.png" class="signinpic">
        <ul>
          <li> 
            <p class="input-l"></p> 
            <p class="input-m">
              <img src="${nroot}/images/icon_email.png" class="icon-set">
              <input class="textfield" type="text" id="return-email" value="E-mail" />
            </p> 
            <p class="input-r"></p>
          </li>
          <li>
            <p class="input-l"></p>
            <p class="input-m">
              <span id="return-password-hint" class="hint">Password</span>
              <img src="${nroot}/images/icon_password.png" class="icon-set">
              <input class="textfield" type="password" id="return-password" value="" />
            </p>
            <p class="input-r"></p>
          </li>
          <li>
            <div id="btn-home-sign-in" class="btn-white">
              <p class="btn-white-left"></p> 
              <p class="btn-white-middle">Sign In</p> 
              <p class="btn-white-right"></p>
            </div>
          </li>
          <li class="forgot">
            <p><a href="#" class="under">Forgot password?</a></p>
          </li>
          <li class="tail">
            <p class="signin-email-error">Email and password do not match, please try again.</p>
            <div id="btn-home-sign-in-fb" class="btn-fb">
              <p class="btn-fb-left"></p>
              <p class="btn-fb-middle">Sign in with Facebook</p>
              <p class="btn-fb-right"></p>
            </div>
          </li>
        </ul>
      </div>
      <div id="signin-panel-signup">
        <img src="${nroot}/images/sign_up.png" class="signuppic">
        <ul>
          <li class="h82">
            <p class="input-l"></p>
            <p class="input-m">
              <img src="${nroot}/images/icon_user.png" class="icon-set">
              <input class="textfield" type="text" id="signup-name" value="Your Name" />
			</p>
            <p class="input-r"></p>
            <p class="create-p">This will be your name as other 9x9 users see you.</p>
            <p class="create-p-error">Only can use 8-­16 English letters, number, low line. This username has already been used, please type another one.</p>
          </li>
          <li> 
            <p class="input-l"></p>
            <p class="input-m">
              <img src="${nroot}/images/icon_email.png" class="icon-set">
              <input class="textfield" type="text" id="signup-email" value="E-mail" />
            </p>
            <p class="input-r"></p>
          </li>
          <li>
            <p class="input-l"></p>
            <p class="input-m">
              <span id="signup-password-hint" class="hint">Password</span>
              <img src="${nroot}/images/icon_password.png" class="icon-set">
              <input class="textfield" type="password" id="signup-password" value="" />
            </p>
            <p class="input-r"></p>
          </li>
          <li>
            <p class="input-l"></p>
            <p class="input-m">
              <span id="signup-password-hint2" class="hint">Repeat Password</span>
              <img src="${nroot}/images/icon_password.png" class="icon-set">
              <input class="textfield" type="password" id="signup-password2" value="" />
            </p>
            <p class="input-r"></p>
          </li>
          <li class="tail">
            <p class="signup-message-error">This email has been used.<br>Two passwords don't match, please retype.</p>
            <div class="signup-message">
              <p id="signup-checkbox"></p>             
              <p id="signup-checkbox-txt">I have read and accepted the <a href="#" class="under">User Agreement</a> and <a href="#" class="under">Privacy Policy</a></p>
            </div>
		    <div id="btn-home-create-account" class="btn-white">
              <p class="btn-white-left"></p>
              <p class="btn-white-middle">Create My Account</p>
              <p class="btn-white-right"></p>
            </div>	
          </li>
        </ul>
      </div>
    </div>
    <p class="r"></p> </div>
</div>
<!-- signin End --> 



<!-- Developer Layer Begin --> 
<div id="developer-layer" class="stage">
  <div class="tape"></div>                                 
    <ul id="developer-menu">
    </ul>
    <div id="developer-title">
      <h1></h1>
    </div>
    <div class="reco-shelf">
      <div id="developer-view"><img src=""></div>
    </div>
    <div id="developer-download">
      <div class="btn-gray">
        <p class="btn-gray-left"></p>
        <p class="btn-gray-middle"></p>
        <p class="btn-gray-right"></p>
      </div>
    </div>
    <div id="developer-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
    <div id="developer-constrain">
       <div id="developer-list">
         <div id="developer-content">
       </div>
    </div>
  </div>
</div>
<!-- Developer Layer End -->




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
    <p><span id="question">You will be sharing the Public section of your Guide with your Facebook friends. Continue?</span></p>
    <ul class="action-list">
      <li><p class="btn" id="btn-yesno-yes"><span id="qyes">Yes</span></p></li>
      <li><p class="btn" id="btn-yesno-no"><span id="qno">No</span></p></li>
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

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><!DOCTYPE html>
<html itemscope itemtype="http://schema.org/">
<head>
<meta charset="UTF-8" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="root" value="http://9x9ui.s3.amazonaws.com/9x9playerV68a"/>
<c:set var="nroot" value="http://9x9ui.s3.amazonaws.com/mock40"/>

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

<script type="text/javascript" charset="utf-8" src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js"></script>
<script type="text/javascript" charset="utf-8" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js"></script>

<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/all.js"></script>
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/scripts/swfobject.js"></script>
<script type="text/javascript" charset="utf-8" src="${root}/javascripts/jquery.swfobject.1-1-1.min.js"></script>
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/scripts/flowplayer-3.2.4.min.js"></script>
<script type="text/javascript" charset="utf-8" src="${nroot}/javascripts/jquery.ellipsis.js"></script>
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/swfupload.js"></script>

<c:if test="${js == \"\"}">
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/mogwai11.js"></script>
</c:if>
<c:if test="${js != \"\"}">
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/${js}.js"></script>
</c:if>

<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/vertical.slider.V3.js"></script>
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/vertical.slider.HOME.js"></script>
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/jquery.mousewheel.3.0.6.js"></script>
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

<style>
.progress-titlecard {
	width: 5%;
	height:8px;
	background: #666;
	position: absolute;
	top: 0;
	left: 0;
	z-index: 32;
}
</style>

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
<!-- Curator Bubble Begin -->

<div id="curator-bubble">
  <ul id="icon-social">
    <li id="twitter"></li>
    <li id="facebook"></li>
    <li id="blogger"></li>
    <li id="google"></li>
  </ul>
  <div id="curator-info">
    <p id="curator-thumb"><img src="thumbnail/profile_default50.png"></p>
    <p id="curator-name-bubble"></p>
    <p id="curator-intro" class="ellipsis multiline"><span></span></p>
  </div>
  <div id="top-ch-name">My Top Channel: <p></p></div>
  <div id="curator-ch-meta">
    <p></p>
  </div>
  <div class="thumb">
      <p class="icon-pl"></p>
      <img src="" class="thumb1"><img src="" class="thumb2"><img src="" class="thumb3"> 
  	  <p class="pl-sign"><span></span></p>
  </div>
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
        <li data-doc="v-contest">Curation Contest</li>
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
      <p id="played"></p>
      <ul id="titlecards"></ul>
      <ul id="sub-ep"></ul>
    </div>
    <ul>
      <li id="btn-play"></li>
      <li id="btn-pause"></li>
      <li id="volume-control">
        <p id="btn-volume" class="loud"></p>
        <p id="volume-bar"></p>
        <p id="btn-dragger"></p>
      </li>
      <li class="left-edge"></li>
      <li id="btn-expand"></li>
      <li id="btn-shrink"></li>
      <li id="btn-share">
        <span class="gray-manage-tip-m"> 
          <span class="gray-tip-top"></span> 
          <span class="gray-tip-content-m"> 
            <span class="gray-tip-left-m"></span>
            <span class="input-l-g"></span>
            <span class="input-m-g">
              <input name="" class="textfield" type="text" value="" id="share-url">
            <!--span class="btn-share-copy"></span-->
          </span>
          <span class="input-r-g"></span>
          <span class="btn-share-mail"></span>
          <span class="btn-share-fb">
            <!--a href="http://www.facebook.com/share.php?u=http://www.9x9.tv" onclick="return fbs_click()" hidefocus="true" target="_blank"></a-->
          </span>
          <span class="gray-tip-right-m"></span> </span>
        </span> 
      </li>
      <li id="btn-favorite">
        <span class="gray-manage-tip-m"> 
          <span class="gray-tip-top"></span> 
          <span class="gray-tip-content-m"> 
            <span class="gray-tip-left-m"></span>
              <span class="favorite-content">Click to add this to your Favorites</span>
            <span class="gray-tip-right-m"></span>
          </span>
        </span> 
      </li>
      <li class="right edge fb"></li>
      <li id="btn-like">
        <div id="fb-like-container">
        </div>
      </li>
      <li id="play-time">
        <span id="played-length">0:14</span><span class="divider">/</span><span id="total-length">3:25</span>
      </li>
      <li class="right-edge"></li>
    </ul>
  </div>
</div>
<!-- Video Layer End -->

<!-- Player Layer Begin -->
<div id="player-layer" class="stage">
  <div id="player-sidebar">
    <p id="player-sidebar-shadow"></p>
    <div id="pl-switcher">
      <div id="popmessage-player-list">
          <div id="popmessage-player-list-holder">
            <p class="popmessage-left"></p>
            <p class="popmessage-middle">Added to <span class="btn-popmessage-guide">your Guide</span>.</p>
            <p class="popmessage-right"></p>
          </div>
      </div>
      <ul id="pl-menu">
        <li id="trending" class="on">
        	<span class="manage-tip">
                    <span class="tip-top"></span>
                    <span class="tip-content">
                        <span class="tip-left"></span>
                        <span class="tip-text">Trending</span>
                        <span class="tip-right"></span> 
                    </span>  
            </span>
        </li>
        <li id="youmaylike">
        	<span class="manage-tip">
                    <span class="tip-top"></span>
                    <span class="tip-content">
                        <span class="tip-left"></span>
                        <span class="tip-text">You May Like</span>
                        <span class="tip-right"></span> 
                    </span>  
            </span>
        </li>
        <li id="myfollow1">
        	<span class="manage-tip">
                    <span class="tip-top"></span>
                    <span class="tip-content">
                        <span class="tip-left"></span>
                        <span class="tip-text">I'm Following (Sorted by updated time)</span>
                        <span class="tip-right"></span> 
                    </span>  
            </span>
        </li>
        <li id="myfollow2">
       		 <span class="manage-tip">
                    <span class="tip-top"></span>
                    <span class="tip-content">
                        <span class="tip-left"></span>
                        <span class="tip-text">I'm Following (Sorted by channel number)</span>
                        <span class="tip-right"></span> 
                    </span>  
            </span>
        </li>
      </ul>
      <p id="pl-type">Trending Stories</p>
      <p id="pl-note">(Sorted by updated time)</p>
    </div>
    <div id="pl-slider" class="slider-wrap">
      <div class="slider-vertical"></div>
    </div>
    <div id="pl-constrain">
      <ul id="pl-list">
      </ul>
    </div>
    <div id="pl-constrain-new">
        <p id="pl-constrain-new-top">Sign in with 9x9.tv and follow the channels you like. Share your favorite channels and a lot more.</p>
        <div id="pl-constrain-new-holder">
            <img src="images/sign_in.png" class="signinpic">
            <div>
                <p class="input-l-g"></p>
                <p class="input-m-g"> 
                    <img src="${nroot}/images/icon_email.png" class="icon-set">
                    <input class="textfield" type="text" id="pl-return-email" value="E-mail" />
                </p>
                <p class="input-r-g"></p>
            </div>
            <div>
                <p class="input-l-g"></p>
                <p class="input-m-g">
                    <img src="${nroot}/images/icon_password.png" class="icon-set">
                    <input class="textfield" type="text" id="pl-return-password" value="Password" />
                </p>
                <p class="input-r-g"></p>
            </div>
            <div id="btn-playback-sign-in" class="btn-white">
                <p class="btn-white-left"></p>
                <p class="btn-white-middle">Sign In</p>
                <p class="btn-white-right"></p>
            </div>
            <div id="btn-playback-forgot-password">
                Forgot password?
            </div>
            <p class="error">
                Email and password do not match, please try again.
            </p>
            <div id="btn-playback-sign-in-fb" class="btn-fb">
                <p class="btn-fb-left"></p>
                <p class="btn-fb-middle">Sign in with Facebook</p>
                <p class="btn-fb-right"></p>
            </div>
	</div>
        <p id="pl-constrain-new-bottom">Do not have an account?<span>Sign up</span></p>
    </div>
    <div id="pl-constrain-return">
        <p><span>Cunnie Pan!</span></p>
        <br/>
        <p>Follow channels you like and watch them from your Guide.</p>
        <br/>
        <p><span class="btn-playback-tutorial">Show me how.</span></p>
    </div>
  </div>
  <div id="player-holder">
    <div id="player-ch-info">
      <p id="ep-title">Mountain Biker gets taken out by BUCK - CRAZY Footage</p>
      <div id="player-ep-source">
          <p id="ch-source">&#8212; <span>Mountain Biker gets taken out by BUCK Footage Part 1</span></p>
          <p id="curator-source">by <span>Moutain Biker's News</span></p>
          <p id="video-source">from <span>YouTube</span></p>
      </div>
      <div id="popmessage-player-info" style="display: none"><p class="popmessage-left"></p><p class="popmessage-middle"></p><p class="popmessage-right"></p></div>
    </div>
    <p id="video-placeholder"></p>
    <div id="player-ep-bar">
      <p id="btn-ep-left"></p>
      <p id="btn-ep-right"></p>
      <p id="player-ep-meta"> <span class="ep-title">Mountain Biker gets taken out by BUCK - CRAZY Footage</span> </p>
      <div id="player-ep-constrain">
        <ul id="player-ep-list">
        </ul>
      </div>
    </div>
    <div id="curator-photo"><img src=""></div>
    <div id="curator-title"> <span>Curator:</span>
      <p id="curator-name">Moutain Biker</p>
    </div>
    <p id="curator-description" class="ellipsis multiline"><span></span></p>
    <div id="flipr">
      <p id="flipr-playpause"></p>
      <p id="flipr-arrowup"></p>
      <p id="flipr-arrowdown"></p>
      <p id="flipr-arrowleft">
      	<span class="gray-manage-tip-m-b"> 
          <span class="gray-tip-content-m-b"> 
            <span class="gray-tip-left-m-b"></span>
            <span class="arrow-hint">Double-click for the next episode.</span>
            <span class="gray-tip-right-m-b"></span> 
          </span>
          <span class="gray-tip-bottom"></span> 
	</span>
      </p>
      <p id="flipr-arrowright">
      	<span class="gray-manage-tip-m-b"> 
          <span class="gray-tip-content-m-b"> 
            <span class="gray-tip-left-m-b"></span>
            <span class="arrow-hint">Double-click for the prev episode.</span>
            <span class="gray-tip-right-m-b"></span> 
          </span>
          <span class="gray-tip-bottom"></span> 
        </span>
      </p>
    </div>
    <p id="btn-follow"><span>Follow this Channel</span></p>
  </div>
</div>
<!-- Player Layer End -->

<!-- Curator Layer Begin -->
<div id="curator-layer" class="stage">
  <div id="curator-sidebar">
    <p id="curator-sidebar-shadow"></p>
    <p id="curator-profile-photo"><img src=""></p>
    <h2 id="curator-profile-name"></h2>
    <div id="curator-paragraphs">
      <p id="curator-declaration" class="ellipsis multiline"></p>
    </div>
    <div id="curator-page"><b>My Page's URL:</b><br><span id="curator-url"></span><br><span class="link">Create customized short URL</span></div>
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
  <div id="curator-main">
    <ul id="curator-tabs">
      <li class="left on" id="channel">
        <span id="btn-manage">
          <span class="manage-tip">
            <span class="tip-top"></span>
            <span class="tip-content">
              <span class="tip-left"></span>
              <span class="tool-text">Manage your channels</span>
              <span class="tip-right"></span>
            </span>
          </span>
        </span>
        <div>
          <p><span class="name"></span>'s Channels<span id="curator-ch-num" class="number">(0)</span></p>
        </div>
      </li>
      <li class="right" id="following">
        <div>
          <p><span class="name"></span>'s Followings<span id="curator-fol-num" class="number">(0)</span></p>
        </div>
      </li>
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
          <p class="channel-title"></p>
          <p class="btn-action"><span>Follow</span></p>
          <p class="icon-pl"></p>
          <img src="" class="thumb1">
          <img src="" class="thumb2">
          <img src="" class="thumb3">
          <p class="followed-curator-photo"><img src=""></p>
          <div class="channel-meta">
            <p>30 Episodes<span class="divider">|</span>10 minutes ago</p>
            <p class="next-update">Next Update: Tomorrow</p>
          </div>
          <p class="followed-curator">by<span></span></p>
        </li>
        <li>
          <p class="channel-title"></p>
          <p class="btn-action"><span>Follow</span></p>
          <p class="icon-pl"></p>
          <img src="" class="thumb1">
          <img src="" class="thumb2">
          <img src="" class="thumb3">
          <p class="followed-curator-photo"><img src=""></p>
          <div class="channel-meta">
            <p>30 Episodes<span class="divider">|</span>10 minutes ago</p>
          </div>
          <p class="followed-curator">by<span>Cunnie Pan</span></p>
        </li>
        <li>
          <p class="channel-title"></p>
          <p class="btn-action"><span>Follow</span></p>
          <p class="icon-pl"></p>
          <img src="" class="thumb1">
          <img src="" class="thumb2">
          <img src="" class="thumb3">
          <p class="followed-curator-photo"><img src=""></p>
          <div class="channel-meta">
            <p>30 Episodes<span class="divider">|</span>1 day ago</p>
          </div>
          <p class="followed-curator">by<span>Cunnie Pan</span></p>
        </li>
        <li>
          <p class="channel-title"></p>
          <p class="btn-action"><span>Follow</span></p>
          <p class="icon-pl"></p>
          <img src="" class="thumb1">
          <img src="" class="thumb2">
          <img src="" class="thumb3">
          <p class="followed-curator-photo"><img src=""></p>
          <div class="channel-meta">
            <p>30 Episodes<span class="divider">|</span>1 day ago</p>
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
  <h2><span id="guide-imfollowing-banner">I'm Following</span> (<span id="chNum">20</span>/72)</h2>
  <div id="guide-add">
    <div class="btn-gray">
      <p class="btn-gray-left"></p>
      <p class="btn-gray-middle">Add your own</p>
      <p class="btn-gray-right"></p>
    </div>
  </div>
  <div id="guide-bubble">
    <img src="${nroot}/images/bg_guide_bubble1.png" id="bg1" class="bg">
    <img src="${nroot}/images/bg_guide_bubble2.png" id="bg2" class="bg">
    <img src="${nroot}/images/bg_guide_bubble3.png" id="bg3" class="bg">
    <h3><span></span></h3>
    <img src="" class="thumbnail1">
    <img src="" class="thumbnail2">
    <img src="" class="thumbnail3">
    <div id="ch-meta">
      <p><span id="ch-meta-count"></span></p>
      <p><span id="ch-meta-ago"></span></p>
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
          <div id="ch-meta">
              <p><span>10 Episodes</span></p>
              <p><span>2 day ago</span></p>
              <p><span>by</span><span class="name"></span></p>
              <p id="ch-brief" class="ellipsis multiline"><span></span></p>
           </div>
        </li>
        <li class="default" id="bubble-default">
          <h3><span></span></h3>
          <div class="default-thumbnail1">
            	<div class="default-no">No Episodes</div>
                <img src="${nroot}/images/guide_ep_default1.png" class="thumbnail1">
          </div>
          <img src="${nroot}/images/guide_ep_default2.png" class="thumbnail2">
          <img src="${nroot}/images/guide_ep_default2.png" class="thumbnail3">
          <div id="ch-meta">
              <p><span>0 Episodes</span></p>
              <p><span></span></p>
              <p><span>by</span><span class="name"></span></p>
              <p id="ch-brief" class="ellipsis multiline"><span></span></p>
          </div>
	</li>
        </ul>
      </div>
      <div id="guide-tm">
        <div id="guide-trending">
          <h3><span><span id="guide-trending-banner">Trending Stories</span> (</span><span id="trading-chNum">9</span><span>)</span></h3>
          <div id="trending-arrows" class="horizon-arrows">
            <div id="trending-up"></div>
            <div id="trending-down"></div>
          </div>
          <img src="${nroot}/images/bg_guide_trending.png" class="min-bg">
          <ul id="gt-list">
          </ul>    
        </div>
        <div id="guide-maylike"> 
          <h3><span><span id="guide-recommended-banner">Recommended</span> (</span><span id="recommend-chNum">9</span><span>)</span></h3>
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

<!-- Add Your Own Layer Begin -->
<div id="guide-add-layer">
  <div id="guide-add-holder">
  	<div id="guide-add-left"></div>
	<div id="guide-add-mid">
    	<p id="guide-add-close"></p> 
		<img class="guide-add-title" src="${nroot}/images/guide_add_title.png">
        <p class="msg">Enter Toutube Channel or Playlist URL:</p>
        <p class="input-l-g"></p>
        <p class="input-m-g">
        	<img class="icon-set" src="${nroot}/images/icon_youtube_gray.png">
        	<input name="" class="textfield" type="text" value="http://www.youtube.com/user/" id="guide-add-url">
        </p>
        <p class="input-r-g"></p>
        <p class="note">We don't accept URLs for single videos.</p>
        <div id="guide-add-btn-holder">
            <div class="btn-white" id="btn-guide-add">
                <p class="btn-white-left"></p>
                <p class="btn-white-middle">Add this channel</p>
                <p class="btn-white-right"></p>
            </div> 
        </div>
    </div>
    <div id="guide-add-right"></div>
  </div>
</div> 
<!-- Add Your Own Layer End --> 









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
      <h1><span id="home-trending-banner">Trending Stories</span> (9)</h1>
      <div id="trending-stories">
        <div id="home-arrows" class="horizon-arrows">
          <div id="home-up"></div>
          <div id="home-down"></div>
        </div>
        <div class="trending-box"> 
          <p class="trending-box-tab"><span>Follow</span></p>
          <p class="trending-box-play"></p>
          <img src="" class="mainpic" height="100%" width="100%">
        </div>
        <div id="trending-box-left"></div>
        <div id="trending-box-mid">
          <div class="trending-box-footer"> 
            <img src=""><img src="">
            <ul>
              <li><p class="trending-f"><span>Channel Name</span></p></li>
              <li id="trending-f-episodes">50 Episodes</li>
              <li id="trending-f-ago-views">1 hour ago  | 100 Views</li>
            </ul>
          </div>
          <div class="trending-content-msg">
            <h2>Episode name</h2>
            <p class="msg-p-left ellipsis multiline">Channel Description</p>
            <p class="msg-p-right">by <b>Liz lisa</b></p>
          </div>
        </div>
        <div id="trending-box-right"></div>
      </div>
      <div id="trending-stories-list">
        <ul id="trending-stories-right">
        </ul>
      </div>
      <hr>
      <div id="followings-wrap">
        <h1>My Followings (0/72)</h1>
        <ul id="followings-box">
        </ul>
      </div>
    </div>
    <div id="homerightbox">
      <p id="home-type">Hottest</p>
      <ul id="home-menu">
        <li id="hottest" class="on">
          <span class="manage-tip">
            <span class="tip-top"></span>
            <span class="tip-content"> 
              <span class="tip-left"></span>
              <span class="tool-text">Hottest</span>
              <span class="tip-right"></span> 
            </span>  
          </span>
        </li>
        <li id="featured">
          <span class="manage-tip">
            <span class="tip-top"></span>
            <span class="tip-content"> 
              <span class="tip-left"></span>
              <span class="tool-text">Featured</span>
              <span class="tip-right"></span> 
            </span>  
          </span>
        </li>
        <li id="recommended">
          <span class="manage-tip">
            <span class="tip-top"></span>
            <span class="tip-content"> 
              <span class="tip-left"></span>
              <span class="tool-text">Recommended</span>
              <span class="tip-right"></span> 
            </span>  
          </span>
        </li>
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
          </div>
        </li>
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
            <p class="textarea">
              <span><textarea class="about-textarea" id="settings-about"></textarea></span>
            </p>
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
              <div id="imagebox-upload-wrap"> <span id="per">50%</span>
                <div id="bar">
                  <p class="bar_l"></p>
                  <p class="bar_m"></p>
                  <p class="bar_r"></p>
                </div>
              </div>              
              <p class="imagebox-upload-wrap-p">Sample.jpg<br>
                <br>
                00:52 remaining</p>              
              <div id="btn-cancel-upload-image" class="btn-white">
                <p class="btn-white-left"></p>
                <p class="btn-white-middle">Cancel</p>
                <p class="btn-white-right"></p>
              </div>           
            </div>           
                <div class="profile181" style="overflow:hidden;">
                	<img src="">
                </div>            
            </div>
          <p class="r"></p>
          <div id="btn-upload-wrapper">
              <div id="btn-upload" class="btn-gray">
                <p class="btn-gray-left"></p>
                <p class="btn-gray-middle">Upload a image</p>
                <p class="btn-gray-right"></p>
              </div>
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
      <div id="settings-panel-change"> <img src="${nroot}/images/change_password.png" class="changepic">
        <ul>
          <li>
            <p class="input-l-g"></p>
            <p class="input-m-g"> <span class="hint">Old Password</span> <img src="${nroot}/images/icon_password.png" class="icon-set">
              <input class="textfield" type="password" id="settings-old-pw" value="" />
            </p>
            <p class="input-r-g"></p>
          </li>
          <li>
            <p class="input-l-g"></p>
            <p class="input-m-g"> <span class="hint">New Password</span> <img src="${nroot}/images/icon_password.png" class="icon-set">
              <input class="textfield" type="password" id="settings-new-pw1" value="" />
            </p>
            <p class="input-r-g"></p>
          </li>
          <li>
            <p class="input-l-g"></p>
            <p class="input-m-g"> <span class="hint">Repeat New Password</span> <img src="${nroot}/images/icon_password.png" class="icon-set">
              <input class="textfield" type="password" id="settings-new-pw2" value="" />
            </p>
            <p class="input-r-g"></p>
          </li>
          <li>
            <p class="passwor-incorrect"></p>
          </li>
          <div id="btn-change-return-password" class="btn-white">
            <p class="btn-white-left"></p>
            <p class="btn-white-middle">Change password</p>
            <p class="btn-white-right"></p>
          </div>
          <div id="btn-cancel-password" class="btn-white">
            <p class="btn-white-left"></p>
            <p class="btn-white-middle">Cancel</p>
            <p class="btn-white-right"></p>
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




<!-- Sign In Begin--> 
<div id="signin-layer" class="stage">
  <div id="signin-holder">
    <p class="l"></p>
    <div class="m">
      <p id="btn-signin-close"></p>
      <div id="signin-panel-signin"> <img src="${nroot}/images/sign_in.png" class="signinpic">
        <ul>
          <li>
            <p class="input-l-g"></p>
            <p class="input-m-g"> <img src="${nroot}/images/icon_email.png" class="icon-set">
              <input class="textfield" type="text" id="return-email" value="E-mail" />
            </p>
            <p class="input-r-g"></p>
          </li>
          <li>
            <p class="input-l-g"></p>
            <p class="input-m-g"> <span id="return-password-hint" class="hint">Password</span> <img src="${nroot}/images/icon_password.png" class="icon-set">
              <input class="textfield" type="password" id="return-password" value="" />
            </p>
            <p class="input-r-g"></p>
          </li>
          <li>
            <div id="btn-home-sign-in" class="btn-white">
              <p class="btn-white-left"></p>
              <p class="btn-white-middle">Sign In</p>
              <p class="btn-white-right"></p>
            </div>
          </li>
          <li class="tail">
            <div class="forgot">
            	<p id="btn-forgot-password">Forgot password?</p>
            </div>
            <p class="signin-email-error">Email and password do not match, please try again.</p>
            <div id="btn-home-sign-in-fb" class="btn-fb">
              <p class="btn-fb-left"></p>
              <p class="btn-fb-middle">Sign in with Facebook</p>
              <p class="btn-fb-right"></p>
            </div>
          </li>
        </ul>
      </div>
      <div id="signin-panel-signup"> <img src="${nroot}/images/sign_up.png" class="signuppic">
        <ul>
          <li class="h82">
            <p class="input-l-g"></p>
            <p class="input-m-g"> <img src="${nroot}/images/icon_user.png" class="icon-set">
              <input class="textfield" type="text" id="signup-name" value="User Name" />
            </p>
            <p class="input-r-g"></p>
            <p class="create-p note">This will be your name as other 9x9 users see you.</p>
            <p class="create-p-error"></p>
          </li>
          <li>
            <p class="input-l-g"></p>
            <p class="input-m-g"> <img src="${nroot}/images/icon_email.png" class="icon-set">
              <input class="textfield" type="text" id="signup-email" value="E-mail" />
            </p>
            <p class="input-r-g"></p>
          </li>
          <li>
            <p class="input-l-g"></p>
            <p class="input-m-g"> <span id="signup-password-hint" class="hint">Password</span> <img src="${nroot}/images/icon_password.png" class="icon-set">
              <input class="textfield" type="password" id="signup-password" value="" />
            </p>
            <p class="input-r-g"></p>
          </li>
          <li>
            <p class="input-l-g"></p>
            <p class="input-m-g"> <span id="signup-password-hint2" class="hint">Repeat Password</span> <img src="${nroot}/images/icon_password.png" class="icon-set">
              <input class="textfield" type="password" id="signup-password2" value="" />
            </p>
            <p class="input-r-g"></p>
          </li>
          <li class="tail">
            <p class="signup-message-error"></p>
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
    <p class="r"></p>
  </div>
</div>
<!-- Sign In End --> 


<!-- Forgot Password Begin-->
<div id="forgot-password-layer" class="stage">
  <div id="forgot-password-holder"> 
    <div class="l"></div>
    <div class="m">
      <p id="btn-forgot-password-close"></p>
      <img src="${nroot}/images/fogot_password.png" class="forgotpic">
      <div class="forgot-password-dada">
        <div class="input-outlay">
          <p class="input-l-g"></p>
          <p class="input-m-g"> <span class="hint"></span> <img src="${nroot}/images/icon_password.png" class="icon-set">
            <input class="textfield" type="email" id="forgot-pw" value="" />
          </p>
          <p class="input-r-g"></p>
         </div>
          <p class="msg note">Enter your email address and we'll send you instructions.</p>
          <p class="msg-error">The email you entered doesn't have an associated user account. Please try again.</p>
          <div id="btn-forgot-password-reset" class="btn-white">
            <p class="btn-white-left"></p>
            <p class="btn-white-middle">Reset</p>
            <p class="btn-white-right"></p>
          </div>
          <p class="back-to-sign">Back to sign in</p>
        </div>
        <div class="forgot-password-msg">
       	   <p class="msg">Success!<br>
                          You should receive an email shortly.<br>
                          If you still need assistance, please <a href="#">contact us.</a></p>
        </div>
    </div>
    <div class="r"></div>
  </div>
</div>
<!-- Forgot Password End-->


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


<!-- Email Share Layer Begin -->
<div id="email-share-layer" class="stage">
  <div id="email-share-holder">
  	<div id="email-share-left"></div>
	<div id="email-share-mid">
    	<p id="email-share-close"></p> 
        <img class="email-share-title" src="${nroot}/images/email_share_title.png">
        <p class="msg">To:</p>
        <p class="input-l-g"></p>
        <p class="input-m-g">
          <input name="" class="textfield" type="text" value="Enter email address" id="email-share-to">
        </p>
        <p class="input-r-g"></p>
        <p class="msg">Message:<span class="note">(150 characters maximum)</span></p>
        <p class="textarea-g"> 
          <span>
            <textarea class="textarea-g" id="email-share-message"></textarea>
          </span>
        </p>
        <p class="msg">Word Verification:</span></p>
        <p class="input-l-g"></p>
        <p class="input-m-g">
          <input name="" class="textfield" type="text" value="Enter word" id="email-share-captcha">
        </p>
        <p class="input-r-g"></p>
        <div id="email-share-captch">
           <div class="btn-white-m" id="btn-email-share-captch">
             <p class="btn-white-left-m"></p>
             <p class="btn-white-middle-m">
               <img class="email-share-captch-graph" src="">
               <span class="email-share-captch-refresh"></span>
             </p>
             <p class="btn-white-right-m"></p>
           </div>  
        </div>
        <div id="email-share-btn-holder">
          <div class="btn-white" id="btn-email-share-send">
            <p class="btn-white-left"></p>
            <p class="btn-white-middle">Send</p>
            <p class="btn-white-right"></p>
          </div> 
          <div class="btn-white" id="btn-email-share-cancel">
            <p class="btn-white-left"></p>
            <p class="btn-white-middle">Cancel</p>
            <p class="btn-white-right"></p>
          </div> 
        </div>
    </div>
    <div id="email-share-right"></div>
  </div>
</div> 
<!-- Email Share Layer End --> 

<!-- IE Teaser layer Begin -->
<div id="ie-teaser-layer">
  <div id="ie-teaser-holder"> 
    <p id="ie-teaser-title">
    	<img src="${nroot}/images/ie_teaser_icon.png" class="ie-teaser-icon">
        You are currently using Internet Explorer
    </p>
    <p id="ie-teaser-close"></p>
    <p class="ie-teaser-msg">To get the full 9x9.tv experience, please view in Chrome or Firefox.</p>
    <div class="footer">
	  <p class="ie-teaser-msg">To download Chrome or Firefox:</p>
      <ul id="ie-teaser-browser">
      	<li class="ie-teaser-chrome">
	    	<p class="btn-white-left-l"></p>
            <p class="btn-white-middle-l"><img src="${nroot}/images/ie_teaser_chrome.png" class="ie-teaser-logo">Google Chrome</p>
            <p class="btn-white-right-l"></p>
        </li>
        <li class="ie-teaser-firefox">
	        <p class="btn-white-left-l"></p>
    	    <p class="btn-white-middle-l"><img src="${nroot}/images/ie_teaser_firefox.png" class="ie-teaser-logo">Mozilla Firefox</p>
            <p class="btn-white-right-l"></p>
        </li>
      </ul>
      <div class="btn-white" id="btn-ie-teaser-ok">
                <p class="btn-white-left"></p>
                <p class="btn-white-middle">Continue to 9x9.tv</p>
                <p class="btn-white-right"></p>
      </div>
    </div>
  </div>
</div>
<!-- IE Teaser layer End --> 




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
    <img src="${nroot}/temporary/loading.gif">
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
    <img src="${nroot}/temporary/icon_error.png" id="icon-error">
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

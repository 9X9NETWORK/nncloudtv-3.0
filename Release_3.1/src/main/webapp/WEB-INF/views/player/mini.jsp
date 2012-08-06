<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<head>
<meta charset="UTF-8" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="root" value="http://9x9ui.s3.amazonaws.com/9x9miniV21"/>

<title>9x9.tv/tv</title>

<link rel="stylesheet" href="${root}/stylesheets/main.css" />


<!-- $Revision: 1726 $ -->

<script type="text/javascript" charset="utf-8" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>

<script type="text/javascript" charset="utf-8" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.8/jquery-ui.min.js"></script>
<script type="text/javascript" charset="utf-8" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.8/i18n/jquery-ui-i18n.min.js"></script>

<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/scripts/swfobject.js"></script>
<!--script type="text/javascript" charset="utf-8" src="${root}/javascripts/jquery.swfobject.1-1-1.min.js"></script-->
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/scripts/flowplayer-3.2.4.min.js"></script>
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/vertical.slider.V2.js"></script>
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/jquery.mousewheel.min.js"></script>
<script type="text/javascript" charset="utf-8" src="${root}/javascripts/jquery.ellipsis.js"></script>
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/soundmanager/soundmanager2.js"></script>

<!--
<c:if test="${js == \"\"}">
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/mini20.js"></script>
</c:if>
<c:if test="${js != \"\"}">
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/${js}.js"></script>
</c:if>
-->
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/mini20.js"></script>

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
soundManager.url = '/player/';
soundManager.useFlashBlock = false;
soundManager.onready(function()
  {
  log ('***************************************** SOUND MANAGER READY **********************************************\n');
  });
</script>

</head>
<body>

<div id="bg-layer"></div>

<div id="audio1" style="display:block;width:750px;height:30px;visibility:hidden;position:absolute;top:0;left:0" href=""></div>
<div id="audio2" style="display:block;width:750px;height:30px;visibility:hidden;position:absolute;top:0;left:0" href=""></div>
<div id="audio3" style="display:block;width:750px;height:30px;visibility:hidden;position:absolute;top:0;left:0" href=""></div>

<div id="video" style="position: absolute; z-Index: 2; height: 100%; width: 100%; background-color: black">
</div>

<div id="sync-layer"> 
  <div id="sync-holder"> 
    <p class="head"><span>Welcome to 9x9!</span></p> 
    <ul id="values"> 
      <li><span>Sign up to access thousands of curated channels</span></li> 
      <li><span>Create your very own personalized channel guide</span></li> 
      <li><span>Watch your favorite channels just like watching TV!</span></li> 
    </ul> 
    <p><span>It's free and easy!</span></p> 
    <p class="btn-hilite"><span>Sign in or Sign up now!</span></p> 
    <p><span>Use iPhone/iPad to remote control 9x9?<br>The name of this device is:</span></p> 
    <p class="device-name"><span>Google TV</span></p> 
  </div> 
</div> 

<div id="welcome-layer">
  <div id="welcome-holder">
    <p><span>Welcome, </span><span class="user-name">Jeff</span></p>
  </div>
</div>

<div id="msg-layer">
  <div id="msg-holder">
    <p><span></span></p>
  </div>
</div>

<div id="yesno-layer">
  <div id="yesno-holder">
    <p id="question"><span>Are you sure you want to remove</span><span id="removee"></span><span>from the user list?</span></p>
    <ul class="action-list">
      <li><p class="btn" id="btn-yesno-yes"><span>Yes</span></p></li>
      <li><p class="btn" id="btn-yesno-no"><span>No</span></p></li>
    </ul>
  </div>
</div>

<div id="confirm-layer"> 
  <div id="confirm-holder"> 
    <p class="head"><span>Sign up complete.<br>You may <strong>start flipping!</strong></span></p> 
    <p class="main"><span>Browse our <strong>Channel Store</strong>, add new channels to your personalized <strong>Channel Guide</strong>, and watch your favorite channels just like how you would on a TV!</span></p>
    <p class="btn-hilite" id="btn-start-add"><span>Start adding channels</span></p>
  </div>
</div> 

<div id="notice-layer">
  <div id="notice-holder">
    <p><span>Added to your guide successfully!</span></p>
  </div>
</div>

<div id="player-layer">
  <div id="osd-layer">
    <div id="osd-holder">
      <p id="ch-info"><span class="head">Channel: </span><span id="ch-title"></span></p>
      <p id="ep-info"><span class="head">Episode: </span><span id="ep-title"></span><span class="dash"> &#8212; </span><span id="ep-age"></span></p>
      <p id="player-hint"><span>Press <img src="${root}/images/btn_9x9.png" class="icon-btn-9x9"> or <img src="${root}/images/btn_m.png" class="icon-btn-m"><br>to go to Guide.</span></p> 
    </div>
  </div>
  <div id="player-holder">
    <div id="video-layer"></div>
  </div>
</div>

<div id="sg-layer">
  <div id="sg-holder">
    <div id="sg-header">
      <img src="${root}/images/logo.png" id="sg-logo">
      <p id="device"><span>Device:</span><span id="device-name">Not connected</span></p>
      <img src="${root}/images/icon_store.png" id="btn-store">
      <img src="${root}/images/icon_help.png" id="btn-help">
      <img src="${root}/images/icon_pc.png" id="btn-pc">
      <img src="${root}/images/icon_pref.png" id="btn-pref">
    </div>
    <div id="user">
      <p id="selected-user">
        <span id="user-name">No users</span><span class="tail">'s Channels</span><img src="${root}/images/icon_expand.png" class="icon-expand">
      </p>
      <ul id="user-list">
      </ul>
    </div>
    <div id="sg-grid" class="x9">
      <div id="sg-constrain">
        <div id="slider">
        </div>
      </div>
    </div>
    <div id="channel-info">
      <p class="section-title"><span>Current Channel:</span></p>
      <p id="channel-title"><span></span></p>
      <p id="channel-meta"> <span id="eps-number">Episodes: </span><br>
        <span id="updates">Updated: </span>
      </p>
      <p id="channel-description" class="ellipsis multiline"><span></span></p>
    </div>
    </div>
  </div>
</div>

<div id="volume-layer">
  <div id="volume-holder">
    <p id="volume-down"><img src="${root}/images/btn_volume_down.png" title="Volume Down"></p>
    <ul id="volume-bars"><li class="on"></li><li class="on"></li><li class="on"></li><li class="on"></li><li></li><li></li><li></li><li></li><li></li><li></li></ul>
    <p id="volume-up"><img src="${root}/images/btn_volume_up.png" title="Volume Up"></p>
  </div>
</div>

<div id="relaydiv" style="z-index: 1; position: absolute; top: 0px; left: 0px">
</div>

<div id="help-layer">
  <div id="help-holder">
    <p id="btn-help-close"><img src="${root}/images/btn_delete_off.png" class="off"><img src="${root}/images/btn_delete_on.png" class="on"></p>
    <ul id="help-tabs">
      <li id="p1" class="on"><span>Panel 1</span></li>
      <li id="p2"><span>Panel 2</span></li>
      <li id="p3"><span>Panel 3</span></li>
      <li id="p4"><span>Panel 4</span></li>
    </ul>
    <div id="p1-panel" class="input-panel">
      <div id="p1-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
      <div id="p1-content" class="constrain">
        <div id="p1-list" class="list">
        </div>
      </div>
    </div>
    <div id="p2-panel" class="input-panel">
      <div id="p2-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
      <div id="p2-content" class="constrain">
        <div id="p2-list" class="list">
        </div>
      </div>
    </div>
    <div id="p3-panel" class="input-panel">
      <div id="p3-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
      <div id="p3-content" class="constrain">
        <div id="p3-list" class="list">
        </div>
      </div>
    </div>
    <div id="p4-panel" class="input-panel">
      <div class="constrain">
        <p id="problem-input">
          <textarea class="textfield">Enter your problem</textarea> 
        </p>
        <p id="btn-report" class="btn"><span>Submit</span></p>
      </div>
    </div>
  </div>
</div>

<div id="store-layer">
  <div id="store-hint"><p><img src="${root}/images/btn_ok.png" class="icon-btn-ok"><span>to add this channel.</span></p><p><img src="${root}/images/btn_9x9.png" class="icon-btn-9x9"><span>or<img src="${root}/images/btn_m.png" class="icon-btn-m">to Guide.</span></p><p><img src="${root}/images/arrow_left.png" class="icon-btn-left"><img src="${root}/images/arrow_right.png" class="icon-btn-right"><span>to flip set.</span></p><p><img src="${root}/images/arrow_up.png" class="icon-btn-up"><img src="${root}/images/arrow_down.png" class="icon-btn-down"><span>to flip channels in the current set.</span></p></div>
  <div id="store-holder"> 
    <div id="store-header">
      <img src="${root}/images/logo.png" id="store-logo">
      <p id="store-title"><span>Channel Store</span></p>
    </div>
    <img src="${root}/images/arrow_up.png" id="ch-up">
    <img src="${root}/images/arrow_left.png" id="set-left">
    <img src="${root}/images/arrow_right.png" id="set-right">
    <div id="store-constrain">    
        <div id="open-set">
          <div id="ch4"></div>
          <div id="ch1" class="i1"><div id="ch1-video"><img src="${root}/thumb/01.jpg" class="thumbnail"></div></div>
          <div id="ch2" class="i2"><div id="ch2-video"><img src="${root}/thumb/02.jpg" class="thumbnail"></div></div>
          <div id="ch3" class="i3"><div id="ch3-video"><img src="${root}/thumb/08.jpg" class="thumbnail"></div></div>
          <p id="set-titles" class="open-txt"><span class="head">Set: </span><span id="set-name">Newest Channels</span></p>
          <p id="ch-titles" class="open-txt"><span id="ch-name">BBC News</span><span id="ch-order">(Ch 3 of 20)</span></p>
          <p id="open-description" class="open-txt ellipsis multiline"><span>Breaking news, sport, TV, radio and a whole lot more. The BBC informs, educates 
and entertains - wherever you are, whatever your age. The BBC's heritage and history since it was founded in 1922.</span></p>
          <p id="open-meta" class="open-txt"><span id="open-epi">30 episodes</span><span class="divider">|</span><span>Subscribers:</span><span id="open-subscriber">1234</span><span class="divider">|</span><span>Updated:</span><span id="open-age">Today</span></p>
        </div>
        <ul id="set-list">
        </ul>
    </div>
  </div>
</div>

<div id="signin-layer">
  <div id="signin-holder">
    <p id="btn-signin-close"><img src="${root}/images/btn_delete_off.png" class="off"><img src="${root}/images/btn_delete_on.png" class="on"></p>
    <ul id="signin-tabs">
      <li id="signin" class="on"><span>Returning Users</span></li>
      <li id="signup"><span>New Users</span></li>
    </ul>
    <div id="signin-panel" class="input-panel">
      <ul class="input-list">
        <li>
          <span>Your 9x9 ID:</span>
          <p class="signin-input">
            <input type="text" class="textfield" id="return-email">
          </p>
        </li>
        <li>
          <span>Password:</span>
          <p class="signin-input">
            <input type="password" class="textfield" id="signin-password">
          </p>
        </li>
        <li><p class="btn" id="btn-signin"><span>Sign in</span></p></li>
      </ul>
      <div id="entry-switcher">
        <p class="head"><span>Need help?</span></p>
        <p class="content"><span>Please visit 9x9.tv or email <a href="mailto:feedback@9x9cloud.tv">feedback@9x9cloud.tv</a></span></p>
      </div>
    </div>
    <div id="signup-panel" class="input-panel">
      <ul class="input-list">
        <li>
          <span>Email:</span>
          <p class="signin-input">
            <input type="text" class="textfield" id="signup-email">
          </p>
        </li>
        <li>
          <span>Password:</span><span class="hint">6-character minimum</span> 
          <p class="signin-input">
            <input type="password" class="textfield" id="signup-password">
          </p>
        </li>
        <li>
          <span>Retype Password:</span>
          <p class="signin-input">
            <input type="password" class="textfield" id="signup-password2">
          </p>
        </li>
        <li>
          <span>Name:</span>
          <p class="signin-input">
            <input type="text" class="textfield" id="signup-name">
          </p>
        </li>
        <li id="signup-gender">
          <p><span>Gender:</span></p>
          <p class="radio-item on"><img src="${root}/images/btn_radio_off.png" class="btn-radio-off"><img src="${root}/images/btn_radio_on.png" class="btn-radio-on"><span>Male</span></p>
          <p class="radio-item"><img src="${root}/images/btn_radio_off.png" class="btn-radio-off"><img src="${root}/images/btn_radio_on.png" class="btn-radio-on"><span>Female</span></p>
        </li>
        <li id="birth-input">
          <span>Birth Year:</span>
          <p class="signin-input">
            <input type="text" class="textfield" value="Example: 1985" id="signup-birthyear">
          </p>
        </li>
      </ul>
      <ul class="input-list-right"> 
        <li id="signup-language">
          <p><span>Display Language:</span></p>
          <p class="radio-item on"><img src="${root}/images/btn_radio_off.png" class="btn-radio-off"><img src="${root}/images/btn_radio_on.png" class="btn-radio-on"><span>English</span></p>
          <p class="radio-item"><img src="${root}/images/btn_radio_off.png" class="btn-radio-off"><img src="${root}/images/btn_radio_on.png" class="btn-radio-on"><span>中文</span></p>
        </li>
        <li id="signup-region">
          <p><span>Content Region:</span></p>
          <p class="radio-item on"><img src="${root}/images/btn_radio_off.png" class="btn-radio-off"><img src="${root}/images/btn_radio_on.png" class="btn-radio-on"><span>US</span></p>
          <p class="radio-item"><img src="${root}/images/btn_radio_off.png" class="btn-radio-off"><img src="${root}/images/btn_radio_on.png" class="btn-radio-on"><span>Chinese</span></p>
        </li>
        <li>
          <p class="term-text"><span>Clicking I accept means that you agree to the <strong id="btn-agreement">9x9 service agreement</strong> and <strong id="btn-legal">legal statement</strong>. You also agree to receive email from 9x9 with service updates, special offers, and survey invitations. You can unsubscribe at any time.</span></p>
        </li>
        <li><p class="btn" id="btn-signup"><span>I Accept</span></p></li>
        <li>
          <p class="help-txt"><span>Need help? Please visit 9x9.tv or email <a href="mailto:feedback@9x9cloud.tv">feedback@9x9cloud.tv</a></span></p>
        </li>
      </ul>
    </div>   
  </div>
</div>

<div id="doc-layer"> 
  <div id="doc-holder"> 
    <p id="btn-doc-close"><img src="${root}/images/btn_delete_off.png" class="off"><img src="${root}/images/btn_delete_on.png" class="on"></p> 
    <ul id="doc-tabs"> 
      <li id="agreement"><span>Agreement</span></li> 
      <li id="legal" class="on"><span>Legal</span></li> 
    </ul> 
    <div id="agreement-panel" class="input-panel"> 
      <div id="agreement-slider" class="slider-wrap"><div class="slider-vertical"></div></div> 
      <div id="agreement-content" class="constrain"> 
        <div id="agreement-list"> 
        </div> 
      </div> 
    </div> 
    <div id="legal-panel" class="input-panel"> 
      <div id="legal-slider" class="slider-wrap"><div class="slider-vertical"></div></div> 
      <div id="legal-content" class="constrain"> 
        <div id="legal-list"> 
        </div> 
      </div> 
    </div> 
  </div> 
</div>

<div id="tutorial-layer">
  <div id="tutorial-holder">
  </div>
</div>

<div id="settings-layer">
  <div id="settings-holder">
    <p id="btn-settings-close"><img src="${root}/images/btn_delete_off.png" class="off"><img src="${root}/images/btn_delete_on.png" class="on"></p>
    <ul id="settings-tabs">
      <li id="preload" class="on hilite"><p class="tab-hilite"></p><span>Preload</span></li>
      <li id="resolution"><p class="tab-hilite"></p><span>Resolution</span></li>
      <li id="acceleration"><p class="tab-hilite"></p><span>Acceleration</span></li>
      <li id="language"><p class="tab-hilite"></p><span>Language &amp; Region</span></li>
    </ul>
    
    <div id="preload-panel" class="input-panel">
      <ul class="input-list">
        <li>
          <p class="head"><span>I want to set 9x9's preloading capacity to:</span></p>
          <p class="option radio-item on" id="p1"><img src="${root}/images/btn_radio_off.png" class="btn-radio-off"><img src="${root}/images/btn_radio_on.png" class="btn-radio-on"><span>Off</span><span class="explanation">Turn off smooth channel flipping for faster buffering</span></p>
          <p class="option radio-item" id="p2"><img src="${root}/images/btn_radio_off.png" class="btn-radio-off"><img src="${root}/images/btn_radio_on.png" class="btn-radio-on"><span>On</span><span class="explanation">Turn on smooth channel flipping for faster browsing</span></p>
        </li>
      </ul>
    </div>

    <div id="resolution-panel" class="input-panel">
      <ul class="input-list">
        <li>
          <p class="head"><span>Set 9x9's default playback resolution at:</span></p>
          <p class="option radio-item on" id="r1"><img src="${root}/images/btn_radio_off.png" class="btn-radio-off"><img src="${root}/images/btn_radio_on.png" class="btn-radio-on"><span>1080p</span></p>
          <p class="option radio-item" id="r2"><img src="${root}/images/btn_radio_off.png" class="btn-radio-off"><img src="${root}/images/btn_radio_on.png" class="btn-radio-on"><span>720p</span></p>
          <p class="option radio-item" id="r3"><img src="${root}/images/btn_radio_off.png" class="btn-radio-off"><img src="${root}/images/btn_radio_on.png" class="btn-radio-on"><span>480p (Default)</span></p>
          <p class="option radio-item" id="r4"><img src="${root}/images/btn_radio_off.png" class="btn-radio-off"><img src="${root}/images/btn_radio_on.png" class="btn-radio-on"><span>360p</span></p>
        </li>
      </ul>
    </div>

    <div id="acceleration-panel" class="input-panel">
      <ul class="input-list">
        <li>
          <p class="head"><span>I want to set 9x9's hardware acceleration capacity to:</span></p>
          <p class="option radio-item on" id="a1"><img src="${root}/images/btn_radio_off.png" class="btn-radio-off"><img src="${root}/images/btn_radio_on.png" class="btn-radio-on"><span>Off</span><span class="explanation">Turn off hardware acceleration for more stable video playback. It requires a faster CPU, and may be jerky in video.</span></p>
          <p class="option radio-item" id="a2"><img src="${root}/images/btn_radio_off.png" class="btn-radio-off"><img src="${root}/images/btn_radio_on.png" class="btn-radio-on"><span>On</span><span class="explanation">Turn on hardware acceleration for smoother video playback in HD and big screen. It requires a supported GPU and driver, and may be unstable on some systems.</span></p>
        </li>
      </ul>
    </div>

    <div id="language-panel" class="input-panel">
      <ul class="input-list">
        <li>
          <p class="head"><span>Choose the language in which you want to view 9x9.tv. This will only change the interface.</span></p>
          <p class="option radio-item on" id="l1"><img src="${root}/images/btn_radio_off.png" class="btn-radio-off"><img src="${root}/images/btn_radio_on.png" class="btn-radio-on"><span>English</span></p>
          <p class="option radio-item" id="l2"><img src="${root}/images/btn_radio_off.png" class="btn-radio-off"><img src="${root}/images/btn_radio_on.png" class="btn-radio-on"><span>中文</span></p>
        </li>
        <li>
          <p class="head"><span>Choose which region's content you would like to watch. This will only change the video content.</span></p>
          <p class="option radio-item on" id="l3"><img src="${root}/images/btn_radio_off.png" class="btn-radio-off"><img src="${root}/images/btn_radio_on.png" class="btn-radio-on"><span>US</span></p>
          <p class="option radio-item" id="l4"><img src="${root}/images/btn_radio_off.png" class="btn-radio-off"><img src="${root}/images/btn_radio_on.png" class="btn-radio-on"><span>Chinese</span></p>
        </li>
      </ul>
    </div>
    
    <ul class="action-list">
      <li><p id="btn-settings-save" class="btn"><span>Save</span></p></li>
      <li><p id="btn-settings-cancel" class="btn"><span>Cancel</span></p></li>
    </ul>
    
  </div>
</div>

</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html itemscope itemtype="http://schema.org/">
<head>
<meta charset="UTF-8"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="root" value="http://9x9ui.s3.amazonaws.com/9x9playerV68a"/>
<c:set var="nroot" value="http://9x9ui.s3.amazonaws.com/9x9playerV104"/>

<!--
crawlVideoThumb: ${crawlVideoThumb}
crawlChannelTitle: ${crawlChannelTitle}
crawlSetTitle: ${crawlSetTitle}

crawlEpisodeTitle: ${crawlEpisodeTitle}
crawlEpThumb1: ${crawlEpThumb1}
crawlEpThumb2: ${crawlEpThumb2}
crawlEpThumb3: ${crawlEpThumb3}

crawlRecommendThumb1: ${crawlRecommendThumb1}
crawlRecommendTitle1: ${crawlRecommendTitle1}
crawlRecommendDesc1: ${crawlRecommendDesc1}
crawlRecommendCount1: ${crawlRecommendCount1}

crawlRecommendThumb2: ${crawlRecommendThumb2}
crawlRecommendTitle2: ${crawlRecommendTitle2}
crawlRecommendDesc2: ${crawlRecommendDesc2}
crawlRecommendCount2: ${crawlRecommendCount2}

crawlRecommendThumb3: ${crawlRecommendThumb3}
crawlRecommendTitle3: ${crawlRecommendTitle3}
crawlRecommendDesc3: ${crawlRecommendDesc3}
crawlRecommendCount3: ${crawlRecommendCount3}

crawlRecommendThumb4: ${crawlRecommendThumb4}
crawlRecommendTitle4: ${crawlRecommendTitle4}
crawlRecommendDesc4: ${crawlRecommendDesc4}
crawlRecommendCount4: ${crawlRecommendCount4}

crawlRecommendThumb5: ${crawlRecommendThumb5}
crawlRecommendTitle5: ${crawlRecommendTitle5}
crawlRecommendDesc5: ${crawlRecommendDesc5}
crawlRecommendCount5: ${crawlRecommendCount5}

crawlRecommendThumb6: ${crawlRecommendThumb6}
crawlRecommendTitle6: ${crawlRecommendTitle6}
crawlRecommendDesc6: ${crawlRecommendDesc6}
crawlRecommendCount6: ${crawlRecommendCount6}

crawlRecommendThumb6: ${crawlRecommendThumb6}
crawlRecommendTitle6: ${crawlRecommendTitle6}
crawlRecommendDesc6: ${crawlRecommendDesc6}
crawlRecommendCount6: ${crawlRecommendCount6}

crawlRecommendThumb7: ${crawlRecommendThumb7}
crawlRecommendTitle7: ${crawlRecommendTitle7}
crawlRecommendDesc7: ${crawlRecommendDesc7}
crawlRecommendCount7: ${crawlRecommendCount7}

crawlRecommendThumb8: ${crawlRecommendThumb8}
crawlRecommendTitle8: ${crawlRecommendTitle8}
crawlRecommendDesc8: ${crawlRecommendDesc8}
crawlRecommendCount8: ${crawlRecommendCount8}

crawlRecommendThumb9: ${crawlRecommendThumb9}
crawlRecommendTitle9: ${crawlRecommendTitle9}
crawlRecommendDesc9: ${crawlRecommendDesc9}
crawlRecommendCount9: ${crawlRecommendCount9}

-->

<!-- $Revision: 2612 $ -->

<!-- Google+ Sharing meta data -->
<meta itemprop="name" content="${fbName}">
<meta itemprop="description" content="${fbDescription}">
<meta itemprop="image" content="${fbImg}"><!-- Google+ requires thumbnail size at least 125px -->

<!-- FB Sharing meta data -->
<meta name="title" content="${fbName}"/>
<meta name="description" content="${fbDescription}"/>

<link rel="image_src" href="${fbImg}" />

<meta property="og:title" content="${fbName}"/>
<meta property="og:image" content="${fbImg}"/>
<meta property="og:description" content="${fbDescription}"/>
<meta property="og:url" content="${fbUrl}"/>

<script type="text/javascript">
var brandinfo = "${brandInfo}";
location.replace('${playerPromotionUrl}');
</script>

<link rel="stylesheet" href="${nroot}/stylesheets/main.css"/>
<link rel="stylesheet" href="http://9x9ui.s3.amazonaws.com/contest/contest.css"/>

<title>9x9.tv</title>

</head>

<body id="body" style="overflow: hidden; visibility: hidden">

<div id="blue" style="background: black; width: 100%; height: 100%; display: block; position: absolute; color: white"></div>

<div id="bg-layer"><img src="${nroot}/images/bg_body.png"></div>

<div id="audio1" style="display:block;width:750px;height:30px;visibility:hidden" href=""></div>
<div id="audio2" style="display:block;width:750px;height:30px;visibility:hidden" href=""></div>
<div id="audio3" style="display:block;width:750px;height:30px;visibility:hidden" href=""></div>

<div id="recent-layer" style="display: none">
  <div id="recent-holder">
    <h4><span>Recently Watched</span></h4>
    <img src="${nroot}/images/btn_winclose.png" id="btn-recent-close">
    <ul id="recent-list"></ul>
  </div>
</div>

<div id="signin-layer">
  <div id="signin-holder">
    <p id="btn-signin-close"><img src="${nroot}/images/btn_delete_off.png" class="off"><img src="${nroot}/images/btn_delete_on.png" class="on"></p>
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
        <!--li id="btn-forgot-pw"><span>Forgot your password?</span></li-->
        <!--li id="keep-signin"><p class="btn-check on"><img src="${nroot}/images/btn_check_on.png" class="btn-check-on"><img src="${nroot}/images/btn_check_off.png" class="btn-check-off"><span>Keep me sign in</span></p></li-->
        <li><p class="btn-hilite" id="btn-signin"><span>Sign in</span></p></li>
      </ul>
      <div id="entry-switcher">
        <p class="head"><span>New to 9x9?</span></p>
        <p class="content"><span>Tired of searching for videos? Access thousands of curated channels and create your very own programming guide!</span></p>
        <p class="content"><span>It's free and easy!</span></p>
        <p class="btn-hilite" id="btn-create-account"><span>Create an Account</span></p>
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
          <span>Password:</span>
          <p class="signin-input">
            <input type="password" class="textfield" id="signup-password">
          </p>
          <span class="hint">6-character minimum</span>
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
      </ul>
      <ul class="input-list-right">
        <li>
          <p><span>Gender:</span></p>
          <p class="radio-item on"><img src="${nroot}/images/btn_radio_off.png" class="btn-radio-off"><img src="${nroot}/images/btn_radio_on.png" class="btn-radio-on"><span>Male</span></p>
          <p class="radio-item"><img src="${nroot}/images/btn_radio_off.png" class="btn-radio-off"><img src="${nroot}/images/btn_radio_on.png" class="btn-radio-on"><span>Female</span></p>
        </li>
        <li id="birth-input">
          <span>Birth Year:</span>
          <p class="signin-input">
            <input type="text" class="textfield" value="Example: 1985" id="signup-birthyear">
          </p>
        </li>
        <li id="captcha-input">
          <span>Word Verification:</span>
          <p class="signin-input">
            <input type="text" class="textfield" value="Type the characters in the picture below" id="signup-captcha">
          </p>
          <p id="captcha"><img src="${nroot}/images/chptcha.gif"></p>
          <img src="${nroot}/images/btn_recaptcha.png" id="btn-recaptcha" title="Get a new challenge">
        </li>
        <li>
          <p class="term-text"><span>Clicking I accept means that you agree to the <a href="">9x9 service agreement</a> and <a href="">privacy statement</a>. You also agree to receive email from 9x9 with service updates, special offers, and survey invitations. You can unsubscribe at any time.</span></p>
        </li>
        <li><p class="btn-hilite" id="btn-signup"><span>I Accept</span></p></li>
      </ul>
    </div>   
  </div>
</div>

<div id="preload-control-images" style="display: none"></div>

<div id="delete-layer">
  <div class="delete-holder" id="delete-holder">
    <p id="step1"><span id="suredel">Are you sure you want to delete</span> "<span id="delete-title-1"></span>"?</p>
    <p id="step2"><span id="havedel">You have deleted channel</span> "<span id="delete-title-2"></span>".</p>
    <div class="actions"><a class="btn" id="btn-delYes" onclick="delete_enter(1)"><span>Yes</span></a>
    <a class="btn on" id="btn-delNo" onclick="delete_enter(2)"><span>No</span></a>
    <a class="btn on" id="btn-returnSG" onclick="delete_enter(1)"><span id="delrsg">Return to Smart Guide</span></a>
    <a class="btn" id="btn-delMore" onclick="delete_enter(2)"><span id="delmore">Delete More Channels</span></a></div>
  </div>
</div>

<div id="confirm-layer">
  <div id="confirm-holder">
    <p><span id="confirm-text"></span></p>
    <p class="btn" id="btn-confirm-close"><span>Close</span></p>
  </div>
</div>

<div id="alert-layer">
  <div id="alert-holder">
    <p><span>Are you still watching?</span></p>
    <ul class="action-list">
      <li>
        <p class="btn"><span>Keep watching</span></p>
      </li>
    </ul>
  </div>
</div>

<div id="waiting-layer">
  <div id="waiting-holder">
    <img src="${nroot}/images/loading.gif">
    <p>One moment...</p>
  </div>
</div>

<div id="buffering">
  <div class="waiting-holder">
    <img src="${root}/images/loading.gif">
    <p id="buffering1">Buffering...</p>
  </div>
</div>

<div id="dir-waiting">
  <div class="waiting-holder">
    <img src="${root}/images/loading.gif">
    <p id="moment2">One moment...</p>
  </div>
</div>

<div id="msg-layer" style="display: none">
  <div id="msg-holder">
    <p><span id="msg-text"></span></p>
  </div>
</div>

<div id="epend-layer" style="display: none">
  <div id="go-up">Press <span class="enlarge">&uarr;</span> to go to the IPG</div>
  <div id="go-down">Press <span class="enlarge">&darr;</span> to see all episodes</div>
  <div id="go-left"><img src="" id="left-tease">Press <span class="enlarge">&larr;</span> to watch previous channel</div>
  <div id="go-right"><img src="" id="right-tease">Press <span class="enlarge">&rarr;</span> to watch next channel</div>
</div>

<div id="success-layer">
  <div class="success-holder" id="success-holder">
    <p class="greeting"><span>Congratulations!</span></p>
    <p><span>You have just added a set of <span id="success-data">4 Da Ai</span> channels to your <strong>Smart Guide</strong>.<br>Your Smart Guide is the place for you to add more channels and personalize your channel line-up.</span></p>
    <ul class="action-list">
      <li><a class="btn on" id="success-goto"><span>Go to my Smart Guide</span></a></li>
    </ul>
  </div>
</div>

<div id="sg-bubble"><img src="${root}/images/bg_bubble.png"><div id="btn-bubble-del"><img src="${root}/images/btn_delete_off.png" class="off"><img src="${root}/images/btn_delete_on.png" class="on"></div><p><span id="rsbubble">Return to Smart Guide for more interesting content</span></p></div>

<!--div id="btn-subscribe" style="z-index: 300; display: none"><img src="${root}/images/btn_subscribe_off.png" class="off"><img src="${root}/images/btn_subscribe_on.png" class="on"></div-->

<div id="opening" style="display: block; z-index: 999">
  <div class="opening-holder" id="splash"></div>
</div>

<div id="yesno-layer">
  <div class="yesno-holder" id="yesno-holder">
    <p id="question"></p>
    <ul class="action-list">
      <li><a href="javascript:yn_enter(1)" class="btn" id="yesno-btn-yes"><span id="qyes">Yes</span></a></li>
      <li><a href="javascript:yn_enter(2)" class="btn" id="yesno-btn-no"><span id="qno">No</span></a></li>
    </ul>
  </div>
</div>

<div id="rename-layer">
  <div id="rename-holder">
    <p class="instruction"><span>You can change the name of this 3x3 channel set</span></p>
    <p id="rename-input">
      <input type="text" class="textfield" id="rename-field" value="Enter a new title for the set">
    </p>
    <ul class="action-list">
      <li><p class="btn disable" id="btn-rename-save"><span>Save</span></p></li>
      <li><p class="btn" id="btn-rename-cancel"><span>Cancel</span></p></li>
    </ul>
  </div>
</div>

<div id="tribtn-layer">
  <div id="tribtn-holder">
    <p><span id="addsucc">The channel you follow is added successfully!</span></p>
    <ul class="action-list">
      <li><p class="btn" id="btn-watchSet"><span>Watching this Set</span></p></li>
      <li><p class="btn" id="btn-toFset"><span>Back to Add Featured Sets</span></p></li>
      <li><p class="btn" id="btn-toSG"><span>Return to Smart Guide</span></p></li>
    </ul>
  </div>
</div>

<div id="email-layer">
  <div id="email-holder">
    <ul class="input-list">
      <li>
        <span>To:</span>
        <p id="email-input">
          <input type="text" class="textfield" value="Enter email address">
        </p>
      </li>
      <li>
        <span>Message:</span><span class="hint">(150 characters maximum)</span>
        <p id="msg-input">
          <textarea class="textfield">Enter message</textarea> 
        </p>
      </li>
      <li>
        <span>Word Verification:</span>
        <p id="email-captcha-input">
          <input type="text" class="textfield" value="Type the characters in the picture below">
        </p>
        <p id="email-captcha"><img></p>
        <img src="${nroot}/images/btn_recaptcha.png" id="btn-email-recaptcha" title="Get a new challenge">
      </li>
    </ul>
    <ul class="action-list">
      <li><p class="btn disable" id="btn-email-send"><span>Send</span></p></li>
      <li><p class="btn" id="btn-email-cancel"><span>Cancel</span></p></li>
    </ul>
  </div>
</div>

<div id="company-layer">
  <div id="company-holder">
    <p id="btn-company-close"><img src="${nroot}/images/btn_delete_off.png" class="off"><img src="${nroot}/images/btn_delete_on.png" class="on"></p>
    <ul id="company-tabs">
      <li id="about" class="on"><span>About Us</span></li>
      <li id="contact"><span>Contact Us</span></li>
      <li id="legal"><span>Legal</span></li>
    </ul>
    <div id="about-panel" class="input-panel">
      <div id="about-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
      <div id="about-content" class="constrain">
        <div id="about-list">
        </div>
      </div>
    </div>
    <div id="contact-panel" class="input-panel">
      <div id="contact-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
      <div id="contact-content" class="constrain">
        <div id="contact-list">
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

<div id="help-layer">
  <div id="help-holder">
    <p id="btn-help-close"><img src="${nroot}/images/btn_delete_off.png" class="off"><img src="${nroot}/images/btn_delete_on.png" class="on"></p>
    <ul id="help-tabs">
      <li id="tutorial" class="on"><span>New User Tutorial</span></li>
      <li id="faq"><span>FAQ</span></li>
      <li id="sync"><span>Diagnostics</span></li>
      <li id="report"><span>Report Problems</span></li>
    </ul>
    <div id="tutorial-panel" class="input-panel">
      <div id="tutorial-content" class="constrain">
        <div id="tutorial-list">
        </div>
      </div>
    </div>
    <div id="faq-panel" class="input-panel accordion">
      <div id="faq-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
      <div id="faq-content" class="constrain">
        <div id="faq-list">
        </div>
      </div>
    </div>
    <div id="sync-panel" class="input-panel accordion">
      <div id="sync-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
      <div id="sync-content" class="constrain">
        <div id="sync-list">
        </div>
      </div>
    </div>
    <div id="report-panel" class="input-panel">
      <div id="report-content" class="constrain">
        <p id="problem-input">
          <textarea class="textfield">Enter your problem</textarea> 
        </p>
        <p id="btn-report" class="btn"><span>Submit</span></p>
      </div>
    </div>
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

<div id="log-layer" style="position: absolute; left: 0; top: 0; height: 100%; width: 100%; background: white; color: black; text-align: left; padding: 20px; overflow: scroll; z-index: 9999; display: none"></div>

<div id="mask"></div>

<div id="fb-root"></div>

<div id="relaydiv" style="z-index: 1; position: absolute; top: 0px; left: 0px; width=500px; height=500px">
</div>

<!--/div-->

<div id="header">
  <img src="${nroot}/images/bg_header.gif" id="bg-header">
  <img src="${nroot}/images/logo.png" id="logo">
  <img src="${nroot}/images/tutorial_screenshot.jpg" style="display:none">
  <p id="slogan"><span>Your Personal Channel Browser</span></p>
  <ul id="nav">
    <li id="btn-account">
      <p><span class="head">Hi,</span><span id="user">Guest</span><img src="${nroot}/images/icon_expand.png" class="icon-expand"></p>
      <ul id="account-dropdown" class="dropdown">
      </ul>
    </li>
    <li class="divider"></li>
    <li id="store" class="main"><img src="${nroot}/images/icon_store.png" id="icon-store"><span>Store</span></li>
    <li class="divider"></li>
    <li id="guide" class="main"><img src="${nroot}/images/icon_guide.png" id="icon-guide"><span>Guide</span><span id="ch-sum"></span></li>
    <li class="divider"></li>
    <li id="player" class="main"><img src="${nroot}/images/icon_player.png" id="icon-player"><span>Player</span></li>
    <li class="divider"></li>
    <li id="curate" class="main"><img src="${nroot}/images/icon_curate.png" id="icon-curate"><span>Curate</span></li>
    <li class="divider" id="curate-divider"></li>
    <li id="help"><img src="${nroot}/images/icon_help.png" id="icon-help"><span>Help</span></li>
    <li class="divider"></li>
    <li id="syncstatus"><img src="${nroot}/images/icon_sync_on.png" id="icon-sync-on" title="with 9x9 sync"><img src="${nroot}/images/icon_sync_off.png" id="icon-sync-off" title="without 9x9 sync"></li>
  </ul>
</div>

<div id="setbubble">
  <img src="${nroot}/images/bubble_tip.png" id="setbubble-tip">
  <img src="${nroot}/images/bg_bubble.png" class="bg-bubble">
  <p id="setbubble-content">
    <span id="setbubble-title"></span>
    <span id="setbubble-description"></span>
  </p>
</div>

<div id="setbubbleclick">
  <div id="setbubbleclick-slider" class="slider-wrap"><div class="slider-vertical"></div></div> 
  <img src="${nroot}/images/add_bubble_tip_left.png" class="left-bubble-tip">
  <img src="${nroot}/images/add_bubble_tip_right.png" class="right-bubble-tip">
  <p id="btn-setbub-close"><img src="${nroot}/images/btn_delete_off.png" class="off"><img src="${nroot}/images/btn_delete_on.png" class="on"></p>
  <div id="setbubbleclick-content">
    <div class="setbubbleclick-category-content">
      <ul id="setbubbleclick-list" class="setbubbleclick-level3-list">
        <li>
          <span class="setbubbleclick-title">Top 10 TV Show of the World</span>
          <span class="setbubbleclick-description">Green sails in a television action movie serial shows</span>
          <span class="setbubbleclick-channel">20 channels</span>
        </li>
      </ul>
    </div>
  </div>
</div>

<div id="add-bubble">
  <img src="${nroot}/images/add_bubble_tip.png" id="add-bubble-tip">
  <img src="${nroot}/images/bg_add_bubble.png" class="bg-add-bubble">
  <p id="addbubble-content">
    <span>Add channels to your personal Guide to receive the latest updates, share on<br><img src="${nroot}/images/icon_fb.png" id="icon-fb">Facebook or Email, watch it in full-screen, and more!</span>
  </p>
</div>

<div id="store-layer" class="stage" style="display: block; background: #f0f0f0">
  <div id="store-holder">
    <div id="pool-waiting">
      <img src="${nroot}/images/loading.gif">
      <p><span>One moment...</span></p>
    </div>
    <div id="preview-waiting">
      <img src="${nroot}/images/loading.gif">
      <p><span>One moment...</span></p>
    </div>
    <h2><span>Channel Store</span></h2>
    <ul id="programlang">
      <li class="on"><span>English Channels</span></li>
      <li><span>中文頻道</span></li>
    </ul>
    <!--img src="${nroot}/images/icon_cart_gray.png" id="btn-cart">
    <p id="cart-bubble">
      <img src="${nroot}/images/cart_bubble.png">
      <span>0</span>
    </p-->
    <!--div id="btn-programlang">
      <p id="selected-programlang"><span>English Channels</span><img src="${nroot}/images/icon_expand.png" class="icon-expand"></p>
      <ul id="programlang-dropdown" class="dropdown">
        <li class="on"><img src="${nroot}/images/icon_check.png" class="icon-check"><span>English Channels</span></li>
        <li><img src="${nroot}/images/icon_check.png" class="icon-check"><span>中文頻道</span></li>
      </ul>
    </div-->
    <p id="search-input">
      <img src="${nroot}/images/icon_search.png" class="icon-search">
      <input type="text" class="textfield" value="Search" id="search-field">
    </p>
    <div id="preview-area">
      <ul id="flip-ch-index"></ul>
      <div id="flip-bubble-constrain"></div>
      <p id="btn-flip-back"><img src="${nroot}/images/btn_flip_back.png" id="icon-fp-back"></p>
      <p id="btn-flip-preview">
        <img src="${nroot}/images/btn_flip_bg.png" id="btn-fp-bg"> 
        <img src="${nroot}/images/btn_flip_L_off.png" id="btn-fp-off"> 
        <img src="${nroot}/images/btn_flip_L_on.png" id="btn-fp-on">
        <img src="${nroot}/images/btn_flip_L_disable.png" id="btn-fp-disable">
      </p>

      <div id="preview-win">
        <div id="preview-win-top-bar">
           <p id="preview-index"><span class="head">Previewing Channel:</span><span id="index-ch-title">${crawlChannelTitle}</span></p>
        </div>
        <div id="preview-video-bg">
           <img id="preview-video" src="${crawlVideoThumb}" height="100%" width="100%">
        </div>
        <p id="preview-index-meta">
          <span id="meta-set-title">${crawlSetTitle}</span><span class="divider">&raquo;</span><span id="meta-ch-title">${crawlChannelTitle}</span><span class="divider">&raquo;</span><span id="meta-ep-title">${crawlEpisodeTitle}</span>
        </p>
        <p id="ep-switcher" class="on">
          <img src="${nroot}/images/icon_ep.png" id="btn-ep">
          <span id="ep-show">Show episodes</span>
        </p>
        <div id="preview-ep" style="display: block">
          <img src="${nroot}/images/arrow_right_on.png" id="preview-arrow-right">
          <img src="${nroot}/images/arrow_left_on.png" id="preview-arrow-left">
          <ul id="preview-ep-list"><li id="sp-1" class="on"><img src="${crawlEpThumb1}" class="thumbnail"><p class="duration"><span></span></p></li><li id="sp-2"><img src="${crawlEpThumb2}" class="thumbnail"><p class="duration"><span></span></p></li><li id="sp-3"><img src="${crawlEpThumb3}" class="thumbnail"><p class="duration"><span></span></p></li>"</ul>
        </div>
        <div id="preview-controller">
          <ul id="control-list">
            <li id="btn-info"><img src="${nroot}/images/btn_info.png" id="icon-info" title="Channel Info"></li>
            <li id="btn-preview-play" class="on"><img src="${nroot}/images/btn_play.png" id="icon-preview-play" title="Play"><img src="${nroot}/images/btn_pause.png" id="icon-preview-pause" title="Pause"></li>
            <li id="btn-flip-prev"><img src="${nroot}/images/btn_flipup.png" id="icon-flip-prev" title="Prev Channel"></li>
            <li id="btn-flip-next"><img src="${nroot}/images/btn_flipdown.png" id="icon-flip-next" title="Next Channel"></li>
            <li id="btn-preview-volume-down"><img src="${nroot}/images/btn_volume_down.png" title="Volume Down"></li>
            <li id="preview-volume-constrain">
              <ul id="preview-volume-bars"><li class="on"></li><li class="on"></li><li class="on"></li><li class="on"></li><li></li><li></li><li></li></ul>
            </li>
            <li id="btn-preview-volume-up"><img src="${nroot}/images/btn_volume_up.png" title="Volume Up"></li>
          </ul>
        </div>
      </div>
      <p id="channel-price"><img src="${nroot}/images/tag_free.png" id="icon-tag"><span class="free">FREE</span></p>
      <!--div id="preview-controller">
        <ul id="control-list">
          <li id="btn-info"><img src="${nroot}/images/btn_info.png" id="icon-info" title="Channel Info"></li>
          <li id="btn-preview-play" class="on"><img src="${nroot}/images/btn_play.png" id="icon-preview-play" title="Play"><img src="${nroot}/images/btn_pause.png" id="icon-preview-pause" title="Pause"></li>
          <li id="btn-flip-prev"><img src="${nroot}/images/btn_flipup.png" id="icon-flip-prev" title="Prev Channel"></li>
          <li id="btn-flip-next"><img src="${nroot}/images/btn_flipdown.png" id="icon-flip-next" title="Next Channel"></li>
        </ul>
      </div-->
      <div id="preview-promo">
        <p><span>Flip through the channels and add your favorites to your Guide</span></p>
      </div>
      <div id="channel-bubble">
        <div id="chbubble-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
        <div id="chbubble-content">
          <div id="chbubble-list">
            <p><span></span></p>
            <p id="chbubble-meta"><span></span><span class="divider">|</span><span></span><br><span id="chbubble-curator"></span></p>
          </div>
        </div>
      </div>
      <div id="btn-add-ch-L" class="btn-hilite"><img src="${nroot}/images/icon_cart_white.png" class="icon-cart"><p class="btn-text"><span>Add this Channel</span></p></div>
    </div>    
    <ul id="tabs">
      <li id="recommended" class="on"><span>Recommended sets</span></li>
      <li id="category"><span>Directory</span></li>
      <li id="yourown"><span>Add your own</span></li>
    </ul>
    <div id="channel-pool">
      <div id="recommended-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
      <div id="recommended-content" class="tab-content">
        <ul id="recommended-list">
          <li id="rec-1" class="on"><div class="set-icon"><p class="bg-set"><img src="${nroot}/images/bg_set_off.png" class="bg-set-off"><img src="${nroot}/images/bg_set_on.png" class="bg-set-on"></p><img src="${crawlRecommendThumb1}" class="thumbnail"><img src="${nroot}/images/set_corner.png" class="set-corner"><img src="${nroot}/images/set_front.png" class="set-front"><img src="${nroot}/images/set_back.png" class="set-back"></div><p class="set-title"><span>${crawlRecommendTitle1}</span></p><p class="set-description"><span>${crawlRecommendDesc1}</span></p><p class="channel-number"><span style="color: rgb(165, 42, 42); ">1 of ${crawlRecommendCount1}  channels</span></p><p id="flashing" class="on"></p></li>
          <li id="rec-2" class="off"><div class="set-icon"><p class="bg-set"><img src="${nroot}/images/bg_set_off.png" class="bg-set-off"><img src="${nroot}/images/bg_set_on.png" class="bg-set-on"></p><img src="${crawlRecommendThumb2}" class="thumbnail"><img src="${nroot}/images/set_corner.png" class="set-corner"><img src="${nroot}/images/set_front.png" class="set-front"><img src="${nroot}/images/set_back.png" class="set-back"></div><p class="set-title"><span>${crawlRecommendTitle2}</span></p><p class="set-description"><span>${crawlRecommendDesc2}</span></p><p class="channel-number"><span style="color: orange">${crawlRecommendCount2} channels</span></p></li>
          <li id="rec-3" class="off"><div class="set-icon"><p class="bg-set"><img src="${nroot}/images/bg_set_off.png" class="bg-set-off"><img src="${nroot}/images/bg_set_on.png" class="bg-set-on"></p><img src="${crawlRecommendThumb3}" class="thumbnail"><img src="${nroot}/images/set_corner.png" class="set-corner"><img src="${nroot}/images/set_front.png" class="set-front"><img src="${nroot}/images/set_back.png" class="set-back"></div><p class="set-title"><span>${crawlRecommendTitle1}</span></p><p class="set-description"><span>${crawlRecommendDesc3}</span></p><p class="channel-number"><span style="color: orange">${crawlRecommendCount3} channels</span></p></li>
          <li id="rec-4" class="off"><div class="set-icon"><p class="bg-set"><img src="${nroot}/images/bg_set_off.png" class="bg-set-off"><img src="${nroot}/images/bg_set_on.png" class="bg-set-on"></p><img src="${crawlRecommendThumb4}" class="thumbnail"><img src="${nroot}/images/set_corner.png" class="set-corner"><img src="${nroot}/images/set_front.png" class="set-front"><img src="${nroot}/images/set_back.png" class="set-back"></div><p class="set-title"><span>${crawlRecommendTitle1}</span></p><p class="set-description"><span>${crawlRecommendDesc4}</span></p><p class="channel-number"><span style="color: orange">${crawlRecommendCount4} channels</span></p></li>
          <li id="rec-5" class="off"><div class="set-icon"><p class="bg-set"><img src="${nroot}/images/bg_set_off.png" class="bg-set-off"><img src="${nroot}/images/bg_set_on.png" class="bg-set-on"></p><img src="${crawlRecommendThumb5}" class="thumbnail"><img src="${nroot}/images/set_corner.png" class="set-corner"><img src="${nroot}/images/set_front.png" class="set-front"><img src="${nroot}/images/set_back.png" class="set-back"></div><p class="set-title"><span>${crawlRecommendTitle1}</span></p><p class="set-description"><span>${crawlRecommendDesc5}</span></p><p class="channel-number"><span style="color: orange">${crawlRecommendCount5} channels</span></p></li>
          <li id="rec-6" class="off"><div class="set-icon"><p class="bg-set"><img src="${nroot}/images/bg_set_off.png" class="bg-set-off"><img src="${nroot}/images/bg_set_on.png" class="bg-set-on"></p><img src="${crawlRecommendThumb6}" class="thumbnail"><img src="${nroot}/images/set_corner.png" class="set-corner"><img src="${nroot}/images/set_front.png" class="set-front"><img src="${nroot}/images/set_back.png" class="set-back"></div><p class="set-title"><span>${crawlRecommendTitle1}</span></p><p class="set-description"><span>${crawlRecommendDesc6}</span></p><p class="channel-number"><span style="color: orange">${crawlRecommendCount6} channels</span></p></li>
          <li id="rec-7" class="off"><div class="set-icon"><p class="bg-set"><img src="${nroot}/images/bg_set_off.png" class="bg-set-off"><img src="${nroot}/images/bg_set_on.png" class="bg-set-on"></p><img src="${crawlRecommendThumb7}" class="thumbnail"><img src="${nroot}/images/set_corner.png" class="set-corner"><img src="${nroot}/images/set_front.png" class="set-front"><img src="${nroot}/images/set_back.png" class="set-back"></div><p class="set-title"><span>${crawlRecommendTitle1}</span></p><p class="set-description"><span>${crawlRecommendDesc7}</span></p><p class="channel-number"><span style="color: orange">${crawlRecommendCount7} channels</span></p></li>
          <li id="rec-8" class="off"><div class="set-icon"><p class="bg-set"><img src="${nroot}/images/bg_set_off.png" class="bg-set-off"><img src="${nroot}/images/bg_set_on.png" class="bg-set-on"></p><img src="${crawlRecommendThumb8}" class="thumbnail"><img src="${nroot}/images/set_corner.png" class="set-corner"><img src="${nroot}/images/set_front.png" class="set-front"><img src="${nroot}/images/set_back.png" class="set-back"></div><p class="set-title"><span>${crawlRecommendTitle1}</span></p><p class="set-description"><span>${crawlRecommendDesc8}</span></p><p class="channel-number"><span style="color: orange">${crawlRecommendCount8} channels</span></p></li>
          <li id="rec-9" class="off"><div class="set-icon"><p class="bg-set"><img src="${nroot}/images/bg_set_off.png" class="bg-set-off"><img src="${nroot}/images/bg_set_on.png" class="bg-set-on"></p><img src="${crawlRecommendThumb9}" class="thumbnail"><img src="${nroot}/images/set_corner.png" class="set-corner"><img src="${nroot}/images/set_front.png" class="set-front"><img src="${nroot}/images/set_back.png" class="set-back"></div><p class="set-title"><span>${crawlRecommendTitle1}</span></p><p class="set-description"><span>${crawlRecommendDesc9}</span></p><p class="channel-number"><span style="color: orange">${crawlRecommendCount9} channels</span></p></li>
        </ul>
      </div>
      <div id="category-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
      <div id="category-content" class="tab-content">
        <ul class="level1-list">
        </ul>
      </div>      
      <div id="yourown-content" class="tab-content">
        <h3><span>Add YouTube Channels</span></h3>
        <p id="yt-intro"><span>Enter YouTube Channel or Playlist URL:</span></p>
        <div id="yt-input">
          <input name="" type="text" class="textfield" value="http://www.youtube.com/user/">
        </div>
        <p id="yt-note"><span>* We don't accept URLs for single video</span></p>
        <div id="btn-add-yt" class="btn-hilite"><img src="${nroot}/images/icon_cart_white.png" class="icon-cart"><p class="btn-text"><span>Add this Channel</span></p></div>
      </div>
    </div>
      <div id="search-layer">
      <p id="btn-search-close"><img src="${nroot}/images/btn_delete_off.png" class="off"><img src="${nroot}/images/btn_delete_on.png" class="on"></p>
      <h4><span>Search Result</span></h4>
     <p id="result-head"></p>
      <div id="search-slider" class="slider-wrap"><div class="slider-vertical"></div></div>
      <div id="search-content">
        <div id="no-result">
          <img src="${nroot}/images/sign_alert.png" id="sign-alert">
          <p class="alert-msg"><span>Your search did not match any channels.</span></p>
          <p class="head"><span>Suggestions:</span></p>
          <p class="suggestions"><span>Please try different keywords<br>Broaden your search terms</span></p>
        </div>
        <ul id="search-list">
        </ul>
      </div>
    </div>
  </div>
</div>

<div id="hint-layer">
  <div id="hint-holder">
    <img src="${nroot}/images/hint_1.png" id="hint-1"><img src="${nroot}/images/hint_2.png" id="hint-2"><img src="${nroot}/images/hint_3.png" id="hint-3">
  </div>
</div>

<div id="hint-bubble">
  <img src="${nroot}/images/bubble_tip_up.png" id="hint-bubble-tip">
  <img src="${nroot}/images/bg_bubble_up.png" class="bg-bubble">
  <div id="hint-bubble-content">
    <p class="head"><span>New to 9x9?</span></p>
    <p><span>Click <span class="link">Here</span> to take a quick tutorial!</span></p>
    <img src="${nroot}/images/tutorial_screenshot.jpg" id="tutorial-screenshot">
  </div>
</div>

<div id="guide-layer" class="stage" style="display: none; background: #f0f0f0">
  <div id="guide-holder">
    <h2><span>9x9 Guide</span></h2>
        <div id="guide-content">
      <img src="${nroot}/images/bg_player.jpg" id="guide-bg">
      <p id="btn-guide2store"><span>&lt; Back to Store</span></p>
      <p id="guide-index"><span class="occupied">13/81</span><span>channels in your 9x9 Guide</span></p>
      <p id="guide-tip"><span class="head">Tips:</span><br><span>Drag and drop channel icons to arrange channels</span></p>
      <div id="grid">
      </div>
      <div id="channel-info">
        <p class="head"><span>Current Channel:</span></p>
        <p class="ch-title"><span>Jazz</span></p>
        <p class="ch-epNum"><span>Episode:</span><span class="amount">12</span></p>
        <p class="ch-updated"><span>Updated:</span><span class="date new">Today</span></p>
        <p class="ch-description ellipsis multiline"><span>Last installment from my "Making Money from Podcasting" series...</span></p>
      </div>
      
      <ul id="guide-sync">
        <li id="btn-guide-sync">
          <img src="${nroot}/images/btn_sync.png">
          <ul id="guide-sync-dropdown" class="dropdown">
            <li class="on"><img src="${nroot}/images/icon_check.png" class="icon-check"><span>Google TV</span></li>
            <li><img src="${nroot}/images/icon_check.png" class="icon-check"><span>Google TV2</span></li>
          </ul>
        </li>
      </ul>
    </div>
  </div>
</div>

<div id="player-layer" class="stage" style="display: none; background: #f0f0f0">
  <div id="player-video"></div>
  <ul id="control-bar">
    <li id="btn-play"><img src="${nroot}/images/btn_play.png" title="Play"></li>
    <li id="btn-pause"><img src="${nroot}/images/btn_pause.png" title="Pause"></li>
    <li class="divider"></li>
    <li id="play-time"><span></span></li>
    <li class="divider"></li>
    <li id="progress-constrain">
      <!--p id="btn-knob" style="display: none"></p-->
      <div id="progress-bar">
        <p id="loaded"></p>
        <p id="played"></p>
      </div>
    </li>
    <li class="divider"></li>
    <li id="btn-volume-down"><img src="${nroot}/images/btn_volume_down.png" title="Volume Down"></li>
    <li id="volume-constrain">
      <ul id="volume-bars"><li class="on"></li><li class="on"></li><li class="on"></li><li class="on"></li><li></li><li></li><li></li></ul>
    </li>
    <li id="btn-volume-up"><img src="${nroot}/images/btn_volume_up.png" title="Volume Up"></li>
    <li class="divider"></li>
    <li id="btn-full" class="right"><img src="${nroot}/images/btn_full.png" title="Full Screen"></li>
    <li id="btn-shrink" class="right"><img src="${nroot}/images/btn_shrink.png" title="Exit Full Screen"></li>


    <li id="btn-sync" class="right">
      <img src="${nroot}/images/btn_sync.png" title="9x9 Sync">
      <ul id="sync-dropdown" class="dropdown">
      </ul>
    </li>
    <li id="btn-share" class="right">
      <img src="${nroot}/images/btn_share.png" title="Share">
      <ul id="share-dropdown" class="dropdown">
        <li class="combo">
          <span>Channel URL:</span>
          <p id="share-input"><input type="text" class="textfield" value=""></p>
        </li>
        <li><img src="${nroot}/images/icon_mail.png" class="icon-mail"><span>Send by email</span></li>
        <li><img src="${nroot}/images/icon_fb.png" class="icon-fb"><span>Share to facebook</span></li>
      </ul>
    </li>
    <li id="btn-sort" class="right">
      <img src="${nroot}/images/btn_sort.png" title="Sort">
      <ul id="sort-dropdown" class="dropdown">
        <li class="head"><span>Episodes sort by:</span></li>
        <li class="on"><img src="${nroot}/images/icon_check.png" class="icon-check"><span>From Newest to Oldest</span></li>
        <li><img src="${nroot}/images/icon_check.png" class="icon-check"><span>From Left off to Newest</span></li>
      </ul>
    </li>
    <li id="btn-rez" class="right">
      <span id="selected-rez">360p</span>
      <ul id="rez-dropdown" class="dropdown">
        <li><img src="${nroot}/images/icon_check.png" class="icon-check"><span>720p</span></li>
        <li class="on"><img src="${nroot}/images/icon_check.png" class="icon-check"><span>480p</span></li>
        <li><img src="${nroot}/images/icon_check.png" class="icon-check"><span>360p</span></li>
        <li><img src="${nroot}/images/icon_check.png" class="icon-check"><span>240p</span></li>
      </ul>
    </li>
  </ul>
  <div id="player-holder">
    <h2><span>9x9 Player</span></h2>
    <div id="player-content">
      <p id="btn-flip-play">
        <img src="${nroot}/images/btn_flip_bg.png" id="btn-fpl-bg"> 
        <img src="${nroot}/images/btn_flip_L_off.png" id="btn-fpl-off"> 
        <img src="${nroot}/images/btn_flip_L_on.png" id="btn-fpl-on">
      </p>
      <img src="${nroot}/images/bg_player.jpg" id="player-bg">
      <p id="btn-player2store"><span>< Back to Store</span></p>
      <p id="player-index"><span>You are watching</span><span class="ch-title"></span><span>channel</span></p>
      <img src="${nroot}/images/player_ep_panel.png" id="player-ep-panel">
      <img src="${nroot}/images/arrow_right_on.png" id="player-arrow-right">
      <img src="${nroot}/images/arrow_left_on.png" id="player-arrow-left">
      <ul id="player-ep-list"></ul>-
      <p id="player-ep-meta">
        <span class="meta-head">Episode:</span>
        <span class="ep-title"></span>
        <span class="amount"></span>
        <span class="age">- Today</span>
      </p>
      <div id="player-ch-flipper">
        <div id="play-next">
          <img src="${nroot}/images/arrow_down.png" id="arrow-next">
          <p class="tooltip"><span>Next Ch</span></p>
          <img src="" class="thumbnail">
          <div id="next-title" class="ellipsis multiline"><p class="txt-holder"><span></span></p></div>
        </div>
        <div id="play-prev">
          <img src="${nroot}/images/arrow_up.png" id="arrow-prev">
          <p class="tooltip"><span>Prev Ch</span></p>
          <img src="" class="thumbnail">
          <div id="prev-title" class="ellipsis multiline"><p class="txt-holder"><span></span></p></div>
        </div>
        <p id="play-ch-index"><span></span></p>
      </div>
      <div id="player-info">
        <div id="ep-description" class="ellipsis multiline">
          <p class="head"><span>Episode Description</span></p>
          <p class="content"><span></span></p>
        </div>
        <div id="comment" class="ellipsis multiline">
          <p class="head"><span>Curator's Comment</span></p>
          <p class="content"><span></span></p>
        </div>
        <div id="ep-uploaded">
          <p class="head"><span>Uploaded:</span></p>
          <p class="content"><span></span></p></div></div>
      </div>
    </div>
  </div>
</div>

<div id="settings-layer">
  <div id="settings-holder">
    <p id="btn-settings-close"><img src="${nroot}/images/btn_delete_off.png" class="off"><img src="${nroot}/images/btn_delete_on.png" class="on"></p>
    <ul id="settings-tabs">
      <li id="general" class="on"><span>General</span></li>
      <!--li id="sharing"><span>Sharing</span></li-->
      <li id="preload"><span>Preload</span></li>
      <li id="resolution"><span>Resolution</span></li>
    </ul>
    <div id="general-panel" class="input-panel">
      <ul class="input-list">
        <li>
          <p class="head"><span>Name:</span></p>
          <p id="reset-name" class="editable"><span>John Smith</span></p>
          <div id="reset-name-input" class="setting-block">
            <p class="settings-input"><input type="text" class="textfield" value="John Smith"></p>
          </div>
        </li>
        <li>
          <p class="head"><span>Email:</span></p>
          <p id="reset-email" class="fixed"><span>john@9x9.tv</span></p>
        </li>
        <li>
          <p class="head"><span>Password:</span></p>
          <p id="reset-pw" class="editable"><span>*******</span></p>
          <div id="reset-pw-input" class="setting-block">
            <p class="settings-input"><input type="text" class="textfield" value="Enter your old password"></p>
            <p class="settings-input"><input type="text" class="textfield" value="Enter a new password"></p>
            <p class="settings-input"><input type="text" class="textfield" value="Re-enter the new password"></p>
          </div>
        </li>
        <li>
          <p class="head"><span>Gender:</span></p>
          <p id="reset-gender" class="editable"><span>Male</span></p>
          <div id="reset-gender-input" class="setting-block">
            <p class="radio-item on"><img src="${nroot}/images/btn_radio_off.png" class="btn-radio-off"><img src="${nroot}/images/btn_radio_on.png" class="btn-radio-on"><span>Male</span></p>
            <p class="radio-item"><img src="${nroot}/images/btn_radio_off.png" class="btn-radio-off"><img src="${nroot}/images/btn_radio_on.png" class="btn-radio-on"><span>Female</span></p>
          </div>
        </li>
        <li>
          <p class="head"><span>Birth Year:</span></p>
          <p id="reset-birth" class="editable"><span>1980</span></p>
          <div id="reset-birth-input" class="setting-block">
            <p class="settings-input"><input type="text" class="textfield" value="1980"></p>
          </div>
        </li>
        <li>
          <p class="head"><span>Language:</span></p>
          <div id="btn-language" class="droppable">
            <p id="selected-language"><span>English</span></p>
            <ul id="language-dropdown" class="dropdown">
              <li class="on"><img src="${nroot}/images/icon_check.png" class="icon-check"><span>English</span></li>
              <li><img src="${nroot}/images/icon_check.png" class="icon-check"><span>中文</span></li>
            </ul>
          </div>
        </li>
        <li>
          <p class="head"><span>Region:</span></p>
          <div id="btn-region" class="droppable">
            <p id="selected-region"><span>English Channels</span></p>
            <ul id="region-dropdown" class="dropdown">
              <li><img src="${nroot}/images/icon_check.png" class="icon-check"><span>English Channels</span></li>
              <li><img src="${nroot}/images/icon_check.png" class="icon-check"><span>中文頻道</span></li>
            </ul>
          </div>
        </li>
      </ul>
      <ul class="action-list">
        <li><p id="btn-general-save" class="btn"><span>Save</span></p></li>
      </ul>
    </div>
    <div id="sharing-panel" class="input-panel">
      <ul class="input-list">
        <li>
          <p class="head"><span>Connect Your 9x9 Account with:</span></p>
          <p class="btn-check on"><img src="${nroot}/images/btn_check_on.png" class="btn-check-on"><img src="${nroot}/images/btn_check_off.png" class="btn-check-off"><span>Facebook</span></p>  
          <p id="btn-connect-fb" class="btn"><span>Connect with Facebook</span></p>
        </li>
        <!--li>
          <p class="head"><span>Auto share on My Facebook Wall when:</span></p>
          <p class="btn-check on"><img src="${nroot}/images/btn_check_on.png" class="btn-check-on"><img src="${nroot}/images/btn_check_off.png" class="btn-check-off"><span>I add a new channel to my guide</span></p>
        </li-->
      </ul>
      <ul class="action-list">
        <li><p id="btn-sharing-save" class="btn"><span>Save</span></p></li>
      </ul>
    </div>
    <div id="preload-panel" class="input-panel">
      <ul class="input-list">
        <li>
          <p class="head"><span>I want to set 9x9's preloading capacity to:</span></p>
          <p class="radio-item" id="preload-off"><img src="${nroot}/images/btn_radio_off.png" class="btn-radio-off"><img src="${nroot}/images/btn_radio_on.png" class="btn-radio-on"><span>Off</span><span class="explanation">Disable preloading of videos</span></p>
          <p class="radio-item" id="preload-on"><img src="${nroot}/images/btn_radio_off.png" class="btn-radio-off"><img src="${nroot}/images/btn_radio_on.png" class="btn-radio-on"><span>On</span><span class="explanation">Enable preloading of videos</span></p>
        </li>
      </ul>
      <ul class="action-list">
        <li><p id="btn-preload-save" class="btn"><span>Save</span></p></li>
      </ul>
    </div>
    <div id="resolution-panel" class="input-panel">
      <ul class="input-list">
        <li>
          <p class="head"><span>Set 9x9's default playback resolution at:</span></p>
          <p class="radio-item" id="rez-hd1080"><img src="${nroot}/images/btn_radio_off.png" class="btn-radio-off"><img src="${nroot}/images/btn_radio_on.png" class="btn-radio-on"><span>1080p</span></p>
          <p class="radio-item" id="rez-hd720"><img src="${nroot}/images/btn_radio_off.png" class="btn-radio-off"><img src="${nroot}/images/btn_radio_on.png" class="btn-radio-on"><span>720p</span></p>
          <p class="radio-item" id="rez-large"><img src="${nroot}/images/btn_radio_off.png" class="btn-radio-off"><img src="${nroot}/images/btn_radio_on.png" class="btn-radio-on"><span>large (480px)</span></p>
          <p class="radio-item" id="rez-medium"><img src="${nroot}/images/btn_radio_off.png" class="btn-radio-off"><img src="${nroot}/images/btn_radio_on.png" class="btn-radio-on"><span>medium (360px)</span></p>
          <p class="radio-item" id="rez-small"><img src="${nroot}/images/btn_radio_off.png" class="btn-radio-off"><img src="${nroot}/images/btn_radio_on.png" class="btn-radio-on"><span>small (240px)</span></p>
        </li>
      </ul>
      <ul class="action-list">
        <li><p id="btn-resolution-save" class="btn"><span>Save</span></p></li>
      </ul>
    </div>
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

<div id="footer">
  <ul id="footer-list">
    <li id="btn-company"><span>Company</span></li>
    <li class="divider"></li>
    <li id="btn-blog"><a href="http://blog.9x9.tv/"><span>Blog</span></a></li>
    <li class="divider"></li>
    <li id="btn-forum"><a href="http://forum.9x9.tv/"><span>Forum</span></a></li>
    <li class="divider"></li>
    <li id="btn-sitelang">
      <p id="selected-sitelang"><span>English Site</span><img src="${nroot}/images/icon_expand.png" class="icon-expand"></p>
      <ul id="sitelang-dropdown" class="dropdown">
        <li class="on"><img src="${nroot}/images/icon_check.png" class="icon-check"><span>English Site</span></li>
        <li><img src="${nroot}/images/icon_check.png" class="icon-check"><span>中文網站</span></li>
      </ul>
    </li>
  </ul>
  <p id="copyright"><span>&copy; 2012 9x9.tv.  All rights reserved</span></p>
</div>

</body>
</html>
